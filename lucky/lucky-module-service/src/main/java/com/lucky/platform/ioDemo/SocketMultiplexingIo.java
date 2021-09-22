package com.lucky.platform.ioDemo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: Loki
 * @data: 2021-09-22 14:56
 **/
public class SocketMultiplexingIo {

    private ServerSocketChannel server = null;
    // 多路复用器 linux select poll epoll win kqueue
    private Selector selector = null;
    public static void main(String[] args) throws Exception{
        SocketMultiplexingIo io = new SocketMultiplexingIo();
        io.start();
    }

    /**
     * 
     * @throws Exception
     */
    public void start() throws Exception{
        initServer();
        System.out.println("服务启动了");
        while (true){
            Set<SelectionKey> keys = selector.keys();
            //调用多路复用器(select,poll  or  epoll  (epoll_wait))
            //select，poll  其实  内核的select（fd4）  poll(fd4)
            //epoll：  其实 内核的 epoll_wait() 系统调用
            //参数可以带时间： 没设置阻塞，有时间设置一个超时
            //selector.wakeup()  结果返回0
            while (selector.select()>0){
                // 返回所有有状态的fd集合
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iter = keySet.iterator();
                //不管什么多路复用器 返回的有状态集合都需要去处理他们的读和写
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    // set 不移除会重复循环处理
                    iter.remove();
                    if (key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println("新客户端：" + client.getRemoteAddress());
                    }else if(key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        buffer.clear();
                        int read = 0;
                        while (true) {
                            read = client.read(buffer);
                            if (read > 0) {
                                buffer.flip();
                                while (buffer.hasRemaining()) {
                                    client.write(buffer);
                                }
                                buffer.clear();
                            } else if (read == 0) {
                                break;
                            } else {
                                client.close();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void initServer() throws Exception{
        server = ServerSocketChannel.open();
        //
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(9090));
        // 优先选择：epoll 在epoll 模型下 open--> epoll_create() 系统调用返回fd3
        selector = Selector.open();
        // epoll：epoll_ctl(fd3,ADD,fd4,  系统调用
        // select,poll:JVM里开辟一个数组 fd放进去
        server.register(selector, SelectionKey.OP_ACCEPT);
    }


}
