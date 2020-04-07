package com.wq.clink.dispather.box.abs;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午2:14
 */
public abstract class Packet<T extends  Closeable> implements Closeable {

    public static final byte TYPE_MEMORY_BYTES = 1;
    public static final byte TYPE_MEMORY_STRING = 2;
    public static final byte TYPE_STREAM_FILE = 3;
    public static final byte TYPE_STREAM_DIRECT = 4;

    public abstract byte type();
    protected long length;

    private T stream;
    public long getLength(){
        return length;
    }
    public final T open() {
        if (stream == null) {
            stream = createStream();
        }
        return stream;
    }
    protected abstract T createStream();



    protected void closeStream(T stream) throws IOException {
        stream.close();
    }

    public byte[] headInfo(){
        return  null;
    }

    public final void close() throws IOException {
        if (stream != null) {
            closeStream(stream);
            stream = null;
        }
    }
}
