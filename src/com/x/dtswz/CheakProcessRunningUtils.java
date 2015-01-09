package com.x.dtswz;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2015/1/7.
 */
public class CheakProcessRunningUtils {

    public static boolean isProcessRunning(Context context, String processName) {
        boolean isRunning = false;

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {

            if (info.processName.equals(processName)) {
                isRunning = true;

                break;
            }
        }

        return isRunning;
    }
}
