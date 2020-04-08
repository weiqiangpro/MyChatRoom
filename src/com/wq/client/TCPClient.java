package com.wq.client;


import com.wq.client.bean.ServerInfo;
import com.wq.clink.Connector;
import com.wq.clink.Context;
import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.ReceivePacket;
import com.wq.ui.chatroom.UiAreMex;
import com.wq.utils.Foo;
import com.wq.utils.constants.CloseUtil;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class TCPClient extends Connector {

    private final File path;
    private UiAreMex uiAreMex;
    public TCPClient(SocketChannel socket,File path) throws IOException {
       setup(socket);
       this.path = path;
    }

    public void setUiAreMex(UiAreMex uiAreMex) {
        this.uiAreMex = uiAreMex;
    }


    public void exit() {
        CloseUtil.close(this);
    }


    public static TCPClient startWith(ServerInfo info,File path) throws IOException {
        SocketChannel socket = SocketChannel.open();

        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()));

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress());
        System.out.println("服务器信息：" + socket.getRemoteAddress());
        try {
            return new TCPClient(socket,path);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接异常");
            CloseUtil.close(socket);
        }
        return null;
    }

    @Override
    protected void onReceivePacket(ReceivePacket packet) {
        //super.onReceivePacket(packet);
        if (packet.type() == Packet.TYPE_MEMORY_STRING){
            String str = (String) packet.entity();
            uiAreMex.onArrive(key.toString()+":"+str+"\n");
//            System.out.println(key.toString()+":"+str);
        }else {
            uiAreMex.onArrive("收到新文件\n");
        }

    }

    public void onChannelClosed(SocketChannel channel) {
        System.out.println("链接关闭");
    }

    @Override
    protected File createNewFile() {
        return Foo.createRandomTemp(path);
    }

    @Override
    protected File createNewFile(String name) {
        return Foo.createRandomTemp(path,name);
    }
}
