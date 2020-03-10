package com.wq.linck;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:30
 */
public interface IoProvider extends Closeable {
    boolean registerInput(SocketChannel channel,HandleInputCallback callback);

    void unRegisterInput(SocketChannel channel);

    abstract class HandleInputCallback implements Runnable {
        @Override
        public final void run() {
            canProviderInput();
        }

        protected abstract void canProviderInput();
    }

}
