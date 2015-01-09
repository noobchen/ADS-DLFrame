package com.x.dtswz;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 守护服务，用于服务被安全软件杀死后自启动
 * Created by Administrator on 2015/1/8.
 */
public class d extends Service {

    private ServiceGuard serviceGuard = new ServiceGuard.Stub() {

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(d.this, c.class);
            d.this.stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(d.this, c.class);
            d.this.startService(i);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!CheakProcessRunningUtils.isProcessRunning(d.this, Config.functionServiceProcessName)) {

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
