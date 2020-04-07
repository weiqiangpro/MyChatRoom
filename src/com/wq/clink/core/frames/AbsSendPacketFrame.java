package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 下午12:38
 */
public abstract class AbsSendPacketFrame extends  AbsSendFrame {


    public AbsSendPacketFrame(int len, byte type, byte flag, short indetifier,SendPacket packet) {
        super(len, type, flag, indetifier,packet);
    }

    /**
     * @return true 没有发送任何数据
     */
    public final synchronized boolean abort(){
        boolean isSending = isSending();
        if (isSending){
            fillDirtyDataOnAbort();
        }
        packet = null;
        return !isSending;
    }

    @Override
    public synchronized boolean handle(IoArgs args) throws IOException {
        if (packet==null && !isSending()){
            return true;
        }
        return super.handle(args);

    }

    public synchronized SendPacket getPacket() {
        return packet;
    }

    @Override
    public final synchronized Frame nextFrame() {
        return packet==null?null:buildNextFrame();
    }

    protected abstract Frame buildNextFrame();

    protected  void fillDirtyDataOnAbort(){

    }


}
