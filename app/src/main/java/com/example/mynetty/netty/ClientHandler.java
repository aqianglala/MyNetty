package com.example.mynetty.netty;

import android.os.SystemClock;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.mynetty.netty.bean.AraProtocol;
import com.example.mynetty.netty.bean.EquipmentInfo;
import com.example.mynetty.netty.bean.Model;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.ScheduledFuture;

public class ClientHandler extends SimpleChannelInboundHandler {

    private volatile ScheduledFuture<?> heartBeat;
    private Timer timer;
    private TimerTask task;
    public ClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.d(  "channelActive");
        //连接成功后发送设备信息
        EquipmentInfo equipmentInfo = new EquipmentInfo();
        equipmentInfo.setEquipmentSn("sssss");
        equipmentInfo.setIp("192.168.1.170");
        equipmentInfo.setVersion(AppUtils.getAppVersionName());
        String result = GsonUtils.toJson(equipmentInfo);

        AraProtocol araProtocol = new AraProtocol();
        araProtocol.setContent(result);
        araProtocol.setFlag(NettyConstant.FLAG_CONNECT);
        araProtocol.setType(NettyConstant.TYPE_TRUEFACE);
        araProtocol.setLength(result.getBytes(StandardCharsets.UTF_8).length);
        ctx.writeAndFlush(araProtocol).addListener(new FutureListener() {
            @Override
            void success() {
                LogUtils.d(  "发送设备号成功");
            }

            @Override
            void error() {
                LogUtils.d(  "发送设备号失败");
            }
        });
        startTimer(ctx);
    }

    /**
     * 开启计时器，30秒对方未响应直接断开连接
     */
    private void startTimer(ChannelHandlerContext ctx){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                if (!NettyClient.getInstance().getConnectStatus()){
                    ctx.close();
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 30 * 1000);
    }
    /**
     * 连接成功
     */
    private void connectSuccessful(ChannelHandlerContext ctx){
        NettyClient.getInstance().setConnectStatus(true);
        //30秒钟发一个心跳
        if (heartBeat == null) {
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatTask(ctx), 0, 30, TimeUnit.SECONDS);
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            AraProtocol araProtocol = (AraProtocol) msg;  //直接转化成协议消息实体
            LogUtils.json(araProtocol);
            if (araProtocol.getFlag() == NettyConstant.FLAG_BUSINESS){
                LogUtils.d("收到发来的业务数据");
                NettyClient.getInstance().executeBusiness(araProtocol);
            }else if (araProtocol.getFlag() == NettyConstant.FLAG_HEARTBEAT){
                LogUtils.d("收到发来的心跳数据");
            }else if (araProtocol.getFlag() == NettyConstant.FLAG_CONNECT){
                LogUtils.d("收到发来的连接成功数据");
                connectSuccessful(ctx);
            }else if (araProtocol.getFlag() == NettyConstant.FLAG_DISCONNECT){
                LogUtils.d("收到发来的断开连接数据");
            }else{
                LogUtils.e("未知的类型数据");
            }
        }catch (Exception e){
            LogUtils.e("解析发来的数据失败，"+e.getMessage());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.d(  "channelInactive");
        reConnect(ctx);
    }

    /**
     * 尝试重新连接
     */
    private void reConnect(ChannelHandlerContext ctx) throws Exception {
        if (heartBeat != null && !heartBeat.isCancelled()) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.close();
        NettyClient.getInstance().close();
        super.channelInactive(ctx);
        SystemClock.sleep(500);
        NettyClient.getInstance().start();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        LogUtils.d(  "netty异常：" + cause.getMessage() + "线程名" + Thread.currentThread().getName());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent stateEvent = (IdleStateEvent) evt;
        switch (stateEvent.state()) {
            case READER_IDLE:
                handlerReaderIdle(ctx);
                break;
            case WRITER_IDLE:
                handlerWriterIdle(ctx);
                break;
            case ALL_IDLE:
                handlerAllIdle(ctx);
                break;
            default:
                break;
        }
    }

    private void handlerAllIdle(ChannelHandlerContext ctx) {
        LogUtils.d(  "handlerAllIdle");
    }

    private void handlerWriterIdle(ChannelHandlerContext ctx) {
        LogUtils.d(  "handlerWriterIdle");
    }

    private void handlerReaderIdle(ChannelHandlerContext ctx)  throws Exception{
        LogUtils.d(  "handlerReaderIdle");
        ctx.close();
    }

    //心跳包发送任务
    private class HeartBeatTask implements Runnable {

        private ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            String message = buildHeartMessage();
            AraProtocol protocolBean = new AraProtocol();
            protocolBean.setContent(message);
            protocolBean.setFlag(NettyConstant.FLAG_HEARTBEAT);
            protocolBean.setType(NettyConstant.TYPE_TRUEFACE);
            protocolBean.setLength(message.getBytes(StandardCharsets.UTF_8).length);
            LogUtils.d(  "发送心跳包" + message);
            ctx.writeAndFlush(protocolBean).addListener(new FutureListener() {

                @Override
                public void success() {
//                    LogUtils.d(  "心跳发送成功");
                }

                @Override
                public void error() {
//                    LogUtils.d(  "心跳发送失败");
                }
            });
        }

        private String buildHeartMessage() {
            Model model = new Model();
            model.setType(1);
            return GsonUtils.toJson(model);
        }
    }

}
