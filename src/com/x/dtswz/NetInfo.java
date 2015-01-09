package com.x.dtswz;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-3-24
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
public class NetInfo {

    public String url = "";
    public JSONObject info ;
    public boolean isEncrypt;
    public OnNetWorkListener listener;


    public NetInfo(Context context, String url, RequestJSON requestJSON, boolean encrypt, OnNetWorkListener listener) {
        this.url = url;
        if (requestJSON!=null) {
            this.info = requestJSON.getJSON(context);
        }else {
            this.info =null;
        }
        this.isEncrypt = encrypt;
        this.listener = listener;
    }

}
