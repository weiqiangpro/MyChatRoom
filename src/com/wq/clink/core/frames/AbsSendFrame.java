package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;
import com.wq.clink.dispather.box.FileSendPacket;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public abstract class AbsSendFrame extends Frame {
    protected volatile SendPacket<?> packet;
    volatile byte headerRemaining = Frame.FRAME_HEADER_LEN;
    protected byte nameReamining;
    protected byte[] name;
    int bodyRemaining;

    public AbsSendFrame(int len, byte type, byte flag, short indetifier, SendPacket packet) {
        super(len, type, flag, indetifier);
        this.bodyRemaining = len;
        this.packet = packet;
    }

    @Override
    public int getConsumableLength() {
        return headerRemaining + bodyRemaining;
    }

    @Override
    public synchronized boolean handle(IoArgs args) throws IOException {
        try {

            args.limt(headerRemaining + nameReamining + bodyRemaining);
            args.startWriting();
            if (headerRemaining > 0 && args.remained()) {
                headerRemaining -= consumeHeader(args);
            }

            if (headerRemaining == 0 && args.remained() && bodyRemaining > 0) {
                bodyRemaining -= consumeBody(args);
            }
            if (headerRemaining == 0 && bodyRemaining == 0 && args.remained() && nameReamining > 0) {
                nameReamining -= consumeName(args);
            }

            return headerRemaining == 0 && bodyRemaining == 0;
        } finally {
            args.finishWriting();
        }
    }

    private byte consumeHeader(IoArgs args) {
        int count = headerRemaining;
        int offset = header.length - count;
        return (byte) args.readFrom(header, offset, count);
    }

    private byte consumeName(IoArgs args) {
        int count = nameReamining;
        int offset = name.length - count;
        return (byte) args.readFrom(name, offset, count);
    }

    protected abstract int consumeBody(IoArgs args) throws IOException;

    protected synchronized boolean isSending() {
        return headerRemaining < Frame.FRAME_HEADER_LEN;
    }
}
