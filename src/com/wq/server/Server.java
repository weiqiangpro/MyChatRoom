package com.wq.server;

import com.wq.clink.Context;
import com.wq.clink.core.impl.MyProvider;
import com.wq.clink.dispather.box.FileSendPacket;
import com.wq.utils.Foo;
import com.wq.utils.util.TCPConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {
    public static void main(String[] args) throws IOException {
        File files = Foo.getCacheDir("server");
        Context.setup(new MyProvider());

        TcpServer tcpServer = new TcpServer(files);
        boolean start = tcpServer.start(TCPConstants.PORT_SERVER);
        if (!start) {
            System.out.println("TCP服务器启动失败");
            return;
        }
        UdpServer.start(TCPConstants.PORT_SERVER);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        do {

            str = bufferedReader.readLine();
            if ("00bye00".equalsIgnoreCase(str))
                break;
            if (str.startsWith("--f")){
                String[] s = str.split(" ");
                if (s.length >=2){
                    String filePath = s[1];
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()){
                        FileSendPacket packet = new FileSendPacket(file,file.getName());
                        tcpServer.send(packet);
                        continue;
                    }
                }
            }
            tcpServer.send(str);

        } while (true);
        UdpServer.stop();
        tcpServer.stop();
        Context.close();
    }
}
