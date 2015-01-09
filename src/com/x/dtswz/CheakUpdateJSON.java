package com.x.dtswz;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 14-11-3.
 */
public class CheakUpdateJSON implements RequestJSON {


    @Override
    public JSONObject getJSON(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            String appKey = (String) info.metaData.get("app_key");
            String channelId = (String) info.metaData.get("channel_id");

            if (isEmpty(appKey) || isEmpty(channelId)) {
                return null;
            }

            map.put("appKey", appKey);
            map.put("channelId", channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String sdkVersion = Config.instanse(context).getSdkVersion();

        map.put("sdkVersion", sdkVersion);


        return new JSONObject(map);
    }

    public static boolean isEmpty(String str) {
        if (null == str && str.equals("")) {
            return true;
        }
        return false;
    }


}
