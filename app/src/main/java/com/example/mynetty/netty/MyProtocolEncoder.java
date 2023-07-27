package com.example.mynetty.netty;


import com.example.mynetty.netty.bean.AraProtocol;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyProtocolEncoder extends MessageToByteEncoder<AraProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AraProtocol msg, ByteBuf out) throws Exception {

        if(msg == null){
            throw new Exception("msg is null");
        }

        //对写入数据进行一次编码
        String content = URLEncoder.encode(msg.getContent() , "UTF-8");
        msg.setContent(content);
        msg.setLength(content.getBytes(StandardCharsets.UTF_8).length);
        out.writeByte(msg.getType());
        out.writeByte(msg.getFlag());
        out.writeInt(msg.getLength());
        out.writeBytes(content.getBytes(Charset.forName("UTF-8")));
    }
}
