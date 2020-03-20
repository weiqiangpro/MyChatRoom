package com.wq.linck.core.impl;

import com.wq.linck.callback.WriteCallBack;
import com.wq.linck.core.IoProvider;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyProvider implements IoProvider, Closeable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicBoolean inReg = new AtomicBoolean(false);
    private final Selector readSelector;
    private final HashMap<SelectionKey, SocketChannelAdapter> inputHashMap = new HashMap<>();
    private final ExecutorService inputHandel = Executors.newFixedThreadPool(4);
    private final ExecutorService outputHandel = Executors.newFixedThreadPool(4);

    public MyProvider() throws IOException {
        this.readSelector = Selector.open();
        startRead();
    }

    private void startRead() {
        Thread thread = new Thread(() -> {
            while (!isClosed.get()){
                try {
                    if (readSelector.select() == 0) {
                        waitSelection(inReg);
                        continue;
                    }
                    Set<SelectionKey> selectionKeys = readSelector.selectedKeys();
                    for (SelectionKey selectionKey : selectionKeys) {

                        if (selectionKey.isReadable()) {
                            selectionKey.interestOps(selectionKey.readyOps() & ~SelectionKey.OP_READ);
                            Callable callable = null;
                            try {
                                callable = inputHashMap.get(selectionKey).readCallBack;
                            } catch (Exception ignored) { }
                            if (callable != null && !inputHandel.isShutdown()) {
                                // 异步调度
                                Future submit = inputHandel.submit(callable);
                                send(selectionKey, (String) submit.get());
                            }
                        }
                    }
                    selectionKeys.clear();
                } catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }, "Clink IoSelectorProvider ReadSelector Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    @Override
    public void send(SelectionKey selectionKey, String str) {
        waitSelection(inReg);
        Iterator<Map.Entry<SelectionKey, SocketChannelAdapter>> iterator = inputHashMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<SelectionKey, SocketChannelAdapter> next = iterator.next();
                if (next.getKey().equals(selectionKey)) {
                    continue;
                }
                WriteCallBack value = (WriteCallBack) next.getValue().writeCallBack;
                value.setIoArgs(str);
                outputHandel.execute(value);
            }
    }

    @Override
    public boolean register(SocketChannel channel, SocketChannelAdapter socketChannelAdapter) {
        synchronized (inReg) {
            inReg.set(true);
            try {
                readSelector.wakeup();
                SelectionKey key = null;
                if (channel.isRegistered()) {
                    key = channel.keyFor(readSelector);
                    if (key != null)
                        key.interestOps(key.readyOps() | SelectionKey.OP_READ );
                }
                if (key == null) {
                    key = channel.register(readSelector, SelectionKey.OP_READ);
                    inputHashMap.put(key, socketChannelAdapter);
                }
                return key!=null;
            } catch (ClosedChannelException e) {
                return false;
            } finally {
                inReg.set(false);
                try {
                    inReg.notify();
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
    public void unRegister(SocketChannel channel) {
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
}
