package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public abstract class AbsSendFrame extends Frame {
    volatile byte headerRemaining = Frame.FRAME_HEADER_LEN;
    int bodyRemaining ;

    public AbsSendFrame(int len, byte type, byte flag, short indetifier) {
        super(len, type, flag, indetifier);
        this.bodyRemaining = len;
    }

    @Override
    public int getConsumableLength() {
        return headerRemaining+bodyRemaining;
    }

    @Override
    public synchronized boolean handle(IoArgs args) throws IOException {
        try {

        args.limt(headerRemaining+bodyRemaining);
        args.startWriting();
        if (headerRemaining > 0 && args.remained()){
            headerRemaining -= consumeHeader(args);
        }
        if (headerRemaining ==0 && args.remained() && bodyRemaining > 0){
            bodyRemaining -= consumeBody(args);
        }

        return headerRemaining ==0 && bodyRemaining == 0;
        }finally {
            args.finishWriting();
        }
    }

    private  byte consumeHeader(IoArgs args){
        int count = headerRemaining;
        int offset = header.length - count;
     return    (byte)args.readFrom(header,offset,count);
    }

    protected abstract int consumeBody(IoArgs args) throws IOException;

    protected synchronized   boolean isSending(){
        return headerRemaining < Frame.FRAME_HEADER_LEN;
    }
}
