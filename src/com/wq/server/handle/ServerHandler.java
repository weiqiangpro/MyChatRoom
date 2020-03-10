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
            @Override
            public void onChannelClosed(SocketChannel channel) {
                super.onChannelClosed(channel);
                try {
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onReceiveNewMessage(String str) {
                super.onReceiveNewMessage(str);
                callBack.onArriveMes(ServerHandler.this,str);
            }
        };

        connector.setup(client);

            //写选择器
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
        //readHandler.exit();
        writeHandler.exit();
        CloseUtil.close(client);
        callBack.onCloseSelf(ServerHandler.this);
        try {
            System.out.println("客户端已退出：" + client.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                try {

                    while (!done){

                        if (selector.select() == 0){
                            if (done)
                                break;
                            continue;
                        }

                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()){
                            if (done)
                                break;
                            SelectionKey next = iterator.next();
                            if (next.isWritable()){
                                SocketChannel channel = (SocketChannel) next.channel();
                                byteBuffer.clear();
                                byteBuffer.put((str+"\n").getBytes());
                                byteBuffer.flip();

                                int write = channel.write(byteBuffer);
                                if (write<0){
                                    System.out.println("客户端已无法发送数据！");
                                    ServerHandler.this.exit();
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }
}