package com.wq.clink.core.frames;

import com.wq.clink.core.IoArgs;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public class ReceiveEntityFrame extends AbsReceiveFrame {
    private WritableByteChannel channel;

    ReceiveEntityFrame(byte[] header) {
        super(header);
    }

    public void bindPacketChannel(WritableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    protected int consumBody(IoArgs args) throws IOException {
        return channel == null ? args.setEmpty(bodyRemaining) : args.writeTo(channel);
    }
}
