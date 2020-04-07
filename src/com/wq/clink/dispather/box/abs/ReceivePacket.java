package com.wq.clink.dispather.box.abs;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午2:26
 */
public abstract class ReceivePacket<T extends OutputStream, Entity> extends Packet<T> {
    private Entity entity;

    public ReceivePacket(long length) {
        this.length = length;
    }


    public Entity entity(){
        return entity;
    }

    protected abstract Entity buildEntity(T stream);

    @Override
    protected final void closeStream(T stream) throws IOException {
        super.closeStream(stream);
        entity = buildEntity(stream);
    }





}
