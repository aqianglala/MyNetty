package com.example.mynetty.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


public abstract class FutureListener implements ChannelFutureListener {
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            success();
        } else {
            error();
        }
    }

     abstract void success();

     abstract void error();
}
