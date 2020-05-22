package com.example.notificationtest.biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.notificationtest.httplib.HiCallBack;
import com.example.notificationtest.httplib.HiHttp;
import com.example.notificationtest.httplib.Request;
import com.example.notificationtest.httplib.Response;
import com.example.notificationtest.httplib.sign.SignatureUtils;

/**
 * 设备信息业务逻辑 第一种情况：本地没有存储imei 1：系统API获取imei，并存储 2：系统API无法获取，通过信通院sdk获取oaid:
 * a：获取不到oaid，向服务器获取生成的imei，并保存 b：获取到oaid，根据oaid调用接口反查imei: 1：获取到，存储imei，oaid
 * 2：获取不到，向服务器获取生成的imei，保存imei，oaid，并上报服务器。
 * <p>
 * 第二种情况：本地有imei，且是Android Q及以上，通过信通院sdk获取oaid:
 * 1：本地没有存储的oaid，保存获取的oaid，上报imei，oaid到服务器
 * 2：本地有存储的oadi，如果和获取的不一致，替换保存最新的，上报imei，oaid到服务器
 */
public enum LeDeviceBiz {

	INIT;

	public static final String ZUI = "com.zui.browser";
	public static final String LENOVO = "com.lenovo.browser";

	public static final String ACTION_OAID = "lenovo.intent.action.BROWSER_OAID";

	public static final String TAG = "LeDeviceBiz";
	/**
	 * sp 存储文件名
	 */
	public static final String SP_FILE_NAME = "device_id_file";
	/**
	 * imei 存储key
	 */
	public static final String SP_KEY_IMEI = "sp_device_imei";
	/**
	 * imei2 存储key
	 */
	public static final String SP_KEY_IMEI2 = "sp_device_imei2";
	/**
	 * oaid 存储key
	 */
	public static final String SP_KEY_OAID = "sp_device_oaid";

	private String imei2;
	private String imeiID = "";
	private String imeiID2 = "";

	/** 标记当前获取imei流程 */
	public volatile AtomicBoolean isGetting;

	/** 当前获取imei流程是否正在执行 */
	public boolean isGettingImei() {
		if (isGetting != null) {
			return isGetting.get();
		}
		return false;
	}

	/**
	 * 初始化设备id 结合imei和oaid模式初始化
	 */
	public void initDeviceId(final Activity context) {
		isGetting = new AtomicBoolean(true);
		imei2 = readImei2();
		String imei = readImei();
		if (TextUtils.isEmpty(imei)) {// 本地没有imei
			Log.i(LeDeviceBiz.TAG, "There is no xxxx locally, and the system method gets it");
			getDeviceId(context, new ReprotListentr() {// 系统方法获取imei
				public void onCallBack(String backImei, String imei2) {// 获取到imei
					saveImei(backImei);// 保存本地
					saveImei2(imei2);
					init(LenovoGameApi.mApp, backImei, imei2);// 初始化Avator
					Log.i(LeDeviceBiz.TAG,
							"There is no xxxx locally. The system method successfully obtains xxxx and initializes the Avator");
				}

				public void onNOData() {// 没有获取到imei
					runRemoteOaid(context.getApplication());
					Log.i(LeDeviceBiz.TAG,
							"There is no xxxx locally, and the system method cannot obtain it. The remote end is requested to execute the xxxx logic");
				}
			});
		} else {// 本地有imei
			init(LenovoGameApi.mApp, imei, imei2);// 初始化Avator
			Log.i(LeDeviceBiz.TAG, "There is xxxx locally, initializing the Avator");
			if (Build.VERSION.SDK_INT >= 28) {// android Q 及以上系统，获取oaid且保存
				runRemoteOaid(context.getApplication());
				Log.i(LeDeviceBiz.TAG, "Local xxxx, request the remote end to execute xxxx logic");
			}
		}
	}

	/**
	 * 启动远端进程去获取oaid
	 */
	private void runRemoteOaid(Application application) {
		// 启动remote服务触发
//		JLibrary.InitEntry(application);
//		MdidSdkHelper.InitSdk(application, true, new IIdentifierListener() {
//			@Override
//			public void OnSupport(boolean arg0, IdSupplier supplier) {
//				String oaid = supplier.getOAID();
//				if (!TextUtils.isEmpty(oaid) && !TextUtils.equals(oaid, "unsupport")) {
//					doRemoteOaidResult(oaid);
//				}
//			}
//		});
	}

	/**
	 * 处理获取oaid后的逻辑业务
	 */
	public void doRemoteOaidResult(String res) {
		Log.i(LeDeviceBiz.TAG, "Get the oaid and return the result :" + res);
		String imei = LeDeviceBiz.INIT.readImei();
		String imei2 = LeDeviceBiz.INIT.readImei2();
		if (TextUtils.isEmpty(imei)) {// 本地没有imei
			if (!TextUtils.isEmpty(res)) {
				// 保存oaid
				LeDeviceBiz.INIT.saveOaid(res);
				// 通过oaid向服务器反查imei，并保存AnalyticsTracker
				LeDeviceBiz.INIT.getImeiByOaid(res);
				Log.i(LeDeviceBiz.TAG,
						"There is no xxxx locally. The remote end requests oaid, saves oaid, and starts contrast checking xxxx");
			} else {// 获取不到oaid，从服务器随机生成一个随机的imei
				Log.i(LeDeviceBiz.TAG,
						"There is no xxxx locally, the remote request cannot obtain oaid, and a random xxxx is generated randomly from the server");
				LeDeviceBiz.INIT.getRandomID(new ReprotListentr() {
					public void onCallBack(String backImei, String imei2) {
						LeDeviceBiz.INIT.saveImei(backImei);// 保存获取的imei
						init(LenovoGameApi.mApp, backImei, imei2);// 初始化Avator
						Log.i(LeDeviceBiz.TAG,
								"There was no local xxxx, and the remote request could not obtain oaid. A random xxxx generated from the server was successfully saved and the avatar was initialized");
						if (isGetting != null) {
							isGetting.set(false);
						}
					}

					public void onNOData() {
						if (isGetting != null) {
							isGetting.set(false);
						}
					}
				});
			}
		} else {// 本地有imei
			if (!TextUtils.isEmpty(res)) {// 获取到oaid
				String oldOaID = LeDeviceBiz.INIT.readOaid();
				Log.i(LeDeviceBiz.TAG, "There is xxxx locally, comparing the old oaid with the new oaid");
				if (!TextUtils.equals(oldOaID, res)) {// 如果不一样，包含oldOaID为空情况
					// qi保存oaid
					LeDeviceBiz.INIT.saveOaid(res);
					// 上报imei, oaid到服务器
					// LeDeviceBiz.INIT.reportAPI(imei, res, imei2, null);
					Log.i(LeDeviceBiz.TAG,
							"There is xxxx locally, and the remote end requests to get oaid. The old id is different from the new one, including the fact that oldOaID is empty, which should be reported as new");
				}

			}
		}
	}

	/**
	 * 保存获取到的真实Imei
	 */
	public void saveImei(String imei) {
		if (LenovoGameApi.mApp != null) {
			SharedPreferences mSP = LenovoGameApi.mApp.getSharedPreferences(SP_FILE_NAME,
					LenovoGameApi.mApp.MODE_PRIVATE);
			SharedPreferences.Editor mEdit = mSP.edit();
			mEdit.putString(SP_KEY_IMEI, imei);
			mEdit.commit();
		}
	}

	/**
	 * 保存获取到的真实Imei2
	 */
	public void saveImei2(String imei2) {
		if (LenovoGameApi.mApp != null) {
			SharedPreferences mSP = LenovoGameApi.mApp.getSharedPreferences(SP_FILE_NAME,
					LenovoGameApi.mApp.MODE_PRIVATE);
			SharedPreferences.Editor mEdit = mSP.edit();
			mEdit.putString(SP_KEY_IMEI2, imei2);
			mEdit.commit();
		}
	}

	/**
	 * 保存获取到的真实Imei
	 */
	public void saveOaid(String oadi) {
		if (LenovoGameApi.mApp != null) {
			SharedPreferences mSP = LenovoGameApi.mApp.getSharedPreferences(SP_FILE_NAME,
					LenovoGameApi.mApp.MODE_PRIVATE);
			SharedPreferences.Editor mEdit = mSP.edit();
			mEdit.putString(SP_KEY_OAID, oadi);
			mEdit.commit();
		}
	}

	/**
	 * 保存获取到的真实Imei
	 */
	public String readImei() {
		SharedPreferences mSP = LenovoGameApi.mApp.getSharedPreferences(SP_FILE_NAME, LenovoGameApi.mApp.MODE_PRIVATE);
		return mSP.getString(SP_KEY_IMEI, "");
	}

	/**
	 * 保存获取到的真实Imei2
	 */
	public String readImei2() {
		SharedPreferences mSP = LenovoGameApi.mApp.getSharedPreferences(SP_FILE_NAME, LenovoGameApi.mApp.MODE_PRIVATE);
		return mSP.getString(SP_KEY_IMEI2, "");
	}

	/**
	 * 保存获取到的真实Imei
	 */
	public String readOaid() {
		SharedPreferences mSP = LenovoGameApi.mApp.getSharedPreferences(SP_FILE_NAME, LenovoGameApi.mApp.MODE_PRIVATE);
		return mSP.getString(SP_KEY_OAID, "");
	}

	/**
	 * 判断是否是同一天
	 */
	public boolean isSameDay(Date date1, Date date2) {
		Calendar calDateA = Calendar.getInstance();
		calDateA.setTime(date1);
		Calendar calDateB = Calendar.getInstance();
		calDateB.setTime(date2);
		return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
				&& calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
				&& calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 计算两个日期之间相差的天数
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 上报imei和oaid的api调用
	 */
	public void reportAPI(final String imei, final String oaid, final String imei2, ReprotListentr listentr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HiHttp.<String>post(Reprot.URL_REPORT)
							.paramsJson(generateRequestBody(imei, oaid, imei2)).toJson("body")
							.execute(new HiCallBack<String>(String.class) {
								@Override
								public void onSuccess(Response response) {
									String jsonstr = (String) response.body();
									Log.d(TAG, "reportAPI Response body = " + jsonstr);

								}

								@Override
								public void onError(Response response) {
								}
							});
				} catch (Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 封装上报参数
	 */
	private String generateRequestBody(String imei, String oaid, String imei2) {
		JSONObject object = new JSONObject();
		try {
			object.put(Reprot.KEY_MERCHANTID, Reprot.MERCHANT_ID);
			object.put(Reprot.KEY_SIGNTYPE, Reprot.SIGNTYPE);
			object.put(Reprot.KEY_OAID, oaid);
			object.put(Reprot.KEY_IMEIID, imei);
			if (!TextUtils.isEmpty(imei2)) {
				object.put(Reprot.KEY_IMEIID2, imei2);
			}
			/*----------对 imeiId, oaId, merchantId 进行RSA256加密-----------start*/
			LinkedHashMap<String, String> httpParams = new LinkedHashMap<String, String>();
			httpParams.put(Reprot.KEY_MERCHANTID, Reprot.MERCHANT_ID);
			httpParams.put(Reprot.KEY_OAID, oaid);
			httpParams.put(Reprot.KEY_IMEIID, imei);
			if (!TextUtils.isEmpty(imei2)) {
				httpParams.put(Reprot.KEY_IMEIID2, imei2);
			}
			httpParams.put(Reprot.KEY_SIGNTYPE, Reprot.SIGNTYPE);
			String sb = SignatureUtils.getSignCheckContent(httpParams);
			// Log.i(LeDeviceBiz.TAG, "generateRequestBody sb :" + sb);
			String sign = SignatureUtils.rsa256Sign(sb, Reprot.PRIVATE_KEY);
			// Log.i(LeDeviceBiz.TAG, "generateRequestBody sign:" + sign);
			/*----------对 imeiId, oaId, merchantId 进行RSA256加密-----------end*/
			object.put(Reprot.KEY_SIGN, sign);

			// Log.i(LeDeviceBiz.TAG, "object :" + object.toString());
		} catch (Exception e) {

		}
		return object.toString();
	}

	/**
	 * 封装获取网络imei参数
	 */
	private String getImeiRequest(String oaid) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		try {
			params.put(Reprot.KEY_MERCHANTID, Reprot.MERCHANT_ID);
			params.put(Reprot.KEY_OAID, oaid);
			/*----------对oaId, merchantId 进行RSA256加密-----------start*/
			String sb = SignatureUtils.getSignCheckContent(params);
			 Log.i(LeDeviceBiz.TAG, "sb : " + sb);
			String sign = SignatureUtils.rsa256Sign(sb, Reprot.PRIVATE_KEY);
			 Log.i(LeDeviceBiz.TAG, "un encode sign:" + sign);
			sign = URLEncoder.encode(sign, "UTF-8");
			 Log.i(LeDeviceBiz.TAG, "sign:" + sign);
			/*----------对oaId, merchantId 进行RSA256加密-----------end*/
			params.put(Reprot.KEY_SIGN, sign);
		} catch (Exception e) {

		}
		return SignatureUtils.getSignCheckContent(params);
	}

	/**
	 * 封装服务自生成imei
	 */
	private String getRandomIDRequest() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		try {
			params.put(Reprot.KEY_MERCHANTID, Reprot.MERCHANT_ID);
			params.put(Reprot.KEY_TIMESTAMP, System.currentTimeMillis() + "");
			/*----------对oaId, merchantId 进行RSA256加密-----------start*/
			String sb = SignatureUtils.getSignCheckContent(params);
			// Log.i(LeDeviceBiz.TAG, "sb : " + sb);
			String sign = SignatureUtils.rsa256Sign(sb, Reprot.PRIVATE_KEY);
			sign = URLEncoder.encode(sign, "UTF-8");
			// Log.i(LeDeviceBiz.TAG, "sign:" + sign);
			/*----------对oaId, merchantId 进行RSA256加密-----------end*/
			params.put(Reprot.KEY_SIGN, sign);
		} catch (Exception e) {

		}
		return SignatureUtils.getSignCheckContent(params);
	}

	public interface ReprotListentr {
		void onCallBack(String backImei, String imei2);

		void onNOData();
	}

	/**
	 * 调用接口通过oaid获取imei
	 */
	public void getImeiByOaid(String oaid) {
		oaid = "1fa72a4fbb3ab6a8";
		String url = Reprot.URL_REPORT + "?" + getImeiRequest(oaid);
		Log.d(TAG, "getImeiByOaid Response url = " + url);
		HiHttp.<String>get(url).execute(new HiCallBack<String>(String.class) {
			@Override
			public void onSuccess(Response response) {
				try {
					String jsonstr = (String) response.body();
					Log.d(TAG, "getImeiByOaid Response body = " + jsonstr);
//					JSONObject jsonObject = new JSONObject(jsonstr);
//
//					if (!jsonObject.isNull("data") && !jsonObject.getJSONObject("data").isNull("imeiId")) {
//						imeiID = jsonObject.getJSONObject("data").getString("imeiId");
//					}
//
//					Log.i(LeDeviceBiz.TAG, "imeiID: " + imeiID);
//					if (!jsonObject.isNull("data") && !jsonObject.getJSONObject("data").isNull("imeiId2")) {
//						imeiID2 = jsonObject.getJSONObject("data").getString("imeiId2");
//					}
//
//					if (!TextUtils.isEmpty(imeiID) && !TextUtils.equals("null", imeiID)) {// 查询到了imei，直接存储
//						saveImei(imeiID);
//						if (!TextUtils.isEmpty(imeiID2) && !TextUtils.equals("null", imeiID2)) {
//							saveImei2(imeiID2);
//							Log.d(TAG, "system get xxxx2 and save");
//						}
//						init(LenovoGameApi.mApp, imeiID, imeiID2);// 初始化Avator
//						Log.i(LeDeviceBiz.TAG,
//								"There is no local xxxx. According to oaid, xxxx is successfully checked back and avatar is initialized");
//						isGetting.set(false);
//					} else {// 查不到，从服务器请求生成imei
//						Log.i(LeDeviceBiz.TAG, "Unable to find, request to generate xxxx from server");
//						getRandomID(new ReprotListentr() {
//							public void onCallBack(String backImei, String imei2) {
//								saveImei(backImei);// 保存imei
//								init(LenovoGameApi.mApp, backImei, imei2);// 初始化Avator
//								reportAPI(backImei, oaid, "", null);// 上报imei和oaid
//								Log.i(LeDeviceBiz.TAG, "LENOVO reportAPI ");
//								Log.i(LeDeviceBiz.TAG,
//										"If it cannot be found, request from the server to generate xxxx, save xxxx successfully");
//								isGetting.set(false);
//							}
//
//							public void onNOData() {
//								isGetting.set(false);
//							}
//						});
//					}
				} catch (Exception e) {
					Log.e(LeDeviceBiz.TAG, "getImeiByOaid  Exception:" + e.getMessage());
				}

			}

			@Override
			public void onError(Response response) {
			}

		});

	}

	/**
	 * 从服务器获取随机数 并保存
	 */
	public void getRandomID(final ReprotListentr listentr) {
		String url = Reprot.URL_RANDOM_ID + "?" + getRandomIDRequest();
		HiHttp.<String>get(url).execute(new HiCallBack<String>(String.class) {
			@Override
			public void onSuccess(Response response) {
				try {
					if (response.body() != null) {
						String jsonResult = (String) response.body();
						// Log.d(TAG, "getRandomID Response body = " + jsonResult);
						JSONObject jsonObject = new JSONObject(jsonResult);
						String imeiID = jsonObject.optString("data");
						if (!TextUtils.isEmpty(imeiID) && !TextUtils.equals("null", imeiID) && listentr != null) {
							listentr.onCallBack(imeiID, "");
							return;
						}
						if (TextUtils.isEmpty(imeiID) && listentr != null) {
							listentr.onNOData();
						}
					}

				} catch (Exception e) {
					Log.e(LeDeviceBiz.TAG, "Exception：" + e.getMessage());
					if (listentr != null) {
						listentr.onNOData();
					}
				}
			}

			@Override
			public void onError(Response response) {
				if (listentr != null) {
					listentr.onNOData();
				}
			}
		});

	}

	@TargetApi(26)
	private void getDeviceId(Context context, final ReprotListentr listentr) {
		String deviceId = "";
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (null != tm) {
			if (ActivityCompat.checkSelfPermission(context,
					Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions((Activity) context,
						new String[] { Manifest.permission.READ_PHONE_STATE }, 1000);
			} else {
				String imei = "";
				String imei2 = "";
				if (tm != null) {
					try {
						if (Build.VERSION.SDK_INT >= 26) {
							imei = tm.getImei(0);
							imei2 = tm.getImei(1);
						} else {
							imei = tm.getDeviceId();
							if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 26) {
								imei2 = tm.getDeviceId(1);
							}
						}
						Log.i(LeDeviceBiz.TAG, imei2);
						if (!TextUtils.isEmpty(imei) && listentr != null) {
							listentr.onCallBack(imei, imei2);
						}
						if (TextUtils.isEmpty(imei) && listentr != null) {
							listentr.onNOData();
						}
					} catch (Exception e) {
						if (listentr != null) {
							listentr.onNOData();
						}
					}
				}
				Log.d(LeDeviceBiz.TAG, "deviceId = " + deviceId + "  imei = " + imei + "  imei2 = " + imei2);
			}
		}
	}

	private static void init(Context context, String imei, String imei2) {
		Log.i(LeDeviceBiz.TAG, "Initializing the Avator 1 = " + imei + "  2 = " + imei2);

	}

	/**
	 * 上报相关内容
	 */
	public static class Reprot {
		public static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDQF/Ljx5KODBbyRrs+nMPN+qceyqiVLIROCreTYAqhL925iDd+DXYoOt37PzKm3stdyvfYOvTUi4Fxudhm7n7u52S2j9pi8nWGpApaXZoTQxeiK8LUDNSMtq96+nu2Rx7blM2K+qbxUlF3M+vePEE1/dgd9HbyIIkEHrT9WSjQGKZtJEh7mXtU6v3ucS4jPSUeakcD80W36jwjvTlnsLQ+b07K8ArapTlrUaNORcn84P9HBcQYdGh3WKFjE/xBw/e9HS5Gyay8UPGRmk9kx5nBagewhlSszS3HSS9Wo0hEip3NyZfhtcjjVNeJ/Hkr46sI0p9rvWw2oVVwkxyckBdbAgMBAAECggEBAMlypbxEc2f35C7esl3GvPcmc6qnXdQrqFk0nsqY6zFneSom6DGrLnt7oXGcmJhajl1aI9y8Fk+8Ta+ezhO3RkJY13K0iCHJvmR9YVhhzMpg5PwliRJm6Uzffbl7y0+GQiUoRGMKi5qKqjrPlMMraaWu/AzjoFJ9lKbcYzHvokbwFxIUgD4Er0bcX6PbStUMiDCdOJZp/6cDc3QMDqzjeAtBJDuuzxDGJ6WpBh+7bRYmxSO/cpsn/l3Zf30WHGbMvsVYna6QC9wGeHHn85MHyI3znHBRWIYRFhOLrWMhM1uRhxKou+Vy1TAJe4p0H2PHPDNSjZErfVNv/yzAuZ4dqgECgYEA68ZmQX7o+hzDVXjMbSnXUWmvvlRiTUp6kEUH0pcpIifo9tVERcTd14cDNyV+vJf2IGJTcwkC4k6mybMcyFgJ8s4tSVgl+wT0XhsaGDb5PxozKUxiry8neUaQJ/kAVkESh3FWEGUBw3pzfYFPZOdYM/ZCmTgs/uWCTA9Zcpi2zZsCgYEA4fGquvj9V9ZxhZA/OJJkuXUVAtpDS5wukWLJNfhviLtYChRsBWOuyHKQQmKmbfyvPEU+tf2hW4Z0KlS8Ez3UJNmqsF3nMu8+7K87a4J2z63Mgtw7PXQNsijx97sP0Tapi+S5U0wHiT9ffTxoJysY4sQPxTz4OxfFC1yskqoxWUECgYAXOASd8peS+f4qjqjjHzm7JMNP5ROBNRJ43rN0g7pjSLbiiAIfNNH8BanFZoXioLx2V/iL3ZlRHBnEx0TQXgssxC0lie7Dl6tRES4ysEc/dZ8QNBuL0QL8KHzN0vG7jND8Uab+GlRgIdD5zSA6eaXwCBJq8GNN/uAkCWbvqCZofQKBgDI89eVU75/UQrGb/TKq+lJyQhW8SjJJq+EPwgWKE+KnJVA4cjxQidBwCLt6C99wy9Xu356/ol0jbDjtVlRA7yayb01/06Rzpd4iqGejPp+22T5Pkax8XX87s3GN2lp+ePxuAvcOdK6DtMu33sC2yHXDem40vE0HX/zCwmxqA0cBAoGAfHr2segQ8orLu7h4pxlZjPLbMdeOqcD8VUar8XYM9kWs6+759YLhsAWQiz62s0qpM5RggH2TF4QnRucYDcvypWyj3UTn2qXYFL5YvAI6/TMZcz9q+MnmDLKHkmfJDLnomdMg/KQDRWoE6h2dqOEwXv6EZE7CFJ2DPLzvZDGEmCQ=";
		public static final String URL_HOST = "https://cloud-service.lenovomm.com";
		public static final String URL_REPORT = URL_HOST + "/zui/v1/imeimapping";
		public static final String URL_RANDOM_ID = URL_HOST + "/zui/v1/imeis";
		public static final String MERCHANT_ID = "183095110139998";
		public static final String SIGNTYPE = "signType";

		public static final String KEY_MERCHANTID = "merchantId";
		public static final String KEY_IMEIID = "imeiId";
		public static final String KEY_IMEIID2 = "imeiId2";
		public static final String KEY_OAID = "oaId";
		public static final String KEY_SIGNTYPE = "signType";
		public static final String KEY_SIGN = "sign";
		public static final String KEY_TIMESTAMP = "timestamp";
	}

}
