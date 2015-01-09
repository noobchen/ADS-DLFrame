package com.x.dtswz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 触发receiver，捕捉开屏以及，新增应用广播，转发Intent到Service处理。
 * Created by Administrator on 14-10-23.
 */

public class b extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            String serviceName = Config.instanse(context).getServiceName();
            try {
                intent.setClass(context, Class.forName(serviceName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            context.startService(intent);
        }
    }
}
