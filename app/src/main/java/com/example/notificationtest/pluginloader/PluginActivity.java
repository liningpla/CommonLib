package com.example.notificationtest.pluginloader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.notificationtest.R;

import java.io.File;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class PluginActivity extends Activity {

    private Button btn_test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        btn_test = findViewById(R.id.btn_test);

        Dynamic dynamic = new Dynamic();
        dynamic.init(this);
        dynamic.showBanner();
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dynamic.showAppWall();
            }
        });
    }

    public void test(){
        /**使用DexClassLoader方式加载类*/
        //dex压缩文件的路径(可以是apk,jar,zip格式)
        String dexPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Dynamic.apk";
        //dex解压释放后的目录
        //String dexOutputDir = getApplicationInfo().dataDir;
        String dexOutputDirs = Environment.getExternalStorageDirectory().toString();
        //定义DexClassLoader
        //第一个参数：是dex压缩文件的路径
        //第二个参数：是dex解压缩后存放的目录
        //第三个参数：是C/C++依赖的本地库文件目录,可以为null
        //第四个参数：是上一级的类加载器
        DexClassLoader cl = new DexClassLoader(dexPath,dexOutputDirs,null,getClassLoader());

        /**使用PathClassLoader方法加载类*/
        //创建一个意图，用来找到指定的apk：这里的"com.dynamic.impl是指定apk中在AndroidMainfest.xml文件中定义的<action name="com.dynamic.impl"/>
        Intent intent = new Intent("com.dynamic.impl", null);
        //获得包管理器
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveinfoes =  pm.queryIntentActivities(intent, 0);
        //获得指定的activity的信息
        ActivityInfo actInfo = resolveinfoes.get(0).activityInfo;
        //获得apk的目录或者jar的目录
        String apkPath = actInfo.applicationInfo.sourceDir;
        //native代码的目录
        String libPath = actInfo.applicationInfo.nativeLibraryDir;
        //创建类加载器，把dex加载到虚拟机中
        //第一个参数：是指定apk安装的路径，这个路径要注意只能是通过actInfo.applicationInfo.sourceDir来获取
        //第二个参数：是C/C++依赖的本地库文件目录,可以为null
        //第三个参数：是上一级的类加载器
        PathClassLoader pcl = new PathClassLoader(apkPath,libPath,this.getClassLoader());
        //加载类
        try {
            //com.dynamic.impl.Dynamic是动态类名
            //使用DexClassLoader加载类
            //Class libProviderClazz = cl.loadClass("com.dynamic.impl.Dynamic");
            //使用PathClassLoader加载类
            Class libProviderClazz = pcl.loadClass("com.example.notificationtest.pluginloader");
            Dynamic dynamic = (Dynamic)libProviderClazz.newInstance();
            if(dynamic != null){
                dynamic.init(this);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
