package com.lucky.platform.ioDemo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: Loki
 * @data: 2021-09-23 19:15
 **/
public class SelectorThread extends ThreadLocal<LinkedBlockingQueue<Channel>> implements Runnable{


    Selector selector = null;
    // 每个线程持有自己独立的队列 get返回initialValue的对象
    LinkedBlockingQueue<Channel> lbq = get();
    SelectorThreadGroup stg;


    SelectorThread(SelectorThreadGroup stg){
        try {
            selector = Selector.open();
            this.stg = stg;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected LinkedBlockingQueue<Channel> initialValue() {
        return new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        System.out.println("服务启动了");
        while(true){
            try {
                Set<SelectionKey> keysSize = selector.keys();
                System.out.println("当前连接 keys："+keysSize.size());
                //不传超时时间会一直阻塞,select在阻塞过程中调用wakeup()会立即返回
                if(selector.select()>0){
                    Set<SelectionKey> keys = selector.selectedKeys();
                    System.out.println("SelectionKey Size："+keys.size());
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isAcceptable()){ // 接收客户端
                            acceptHandler(key);
                        }else if(key.isReadable()){ //读取数据
                            readHandler(key);
                        }else if(key.isWritable()){ //写数据

                        }
                    }
                }
                // 处理一些task
                if (!lbq.isEmpty()){
                    Channel channel = lbq.take();
                    if (channel instanceof ServerSocketChannel){
                        ServerSocketChannel server = (ServerSocketChannel) channel;
                        // 注入到selector  接收连接
                        server.register(selector,SelectionKey.OP_ACCEPT);
                        System.out.println(Thread.currentThread().getName()+" register listen");
                    }else if (channel instanceof SocketChannel){
                        SocketChannel client = (SocketChannel) channel;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        String name = Thread.currentThread().getName();
                        System.out.println(name+" register client: " + client.getRemoteAddress());
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readHandler(SelectionKey key){
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        while(true){
            try {
                int read = client.read(buffer);
                //读到数据了
                if (read> 0){
                    buffer.flip();
                    while (buffer.hasRemaining()){
                        client.write(buffer);
                    }
                    buffer.clear();
                }else if (read == 0){
                    break;
                }else {
                    key.cancel();
                    System.out.println("客户端关闭了！！！："+client.getRemoteAddress());
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void acceptHandler(SelectionKey key){
        System.out.println(Thread.currentThread().getName()+"   acceptHandler......");
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            //choose a selector  and  register!!
            this.stg.selector(client);
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
