package com.x.dtswz;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-3-24
 * Time: 上午11:08
 * To change this template use File | Settings | File Templates.
 */
public class HttpUtil {


    private int relinkTimes = 0;               //计数重连次数
    private HttpClient httpClient;
    private HttpPost httpPost;
    private NetInfo netInfo = null;
    private final int CLIENT_TIMEOUT = 5;
    private final int CLIENT_SO_TIMEOUT = 10;
    private final int RETRY_TIMES = 3;

    public HttpUtil(NetInfo netInfo) {
        this.netInfo = netInfo;
    }


    public void communicationData() {


        try {
            relinkTimes++;


            String msg = "";

            httpPost = new HttpPost(netInfo.url);


            if (netInfo.isEncrypt && netInfo.info != null) {

                msg = EncrypUtil.encode(netInfo.info.toString());


            }else if(netInfo.info ==null) {

                this.netInfo.listener.onFail("");

                return;

            }

            StringEntity se = new StringEntity(msg, HTTP.UTF_8);

            httpPost.setEntity(se);

            httpClient = new DefaultHttpClient();

            //请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    CLIENT_TIMEOUT * 1000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    CLIENT_SO_TIMEOUT * 1000);


            // 发送请求
            HttpResponse httpResponse = httpClient.execute(httpPost);


            int rc = httpResponse.getStatusLine().getStatusCode();

            if (rc != HttpURLConnection.HTTP_OK) {

                throw new IOException("HTTP response code: " + rc);
            }
            // 得到应答的字符串，这也是一个 JSON 格式保存的数据
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                String result = EntityUtils.toString(entity);
                if (netInfo.isEncrypt) {
                    result = EncrypUtil.decode(result);
                }

                if (netInfo.listener != null) {

                    netInfo.listener.onSuccess(result);
                    netInfo.listener = null;
                }
            } else {
                if (netInfo.listener != null) {
                    netInfo.listener.onFail("");
                    netInfo.listener = null;
                }
            }

            netInfo.info = null;

        } catch (Exception e) {

            e.printStackTrace();

            if (relinkTimes < RETRY_TIMES) {
                reconnect();
            } else {
                relinkTimes = 0;
                connectFailed("Exception", e.toString());
            }
        } finally {
            closeConnect();
        }
    }

    private void reconnect() {
        closeConnect();
        communicationData();
    }

    private void closeConnect() {
        try {
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
                httpClient = null;
            }

            if (httpPost != null) {
                if (!httpPost.isAborted()) {
                    httpPost.abort();
                }
                httpPost = null;
            }
        } catch (Exception e) {

        }
    }


    private void connectFailed(String errorTip, String exceptionStr) {
        if (netInfo.listener != null) {
            netInfo.listener.onFail("");
            netInfo.listener = null;
        }
    }


}
