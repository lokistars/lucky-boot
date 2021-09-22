package com.lucky.platform.ioDemo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Bio demo
 * @author: Loki
 * @data: 2021-09-19 22:29
 **/

public class SocketBio {


    public static void main(String[] args) throws Exception{
        bioServerTest();
    }

    /**
     * bio 服务端
     * @throws Exception
     */
    public static void bioServerTest() throws Exception{
        // 绑定的端口,指定客户连接请求队列的长度
        ServerSocket server = new ServerSocket(9090,2);
        while (true){
            // 从连接请求队列中取出一个客户的连接请求，然后创建与客户连接的Socket对象，
            // 并将它返回。如果队列中没有连接请求，accept()方法就会一直等待
            Socket client = server.accept();
            System.out.println("client\t" + client.getPort());
            //创建一个线程 因为是阻塞的 所以需要多个线程
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
            executor.execute(()->{
                System.out.println("client 已连接");
                try (BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()))){
                    char[] by = new char[1024];
                    while (true){
                        final int read = buf.read(by);
                        if (read > 0) {
                            System.out.println("client read some data is :" + read + " val :" + new String(by, 0, read));
                        } else if (read == 0) {
                            System.out.println("client readed nothing!");
                            continue;
                        } else {
                            System.out.println("client readed -1...");
                            System.in.read();
                            client.close();
                            break;
                        }
                    }
                    /*String str = "";
                    while ((str = buf.readLine()) != null){
                        System.out.println("获取到数据:" +str);
                    }
                    client.close();*/
                    System.out.println("客户端断开连接");
                }catch(Exception e){
                    e.printStackTrace();
                }
            });
        }
    }


}
