package com.example.mynetty.netty;

import android.os.SystemClock;
import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.mynetty.netty.bean.AraProtocol;
import com.example.mynetty.netty.bean.BaseHttpResult;
import com.example.mynetty.netty.bean.BusinessBean;
import com.example.mynetty.netty.bean.EquipHeartbeat;
import com.example.mynetty.netty.bean.TransferMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient {
    private EventLoopGroup nioEventLoopGroup;
    private volatile static NettyClient nettyClient;


    private volatile Channel channel;

    private Bootstrap bootstrap;
    private volatile boolean connectStatus;
    private final String mNettyHost = "192.168.3.173";

    private NettyClient() {
    }

    public static NettyClient getInstance() {
        if (nettyClient == null) {
            synchronized (NettyClient.class) {
                if (nettyClient == null) {
                    nettyClient = new NettyClient();
                }
            }
        }
        return nettyClient;
    }

    public synchronized void start() {
        nioEventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new IdleStateHandler(40, 40, 40));
                        p.addLast(new MyProtocolEncoder());
                        p.addLast(new MyProtocolDecoder(NettyConstant.MAX_FRAME_LENGTH, NettyConstant.LENGTH_FIELD_OFFSET, NettyConstant.LENGTH_FIELD_LENGTH, NettyConstant.LENGTH_ADJUSTMENT, 0, false));
                        p.addLast(new ClientHandler());
                    }
                });
        try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
//        LogUtils.d("正在连接，当前线程是" + Thread.currentThread().getName());
        ChannelFuture future = bootstrap.connect(NettyConstant.HOST, NettyConstant.PORT);
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                if (channel != null && channel.isActive()) {
                    channel.closeFuture();
                }
                channel = channelFuture.channel();
            } else {
                future.channel();
                setConnectStatus(false);
                SystemClock.sleep(500);
                doConnect();
            }
        });
    }

    /**
     * 执行对应业务
     *
     * @param araProtocol
     */
    public void executeBusiness(AraProtocol araProtocol) {
        String content = araProtocol.getContent();
        if (TextUtils.isEmpty(content)) {
            LogUtils.d("发来的业务数据为空");
            return;
        }
        TransferMessage transferMessage = GsonUtils.fromJson(araProtocol.getContent(), TransferMessage.class);
        if (transferMessage == null) {
            LogUtils.d("发来的业务数据解析出错");
            return;
        }
        String businessMessage = transferMessage.getBusinessMessage();
        BusinessBean businessBean = GsonUtils.fromJson(businessMessage, BusinessBean.class);
        if (businessBean == null) {
            LogUtils.d("发来的业务数据解析出错");
            sendMessage(transferMessage.getTransferId(), generalErrorResult("业务数据解析异常"));
            return;
        }
        if (businessBean.getType() == NettyConstant.BUSINESS_TYPE_SYC_DATA) {
            LogUtils.d("业务数据需要更新");
            EquipHeartbeat equipHeartbeat = GsonUtils.fromJson(businessBean.getRequestContext(), EquipHeartbeat.class);
            sendMessage(transferMessage.getTransferId(), generalOkResult());
            // TODO: 2023-07-27 执行业务
        } else if (businessBean.getType() == NettyConstant.BUSINESS_TYPE_INSTRUCTION) {
            LogUtils.d("需要执行指令");
            sendMessage(transferMessage.getTransferId(), generalOkResult());
            // TODO: 2023-07-27 执行指令
        } else {
            LogUtils.d("未知的业务数据类型");
            sendMessage(transferMessage.getTransferId(), generalErrorResult("未知的业务类型"));
        }
    }


    public void sendMessage(String fid, BaseHttpResult result) {
        boolean isConnect = channel != null && channel.isActive();
        if (!isConnect) {
            LogUtils.d("------尚未连接");
            return;
        }
        AraProtocol protocolBean = new AraProtocol();
        TransferMessage transferMessage = new TransferMessage();
        transferMessage.setTransferId(fid);
        transferMessage.setBusinessMessage(GsonUtils.toJson(result));
        String msg = GsonUtils.toJson(transferMessage);
        protocolBean.setContent(msg);
        protocolBean.setFlag(NettyConstant.FLAG_BUSINESS);
        protocolBean.setType(NettyConstant.TYPE_TRUEFACE);
        protocolBean.setLength(msg.getBytes(StandardCharsets.UTF_8).length);
        channel.writeAndFlush(protocolBean).addListener(new FutureListener() {
            @Override
            public void success() {
//                    LogUtils.d( "发送成功--->字节数:" + protocolBean.getContent().length() + "," + protocolBean.getContent());

            }

            @Override
            public void error() {
//                    LogUtils.d( "发送失败--->" + protocolBean.getContent());
            }
        });
    }


    public synchronized boolean getConnectStatus() {
        return connectStatus;
    }


    public synchronized void close() {
        try {
            if (nioEventLoopGroup != null) {
                nioEventLoopGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
                setConnectStatus(false);
                nioEventLoopGroup = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void setConnectStatus(boolean b) {
        connectStatus = b;
    }

    private BaseHttpResult generalOkResult() {
        BaseHttpResult baseHttpResult = new BaseHttpResult();
        baseHttpResult.setCode("0");
        baseHttpResult.setMessage("success");
        return baseHttpResult;
    }

    private BaseHttpResult generalErrorResult(String error) {
        BaseHttpResult baseHttpResult = new BaseHttpResult();
        baseHttpResult.setCode("-1");
        baseHttpResult.setMessage(error);
        return baseHttpResult;
    }
}
