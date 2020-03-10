package com.wq.linck.core;

import com.wq.linck.callback.OnArrivedAndReadNext;

import java.io.Closeable;
import java.io.IOException;

public interface Receiver extends Closeable {
  //  boolean receiveAsync(IoArgs.IoArgsEventListener listener) throws IOException;
    boolean receiveAsync(OnArrivedAndReadNext callBack) throws IOException;
}
