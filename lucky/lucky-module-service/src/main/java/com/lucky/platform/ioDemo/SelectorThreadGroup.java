package com.lucky.platform.ioDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;

/**
 * @author: Loki
 * @data: 2021-09-24 14:26
 **/
public class SelectorThreadGroup {

    ServerSocketChannel server = null;
    SelectorThread thread ;
    SelectorThreadGroup(){
        thread = new SelectorThread(this);
        new Thread(thread).start();
    }

    private void bind(int port){
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            // 注册到selector上;
            selector(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selector(Channel channel){
        SelectorThread thread = this.thread;
        thread.lbq.add(channel);
        thread.selector.wakeup();
    }

    public static void main(String[] args){
        SelectorThreadGroup group = new SelectorThreadGroup();
        group.bind(9014);

    }

}
