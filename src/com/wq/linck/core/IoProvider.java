package com.wq.linck.core;

import com.wq.linck.callback.OnArrivedAndReadNext;
import com.wq.linck.callback.ReadCallBack;
import com.wq.linck.callback.WriteCallBack;
import com.wq.linck.core.impl.SocketChannelAdapter;

import java.io.Closeable;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:30
 */
public interface IoProvider extends Closeable {
    void send(SelectionKey selectionKey, String str);

    boolean register(SocketChannel channel, SocketChannelAdapter socketChannelAdapter);
    void unRegister(SocketChannel channel);
//    abstract class HandleInputCallback implements Runnable {
//        @Override
//        public final void run() {
//            canProviderInput();
//        }
//
//        protected abstract void canProviderInput();
//    }

}
