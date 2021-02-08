package com.pjm.nettyservice.socket;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

        Message message = new Message();

        message.setMagicNumber(byteBuf.readInt());  // 读取魔数

        message.setMainVersion(byteBuf.readByte()); // 读取主版本号

        message.setSubVersion(byteBuf.readByte()); // 读取次版本号

        message.setModifyVersion(byteBuf.readByte());	// 读取修订版本号

        CharSequence sessionId = byteBuf.readCharSequence(

                Constants.SESSION_ID_LENGTH, Charset.defaultCharset());	// 读取sessionId

        message.setSessionId((String)sessionId);

        message.setMessageType(MessageTypeEnum.get(byteBuf.readByte()));	// 读取当前的消息类型

        short attachmentSize = byteBuf.readShort();	// 读取附件长度

        for (short i = 0; i < attachmentSize; i++) {

            int keyLength = byteBuf.readInt();	// 读取键长度和数据

            CharSequence key = byteBuf.readCharSequence(keyLength, Charset.defaultCharset());

            int valueLength = byteBuf.readInt();	// 读取值长度和数据

            CharSequence value = byteBuf.readCharSequence(valueLength, Charset.defaultCharset());

            message.addAttachment(key.toString(), value.toString());

        }



        int bodyLength = byteBuf.readInt();	// 读取消息体长度和数据

        CharSequence body = byteBuf.readCharSequence(bodyLength, Charset.defaultCharset());

        message.setBody(body.toString());

        out.add(message);

    }

}

