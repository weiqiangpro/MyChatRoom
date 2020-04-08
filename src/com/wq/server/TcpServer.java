package com.wq.server;

import com.wq.clink.dispather.box.abs.SendPacket;
import com.wq.server.handle.ServerHandler;
import com.wq.utils.constants.CloseUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpServer implements ServerHandler.CallBack {

    private final File path;
    private ServerListener serverListener;
    private List<ServerHandler> serverHandlerList = new ArrayList<ServerHandler>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ServerSocketChannel serverSocketChannel = null;

    public TcpServer(File path) {
        this.path = path;
    }

    public boolean start(int port) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            this.serverSocketChannel = serverSocketChannel;
            System.out.println("服务器信息：" + serverSocketChannel.getLocalAddress());
            serverListener = new ServerListener(selector);
            serverListener.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("端口号被占用");
            return false;
        }
        return true;
    }

    public void stop() {
        if (serverListener != null) {
            serverListener.exit();
            serverListener = null;
        }
        CloseUtil.close(serverSocketChannel);
        synchronized (this) {
            for (ServerHandler clientHandler : serverHandlerList) {
                clientHandler.exit();
            }
            serverHandlerList.clear();
        }
        executorService.shutdown();
    }

    public synchronized void send(SendPacket packet) {
        for (int i = serverHandlerList.size()-1 ; i>=0;i--){
            serverHandlerList.get(i).send(packet);
        }
    }

    public synchronized void send(String str) {
        for (int i = serverHandlerList.size()-1 ; i>=0;i--){
            serverHandlerList.get(i).send(str);
        }
    }

    @Override
    public void onArriveMes(ServerHandler serverHandler, String mes) {
        executorService.execute(() -> {
            synchronized (TcpServer.this) {
                for (int i = serverHandlerList.size()-1 ; i>=0;i--){
                        if ( serverHandlerList.get(i).equals(serverHandler))
                            continue;
                    serverHandlerList.get(i).send(mes);
                    }
            }
        });
    }

    @Override
    public synchronized void onCloseSelf(ServerHandler serverHandler) {
        serverHandlerList.remove(serverHandler);
    }

    private class ServerListener extends Thread {
        private Selector selector;
        private boolean done = false;
        ServerListener(Selector selector) {
            this.selector = selector;
        }
        public void run() {
            super.run();
            System.out.println("服务器准备就绪～");
            Selector selector = this.selector;
            while (!done) {
                try {
                    if (selector.select() == 0) {
                        if (done)
                            break;
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        if (done)
                            break;
                        SelectionKey next = iterator.next();
                        iterator.remove();
                        if (next.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) next.channel();
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            ServerHandler clientHandler = new ServerHandler(socketChannel, TcpServer.this, path);
                            serverHandlerList.add(clientHandler);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("TcpServer --113");
                    continue;
                }
            }
            System.out.println("服务器已关闭！");
        }
        void exit() {
            done = true;
            selector.wakeup();
        }
    }
}
