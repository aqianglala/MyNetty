package com.example.mynetty.netty;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

/**
 * <pre>
 *     author : zhangx
 *     time   : 2022/01/20
 *     desc   : Netty服务，主要用于数据同步
 *     version: 1.0
 * </pre>
 */
public class NettyService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        connect();
    }

    @Override
    public void onDestroy() {
        NettyClient.getInstance().close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connect() {
        if (!NettyClient.getInstance().getConnectStatus()) {
            LogUtils.d("发起连接");
            new Thread(){
                @Override
                public void run() {
                    NettyClient.getInstance().start();
                }
            }.start();
        }
    }
}
