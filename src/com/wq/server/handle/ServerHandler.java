package com.wq.server.handle;

import com.wq.linck.Connector;
import com.wq.utils.constants.CloseUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler {
    private SocketChannel client;
    private boolean done = false;
    private WriteHandler writeHandler;
    private String info;
    private CallBack callBack;
    private final Connector connector;

    public ServerHandler(SocketChannel client, CallBack callBack) throws IOException {
        this.callBack = callBack;
        this.client = client;

        connector = new Connector() {
//            @Override
//            public void onChannelClosed(SocketChannel channel) {
//                super.onChannelClosed(channel);
//                try {
//                    close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            @Override
            protected void onReceiveNewMessage(String str) {
                super.onReceiveNewMessage(str);
                callBack.onArriveMes(ServerHandler.this,str);
            }
        };
        connector.setup(client);
            Selector writeSelector = Selector.open();
            client.register(writeSelector, SelectionKey.OP_WRITE);

            writeHandler = new WriteHandler(writeSelector);

            this.info = "客户端：[" + client.getRemoteAddress().toString() + "]";


    }

    @Override
    public boolean equals(Object obj) {
        return info.equals(((ServerHandler) obj).getInfo());
    }

    public String getInfo() {
        return info;
    }

    public void send(String str) {
        if (done)
            return;
        writeHandler.send(str);
    }

    public void exit() {
        writeHandler.exit();
        CloseUtil.close(client);
        callBack.onCloseSelf(ServerHandler.this);
        System.out.println( this.info+" 已退出");
    }

    public interface CallBack {
        void onArriveMes(ServerHandler serverHandler, String mes);

        void onCloseSelf(ServerHandler serverHandler);
    }


    private class WriteHandler {
        private boolean done = false;
        private final Selector selector;
        private final ExecutorService executorService;
        private final ByteBuffer byteBuffer;
        public WriteHandler(Selector selector) {
            this.byteBuffer = ByteBuffer.allocate(256);
            this.selector = selector;
            this.executorService = Executors.newSingleThreadExecutor();
        }

        void exit() {
            done = true;
            selector.wakeup();
            CloseUtil.close(selector);
            executorService.shutdownNow();
        }

        void send(String str) {
            executorService.execute(() -> {
                if (ServerHandler.this.done)
                    return;
                try {
                    byteBuffer.clear();
                    byteBuffer.put((str+"\n").getBytes());
                    byteBuffer.flip();
                    while (!done && byteBuffer.hasRemaining()){
                        int write = client.write(byteBuffer);
                        if (write<0){
                            System.out.println("客户端已无法发送数据！");
                            ServerHandler.this.exit();
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("远程主机强迫关闭了一个现有的连接");
                    ServerHandler.this.exit();
                }

            });
        }
    }
}