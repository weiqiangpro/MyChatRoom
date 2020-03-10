package com.wq.linck.impl;

import com.wq.linck.IoProvider;
import java.io.IOException;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:34
 */
public class Provider implements IoProvider {
    private final AtomicBoolean isClosed  =  new AtomicBoolean(false);

    private final AtomicBoolean inRegIn  =  new AtomicBoolean(false);

    private final Selector readSelector;

    private final HashMap<SelectionKey,Runnable> inputHashMap = new HashMap<>();

    private final ExecutorService inputHandel = Executors.newFixedThreadPool(4,new IoProviderThreadFactory("read_pool"));

    public Provider() throws IOException {
        this.readSelector = Selector.open();
        startRead();
    }

    private void startRead() {
        Thread thread = new Thread("Clink IoSelectorProvider ReadSelector Thread") {
            @Override
            public void run() {
                while (!isClosed.get()) {
                    try {
                        if (readSelector.select() == 0) {
                            waitSelection(inRegIn);
                            continue;
                        }

                        Set<SelectionKey> selectionKeys = readSelector.selectedKeys();
                        for (SelectionKey selectionKey : selectionKeys) {

                            if (selectionKey.isValid()) {
                                selectionKey.interestOps(selectionKey.readyOps() & ~SelectionKey.OP_READ);
                                Runnable runnable = null;
                                try {
                                    runnable = inputHashMap.get(selectionKey);
                                } catch (Exception ignored) { }
                                if (runnable != null && !inputHandel.isShutdown()) {
                                    // 异步调度
                                    inputHandel.execute(runnable);
                                }
                            }
                        }
                        selectionKeys.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    @Override
    public boolean registerInput(SocketChannel channel, HandleInputCallback callback) {

        synchronized (inRegIn) {
            // 设置锁定状态
            inRegIn.set(true);

            try {
                // 唤醒当前的selector，让selector不处于select()状态
                readSelector.wakeup();

                SelectionKey key = null;
                if (channel.isRegistered()) {
                    // 查询是否已经注册过
                    key = channel.keyFor(readSelector);
                    if (key != null) {
                        key.interestOps(key.readyOps() | SelectionKey.OP_READ);
                    }
                }

                if (key == null) {
                    // 注册selector得到Key
                    key = channel.register(readSelector, SelectionKey.OP_READ);
                    // 注册回调
                    inputHashMap.put(key, callback);
                }

                return true;
            } catch (ClosedChannelException e) {
                return false;
            } finally {
                // 解除锁定状态
                inRegIn.set(false);
                try {
                    // 通知
                    inRegIn.notify();
                } catch (Exception ignored) {
                }
            }
        }
    }
    private static void waitSelection(final AtomicBoolean locker) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (locker) {
            if (locker.get()) {
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void unRegisterInput(SocketChannel channel) {
        if (channel.isRegistered()) {
            SelectionKey key = channel.keyFor(readSelector);
            if (key != null) {
                // 取消监听的方法
                key.cancel();
                inputHashMap.remove(key);
                readSelector.wakeup();
            }
        }
    }



    @Override
    public void close() throws IOException {

    }

    static class IoProviderThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        IoProviderThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
