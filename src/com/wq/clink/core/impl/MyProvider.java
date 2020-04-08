package com.wq.clink.core.impl;

import com.wq.clink.core.IoProvider;
import com.wq.utils.constants.CloseUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyProvider implements IoProvider, Closeable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicBoolean inReg = new AtomicBoolean(false);
    private final AtomicBoolean outReg = new AtomicBoolean(false);
    private final Selector readSelector;
    private final Selector writeSelector;
    private final HashMap<SelectionKey, Runnable> inputHashMap = new HashMap<>();
    private final HashMap<SelectionKey, Runnable> outputHashMap = new HashMap<>();
    private final ExecutorService inputHandel = Executors.newFixedThreadPool(4);
    private final ExecutorService outputHandel = Executors.newFixedThreadPool(4);

    public MyProvider() throws IOException {
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
        startRead();
        startWrite();
    }

    private void startRead() {
        Thread thread = new SelectThread("Clink IoSelectorProvider ReadSelector Thread",
                inReg, readSelector, inputHashMap, inputHandel, isClosed, SelectionKey.OP_READ);
        thread.start();
    }


    private void startWrite() {
        Thread thread = new SelectThread("Clink IoSelectorProvider WriteSelector Thread",
                outReg, writeSelector, outputHashMap, outputHandel, isClosed, SelectionKey.OP_WRITE);
        thread.start();
    }

    @Override
    public boolean writeRegister(SocketChannel channel, Runnable runnable) {
        return resign(channel, runnable, outReg, writeSelector, outputHashMap, SelectionKey.OP_WRITE);
    }

    @Override
    public boolean readRegister(SocketChannel channel, Runnable runnable) {
        return resign(channel, runnable, inReg, readSelector, inputHashMap, SelectionKey.OP_READ);
    }

    @SuppressWarnings("all")
    private boolean resign(SocketChannel channel, Runnable runnable, AtomicBoolean lock, Selector selector, HashMap hashMap, int op) {
        synchronized (lock) {
            lock.set(true);
            try {
                selector.wakeup();
                SelectionKey key = null;
                if (channel.isRegistered()) {
                    key = channel.keyFor(selector);
                    if (key != null)
                        key.interestOps(key.readyOps() | op);
                }
                if (key == null) {
                    key = channel.register(selector, op);
                    hashMap.put(key, runnable);
                }
                return key != null;
            } catch (ClosedChannelException | CancelledKeyException | ClosedSelectorException e) {
                return false;
            } finally {
                lock.set(false);
                try {
                    lock.notify();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void waitSelection(final AtomicBoolean locker) {
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
    public void unReadRegister(SocketChannel channel) {
        unRegister(inReg, readSelector, channel, inputHashMap);
    }

    @Override
    public void unWriteRegister(SocketChannel channel) {
        unRegister(outReg, writeSelector, channel, outputHashMap);
    }

    private void unRegister(AtomicBoolean locker, Selector selector, SocketChannel channel, HashMap hashMap) {
        synchronized (locker) {
            locker.set(true);
            selector.wakeup();
            try {
                if (channel.isRegistered()) {
                    SelectionKey key = channel.keyFor(selector);
                    if (key != null) {
                        // 取消监听的方法
                        key.cancel();
                        hashMap.remove(key);
                        selector.wakeup();
                    }
                }
            } finally {
                locker.set(false);
                try {
                    locker.notifyAll();
                } catch (Exception ignored) {
                }
            }

        }
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            inputHandel.shutdown();
            outputHandel.shutdown();
            inputHashMap.clear();
            outputHashMap.clear();
            CloseUtil.close(readSelector, writeSelector);
        }
    }

    static class SelectThread extends Thread {
        private final AtomicBoolean locker;
        private final AtomicBoolean isClosed;
        private final Selector selector;
        private final HashMap<SelectionKey, Runnable> callMap;
        private final ExecutorService pool;
        private final int interestOps;

        SelectThread(String name, AtomicBoolean locker, Selector selector, HashMap<SelectionKey, Runnable> callMap,
                     ExecutorService pool, AtomicBoolean isClosed, int interestOps) {
            super(name);
            this.locker = locker;
            this.selector = selector;
            this.callMap = callMap;
            this.pool = pool;
            this.isClosed = isClosed;
            this.interestOps = interestOps;
            this.setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            AtomicBoolean locker = this.locker;
            AtomicBoolean isClosed = this.isClosed;
            Selector selector = this.selector;
            HashMap<SelectionKey, Runnable> callMap = this.callMap;
            ExecutorService pool = this.pool;
            int interestOps = this.interestOps;
            while (!isClosed.get()) {
                try {
                    if (selector.select() == 0) {
                        waitSelection(locker);
                        continue;
                    } else if (locker.get()) {
                        waitSelection(locker);
                    }
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isValid()) {
                            synchronized (locker) {
                                try {
                                    selectionKey.interestOps(selectionKey.readyOps() & ~interestOps);
                                } catch (CancellationException e) {
                                    continue;
                                }
                            }
                            selector.wakeup();
                            pool.execute(callMap.get(selectionKey));
                        }
                        iterator.remove();
                    }
                    selectionKeys.clear();
                } catch (ClosedChannelException ignored){
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (CancelledKeyException ignored){}
            }
        }
    }
}
