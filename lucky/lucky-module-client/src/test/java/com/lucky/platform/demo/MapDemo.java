package com.lucky.platform.demo;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @program: lucky
 * @description: Map 存储的是Key - value 键值映射的格式
 * @author: Loki
 * @create: 2020-11-05 15:09
 **/
public class MapDemo {
    /**
     * jdk 1.7 数据结构 数组+ 链表   jdk 1.8 数组+链表 +红黑树
     * @param args
     */
    public static void main(String[] args) {
        try {

            RandomAccessFile file = new RandomAccessFile("D:\\a.txt", "rw");
            FileChannel channel = file.getChannel();
            /*FileChannel channel = new FileOutputStream("D:\\a.txt").getChannel();*/
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 20);
            map.put("123456789".getBytes());
            System.out.println("map ---");
            channel.write(map);
            channel.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
