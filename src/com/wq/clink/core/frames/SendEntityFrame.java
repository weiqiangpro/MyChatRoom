package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public class SendEntityFrame extends AbsSendPacketFrame {

    private final long unConsumeEntityLength;
    private final ReadableByteChannel channel;

    SendEntityFrame(short indetifier,
                    long entityLength,
                    ReadableByteChannel channel,
                    SendPacket packet) {
        super((int) Math.min(entityLength, MAX_CAPACITY),
                Frame.TYPE_PACKET_ENTITY,
                Frame.FLAG_NONE,
                indetifier,
                packet);
        this.channel = channel;
        unConsumeEntityLength = entityLength - bodyRemaining;

    }

    @Override
    protected int consumeBody(IoArgs args) throws IOException {
        if (packet == null){
            return args.fillEmpty(bodyRemaining);
        }
        return args.readFrom(channel);
    }

    @Override
    public Frame buildNextFrame() {
        if (unConsumeEntityLength == 0)
            return null;
        return new SendEntityFrame(getBodyIndetifier(), unConsumeEntityLength, channel, packet);
    }
}
