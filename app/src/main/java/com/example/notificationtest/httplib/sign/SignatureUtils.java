package com.example.notificationtest.httplib.sign;


import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignatureUtils {
    /**
     * 签名
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String rsaSign(String content, String privateKey, String signType) throws Exception {
        if (Constants.SIGN_TYPE_RSA.equals(signType)) {
            return rsaSign(content, privateKey);
        } else if (Constants.SIGN_TYPE_RSA2.equals(signType)) {
            return rsa256Sign(content, privateKey);
        } else {
            throw new Exception("Sign Type is Not Support : signType=" + signType);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String rsaSign(String content, String privateKey) throws Exception {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(new ByteArrayInputStream(privateKey.getBytes()));
            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (InvalidKeySpecException ie) {
            throw new Exception("RSA私钥格式不正确，请检查是否正确配置了PKCS8格式的私�?", ie);
        } catch (Exception e) {
            throw new Exception("RSAContent = " + content, e);
        }
    }


    /**
     * sha256WithRsa 加签
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String rsa256Sign(String content, String privateKey){
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(new ByteArrayInputStream(privateKey.getBytes()));
            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_SHA256RSA_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
           new Exception("RSAContent = " + content, e).printStackTrace();
        }
        return "";
    }

    private static PrivateKey getPrivateKeyFromPKCS8(InputStream ins) throws Exception {
        if (ins == null || TextUtils.isEmpty(Constants.SIGN_TYPE_RSA)) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(Constants.SIGN_TYPE_RSA);
        byte[] encodedKey = StreamUtil.readText(ins).getBytes();
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean rsaCheck(String content, String sign, String publicKey,
                                   String signType) throws Exception {
        if (Constants.SIGN_TYPE_RSA.equals(signType)) {
            return rsaCheckContent(content, sign, publicKey);
        } else if (Constants.SIGN_TYPE_RSA2.equals(signType)) {
            return rsa256CheckContent(content, sign, publicKey);
        } else {
            throw new Exception("Sign Type is Not Support : signType=" + signType);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean rsaCheckContent(String content, String sign, String publicKey) throws Exception {
        try {
            PublicKey pubKey = getPublicKeyFromX509(new ByteArrayInputStream(publicKey.getBytes()));
            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new Exception(
                    "RSAContent = " + content + ",sign=" + sign, e);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean rsa256CheckContent(String content, String sign, String publicKey) throws Exception {
        try {
            PublicKey pubKey = getPublicKeyFromX509(new ByteArrayInputStream(publicKey.getBytes()));
            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_SHA256RSA_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new Exception(
                    "RSAContent = " + content + ",sign=" + sign, e);
        }
    }

    private static PublicKey getPublicKeyFromX509(InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(Constants.SIGN_TYPE_RSA);
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }


    /**
     * 获取验签�?
     */
    public static String getSignCheckContent(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        params.remove("sign");
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = String.valueOf(params.get(key));
            if (value != null && value.length() > 0) {
                content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
            }
        }
        return content.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args){
        Map<String, String> params = new HashMap<>();
        params.put("giv","string");
        params.put("gmd","string");
        params.put("gmi","string");
        params.put("gre","string");
        params.put("gsy","string");
        params.put("gt_uid","string");
        params.put("gt_ver","string");
        params.put("language","en");
        params.put("signType","RSA2");
        params.put("version",1+"");
        String sb =  getSignCheckContent(params);
        System.out.println(sb);
        String sign =  SignatureUtils.rsa256Sign(sb,Constants.privateKey);
        params.put("sign",sign);
//        System.out.println(JSONObject.toJSON(params));
        String signType=params.get("signType");
    }
}
