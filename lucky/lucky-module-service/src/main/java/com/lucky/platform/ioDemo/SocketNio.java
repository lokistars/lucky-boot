package com.lucky.platform.ioDemo;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * @author: Loki
 * @data: 2021-09-21 17:35
 **/
public class SocketNio {

    public static void main(String[] args) throws Exception{
        nioServerTest();
    }

    public static void nioServerTest() throws Exception{
        LinkedList<SocketChannel> clients = new LinkedList<>();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(9090));
        // 设置为非阻塞 true为阻塞
        server.configureBlocking(false);
        //可以在堆里 堆外
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (true){
            Thread.sleep(1000);
            //接受客户端的连接  会存在空转的情况,这里会不阻塞异步的
            final SocketChannel client = server.accept();
            if (client != null){
                //连接后的数据读写使用的 把客户端设置为非阻塞
                client.configureBlocking(false);
                final int port = client.socket().getPort();
                System.out.println("client\t" + port);
                clients.add(client);
            }else{
                System.out.println("不会阻塞");
            }
            //接收的时候需要处理客户端的数据 每次循环全部客户端会很慢
            //每一次都需要read系统调用,询问数据是否到达,用户态切换到内核态
            for (SocketChannel socket : clients) {
                // >0  -1  0   //不会阻塞
                int read = socket.read(buffer);
                if (read > 0){
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);
                    String b = new String(aaa);
                    System.out.println(socket.socket().getPort() + " : " + b);
                    buffer.clear();
                } else if (read == 0) {
                    System.out.println("client readed nothing!");
                    continue;
                } else {
                    System.out.println("client readed -1...");
                    socket.close();
                    clients.remove(socket);
                    break;
                }
            }
        }
    }
}
