package com.wq.server.handle;

import com.wq.clink.Connector;
import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.ReceivePacket;
import com.wq.utils.Foo;
import com.wq.utils.constants.CloseUtil;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ServerHandler extends  Connector{
    private SocketChannel client;
    private boolean done = false;
    private String info;
    private CallBack callBack;
    private final File path;
    public ServerHandler(SocketChannel client, CallBack callBack,File file) throws IOException {
        this.path =file;
        this.callBack = callBack;
            this.info = "客户端：[" + client.getRemoteAddress().toString() + "]";
        setup(client);
    }

    @Override
    public boolean equals(Object obj) {
        return info.equals(((ServerHandler) obj).getInfo());
    }

    public String getInfo() {
        return info;
    }

    @Override
    protected File createNewFile() {
        return Foo.createRandomTemp(path);
    }

    @Override
    protected void onReceivePacket(ReceivePacket packet) {
        super.onReceivePacket(packet);
        if (packet.type() == Packet.TYPE_MEMORY_STRING){
            String str = (String) packet.entity();
            System.out.println(key.toString()+":"+str);
            callBack.onArriveMes(this,str);
        }
    }



    public void exit() {
        CloseUtil.close(client);
        callBack.onCloseSelf(ServerHandler.this);
        System.out.println( this.info+" 已退出");
    }

    public interface CallBack {
        void onArriveMes(ServerHandler serverHandler, String mes);
        void onCloseSelf(ServerHandler serverHandler);
    }
}