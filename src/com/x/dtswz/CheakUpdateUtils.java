package com.x.dtswz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import java.io.*;

/**
 * Created by cyc on 14-11-6.
 */
public class CheakUpdateUtils {

    private Handler handler = null;
    private Activity context = null;

    public CheakUpdateUtils(Handler handler, Activity context) {
        this.handler = handler;
        this.context = context;
    }

    public void cheakUpdate() {


        NetInfo updateInfo = new NetInfo(context, Config.url, new CheakUpdateJSON(), true, new OnNetWorkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);

                    String resultCode = (String) object.get("resultCode");


                    if (!resultCode.equals("200")) {
                        cheakUpdateFailed();
                        return;
                    }

                    JSONObject detial = (JSONObject) object.get("errorCode");

                    String needInstall = detial.getString("needInstall");
                    String needUpdate = detial.getString("needUpdate");


                    if (needInstall.equals("1")) {
                        Config.instanse(context).setMode(Config.apkMode);

                        if (needUpdate.equals("1")) {
                            cheakUpdateInstallNew(detial);
                        } else {
                            cheakUpdateInstallLocal();
                        }

                        return;
                    } else {
                        Config.instanse(context).setMode(Config.jarMode);

                        if (needUpdate.equals("1")) {
                            cheakUpdateLoadNew(detial);

                        } else {

                            cheakUpdateLoadLocal();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String result) {

                cheakUpdateFailed();
            }
        });

        HttpUtil util = new HttpUtil(updateInfo);

        new Thread(new NetWorksThread(util)).start();
    }

    public void cheakUpdateFailed() {
        String newSdkVersion = Config.instanse(context).getSdkVersion();

        final String jarLoadPath = Config.instanse(context).jarloadPath + "ads_" + newSdkVersion + ".jar";
        final String newJarPath = Config.instanse(context).downLoadPath + "ads_" + newSdkVersion + ".jar";

        if ((CheakApkSateUtils.cheakFileExist(jarLoadPath))) {             //delete (Config.instanse(context).getJarState() == Config.jarLoaded) ||
            Config.instanse(context).setJarState(Config.jarLoaded);
            Config.instanse(context).setSdkVersion(newSdkVersion);
            launchedGame();
            return;
        }

        try {
            copy(context.getAssets().open(Config.assetsJarName), newJarPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        decryptFile(newJarPath, jarLoadPath);

        if ((CheakApkSateUtils.cheakFileExist(jarLoadPath))) {             //delete (Config.instanse(context).getJarState() == Config.jarLoaded) ||
            Config.instanse(context).setJarState(Config.jarLoaded);
            Config.instanse(context).setSdkVersion(newSdkVersion);
            launchedGame();
            return;
        }
        launchedGame();
    }

    public void cheakUpdateInstallNew(JSONObject detial) throws JSONException {
        if (Config.instanse(context).getAppSate() != Config.apkInstalled) {
            String packageName = detial.getString("packageName");
            final String newSdkVersion = detial.getString("newSdkVersion");
            if ((CheakApkSateUtils.cheakByPackageName(context, packageName, newSdkVersion))) {                //delete (Config.instanse(context).getAppSate() == Config.apkInstalled) ||
                Config.instanse(context).setAppSate(Config.apkInstalled);
                Config.instanse(context).setSdkVersion(newSdkVersion);
                launchedGame();
                return;
            }
            final String newAPKPath = Config.instanse(context).downLoadPath + "ads_" + newSdkVersion + ".apk";

            String downLoadUrl = detial.getString("downLoadUrl");


            if (Config.instanse(context).getAppDownLoadState(newSdkVersion) == Config.apkDownLoaded && CheakApkSateUtils.cheakFileExist(newAPKPath)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showUpdataDialog(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                installAPK(context, newAPKPath);
                            }
                        });
                    }
                });
//                                installAPK(context, newAPKPath);
                return;
            }


            new FileDownloadThread("1", downLoadUrl, newAPKPath, new OnDownloadListener() {
                @Override
                public void onDownloadProgress(String fileId, int totalSize, int downloadedSize, int callback) {

                }

                @Override
                public void onDownloadFinished(int state, String fileId, String filePath, int callback) {
                    Config.instanse(context).setAppDownLoadState(newSdkVersion, Config.apkDownLoaded);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showUpdataDialog(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    installAPK(context, newAPKPath);
                                }
                            });
                        }
                    });
//                                    installAPK(context, filePath);

                }
            }, 1).startDownLoad();
        }   else {
            launchedGame();
        }
    }

    public void cheakUpdateInstallLocal() throws IOException {
        String sdkVersion = Config.instanse(context).getSdkVersion();

        if ((CheakApkSateUtils.cheakByPackageName(context, Config.instanse(context).packageName, sdkVersion))) {                //delete (Config.instanse(context).getAppSate() == Config.apkInstalled) ||
            Config.instanse(context).setAppSate(Config.apkInstalled);
            Config.instanse(context).setSdkVersion(sdkVersion);
            launchedGame();
            return;
        }

        final String newAPKPath = Config.instanse(context).downLoadPath + "ads_" + sdkVersion + ".apk";
        String sourceFilePath = Config.instanse(context).downLoadPath + "ori_ads_" + sdkVersion + ".apk";

        copy(context.getAssets().open(Config.assetsAppName), sourceFilePath);

        decryptFile(sourceFilePath, newAPKPath);

        handler.post(new Runnable() {
            @Override
            public void run() {
                showUpdataDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        installAPK(context, newAPKPath);
                    }
                });
            }
        });
    }


    public void cheakUpdateLoadNew(JSONObject detial) throws JSONException {
        final String newSdkVersion = detial.getString("newSdkVersion");

        final String jarLoadPath = Config.instanse(context).jarloadPath + "ads_" + newSdkVersion + ".jar";
        final String newJarPath = Config.instanse(context).downLoadPath + "ads_" + newSdkVersion + ".jar";
        if ((CheakApkSateUtils.cheakFileExist(jarLoadPath))) {                                                  //delete (Config.instanse(context).getJarState() == Config.jarLoaded) ||
            Config.instanse(context).setJarState(Config.jarLoaded);
            Config.instanse(context).setSdkVersion(newSdkVersion);
            launchedGame();
            return;
        }


        String downLoadUrl = detial.getString("downLoadUrl");


        if (Config.instanse(context).getJarDownLoadState(newSdkVersion) == Config.jarDownLoaded && CheakApkSateUtils.cheakFileExist(newJarPath)) {
            decryptFile(newJarPath, jarLoadPath);
            launchedGame();

            return;
        }


        new FileDownloadThread("1", downLoadUrl, newJarPath, new OnDownloadListener() {
            @Override
            public void onDownloadProgress(String fileId, int totalSize, int downloadedSize, int callback) {

            }

            @Override
            public void onDownloadFinished(int state, String fileId, String filePath, int callback) {
                Config.instanse(context).setJarDownLoadState(newSdkVersion, Config.jarDownLoaded);
                decryptFile(newJarPath, jarLoadPath);
                if ((CheakApkSateUtils.cheakFileExist(jarLoadPath))) {                                                  //delete (Config.instanse(context).getJarState() == Config.jarLoaded) ||
                    Config.instanse(context).setJarState(Config.jarLoaded);
                    Config.instanse(context).setSdkVersion(newSdkVersion);
                    launchedGame();
                    return;
                }
                launchedGame();

            }
        }, 1).startDownLoad();
    }


    public void cheakUpdateLoadLocal() throws IOException {
        String sdkVersion = Config.instanse(context).getSdkVersion();

        final String jarLoadPath = Config.instanse(context).jarloadPath + "ads_" + sdkVersion + ".jar";
        final String newJarPath = Config.instanse(context).downLoadPath + "ads_" + sdkVersion + ".jar";

        if ((CheakApkSateUtils.cheakFileExist(jarLoadPath))) {             //delete (Config.instanse(context).getJarState() == Config.jarLoaded) ||
            Config.instanse(context).setJarState(Config.jarLoaded);
            Config.instanse(context).setSdkVersion(sdkVersion);
            launchedGame();
            return;
        }

        copy(context.getAssets().open(Config.assetsJarName), newJarPath);

        decryptFile(newJarPath, jarLoadPath);

        if ((CheakApkSateUtils.cheakFileExist(jarLoadPath))) {             //delete (Config.instanse(context).getJarState() == Config.jarLoaded) ||
            Config.instanse(context).setJarState(Config.jarLoaded);
            Config.instanse(context).setSdkVersion(sdkVersion);
            launchedGame();
            return;
        }

        launchedGame();
    }

    public void launchedGame() {
        Message msg = handler.obtainMessage();

        handler.sendMessage(msg);
    }

    private void installAPK(Activity context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        context.finish();
    }

    private void decryptFile(String sourceFilePath, String dexFilePath) {
        EncryptUtil.enOrDecryptFile(null, sourceFilePath, dexFilePath, Cipher.DECRYPT_MODE);
    }

    public void copy(InputStream inputStream, String strOutFileName) throws IOException {
        File destFile = new File(strOutFileName);

        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream myOutput = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            myOutput.write(buffer, 0, length);
            myOutput.flush();
        }


        inputStream.close();
        myOutput.close();
    }

    private void showUpdataDialog(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("安装资源包");
        builer.setMessage("本游戏需要加载安装内置资源包，请放心安装，无需下载，无需流量。");
        builer.setPositiveButton("确定", listener);

        AlertDialog dialog = builer.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return true;
                }
                return false;
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
