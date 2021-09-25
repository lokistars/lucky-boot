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
     * strace -ff -o epoll 追踪系统调用
     * socket(AF_INET6, SOCK_STREAM, IPPROTO_IP) = 6
     * fcntl(6, F_SETFL, O_RDWR|O_NONBLOCK)    = 0  //设置非阻塞
     * bind(6, {sa_family=AF_INET6, sin6_port=htons(9090)
     * listen(6, 50)
     * epoll_create(256) = 9  (文件描述符)
     * epoll_ctl(9, EPOLL_CTL_ADD, 6, {EPOLLIN, {u32=6, u64=140247862083590}}) = 0
     * epoll_wait(9, [{EPOLLIN, {u32=6, u64=140247862083590}}], 4096, -1) = 1
     * accept(6, {sa_family=AF_INET6, sin6_port=htons(36254) = 10
     * fcntl(10, F_SETFL, O_RDWR|O_NONBLOCK)   = 0
     * @throws Exception
     */
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

    /**
     *
     * @throws Exception
     */
    public void start() throws Exception{
        initServer();
        System.out.println("服务启动了");
        while (true){
            // 当前连接的key
            Set<SelectionKey> keys = selector.keys();
            System.out.println("当前连接 keys："+keys.size());
            //调用多路复用器(select,poll  or  epoll  (epoll_wait))
            //select，poll  其实  内核的select（fd4）  poll(fd4)
            //epoll：  其实 内核的 epoll_wait() 系统调用
            //参数可以带时间： 没设置会阻塞，有时间设置一个超时
            //selector.wakeup()  结果返回0
            if (selector.select()>0){
                // 返回所有有状态的fd集合
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iter = keySet.iterator();
                //不管什么多路复用器 返回的有状态集合都需要去处理他们的读和写
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    // set 不移除会重复循环处理
                    iter.remove();
                    // 有人建立连接了
                    if (key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // 连接后注册一个read
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println("新客户端：" + client.getRemoteAddress());
                    }
                    // 有人读
                    else if(key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        buffer.clear();
                        int read = 0;
                        while (true) {
                            read = client.read(buffer);
                            if (read > 0) {
                                buffer.flip();
                                while (buffer.hasRemaining()) {
                                    //读到的数据又给写回去了。
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
                    // 有人写 send -queue 队列只要是空的,一定会给你返回可以写的事件
                    //
                    else if(key.isWritable()){
                        key.cancel(); //踢出文件描述符

                    }
                }
            }
        }
    }


}
