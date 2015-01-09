package com.x.dtswz;

/**
 * Created by Administrator on 14-11-3.
 */
public class NetWorksThread implements Runnable {
    HttpUtil util = null;

    public NetWorksThread(HttpUtil util) {
        this.util = util;
    }

    @Override
    public void run() {
        util.communicationData();
    }
}
