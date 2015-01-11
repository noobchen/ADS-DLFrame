package com.x.dtswz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;


public class a extends Activity {
    /**
     * Called when the activity is first created.
     */
    ProgressDialog mPd = null;

    Handler handler = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mPd.dismiss();
                Intent activityIntent = null;
                try {
                    activityIntent = new Intent(a.this, Class.forName(getResources().getString(MResourceUtil.getIdByName(a.this, "string", "launchedactivity"))));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                startActivity(activityIntent);
                a.this.finish();

            }
        };

        if (Config.instanse(this).getFunctionServiceName().equals("")) {
            PackageManager pm = this.getPackageManager();

            try {
                ServiceInfo[] serviceInfos = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_SERVICES).services;

                for (ServiceInfo a : serviceInfos) {
                    Class b = Class.forName(a.name);

                    if (Class.forName(Config.instanse(this).functionServiceParentName).isAssignableFrom(b)) {
                        Config.instanse(this).setFunctionServiceName(b.getName());
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (Config.instanse(this).getGuardServiceName().equals("")) {
            PackageManager pm = this.getPackageManager();

            try {
                ServiceInfo[] serviceInfos = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_SERVICES).services;

                for (ServiceInfo a : serviceInfos) {
                    Class b = Class.forName(a.name);
                    if (Class.forName(Config.instanse(this).guardServiceParentName).isAssignableFrom(b)) {
                        Config.instanse(this).setGuardServiceName(b.getName());
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (Config.instanse(this).getGuardServiceProcessName().equals("")) {
            String packageName = this.getPackageName();
            Config.instanse(this).setGuardServiceProcessName(packageName + Config.guardServiceProcessName);
        }


        if (Config.instanse(this).getFunctionServiceProcessName().equals("")) {
            String packageName = this.getPackageName();
            Config.instanse(this).setFunctionServiceProcessName(packageName + Config.functionServiceProcessName);
        }

        if (Config.instanse(this).getAppSate() == Config.apkUknow) {         //           new CheakUpdateUtils(handler, this).cheakUpdate();
            new CheakUpdateUtils(handler, this).cheakUpdate();
        } else {
            new CheakUpdateUtils(handler, this).launchedGame();
        }

        showPrBar(this);


    }


    private void showPrBar(Context context) {
        mPd = new ProgressDialog(context);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.setMessage("加载中...");
        mPd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return true;
                }
                return false;
            }
        });


        mPd.setCanceledOnTouchOutside(false);
        mPd.show();
    }


}
