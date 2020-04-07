package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:53
 */
public class SendHeaderFrame extends AbsSendPacketFrame {
    static final int PACKET_HEADER_FRAME_MIN_LENGTH = 6;
    private final byte[] body;

    public SendHeaderFrame(short indetifier, SendPacket packet) {
        super(PACKET_HEADER_FRAME_MIN_LENGTH, Frame.TYPE_PACKET_HEADER, Frame.FLAG_NONE, indetifier, packet);
        long packetLength = packet.getLength();
        byte packetType = packet.type();
        byte[] packetHeaderInfo = packet.headInfo();
        this.body = new byte[bodyRemaining];
        body[0] = (byte) (packetLength >> 32);
        body[1] = (byte) (packetLength >> 24);
        body[2] = (byte) (packetLength >> 16);
        body[3] = (byte) (packetLength >> 8);
        body[4] = (byte) packetLength;
        body[5] = packetType;

        if (packetHeaderInfo != null) {
            System.arraycopy(packetHeaderInfo, 0, body, PACKET_HEADER_FRAME_MIN_LENGTH, packetHeaderInfo.length);
        }


    }

    @Override
    protected int consumeBody(IoArgs args) throws IOException {
        int count = bodyRemaining;
        int offset = body.length - count;
        return args.readFrom(body, offset, count);
    }

    @Override
    public SendEntityFrame buildNextFrame() {
        InputStream stream  = packet.open();
        ReadableByteChannel channel = Channels.newChannel(stream);

        return new SendEntityFrame(getBodyIndetifier(),packet.getLength(),channel,packet);

    }
}
