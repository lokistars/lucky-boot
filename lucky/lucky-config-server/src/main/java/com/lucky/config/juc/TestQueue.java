package com.lucky.config.juc;

import com.lucky.config.util.MyPriorityQueue;

/**
 * @program: lucky
 * @description:
 * @author: Loki
 * @data: 2023-06-22 12:10
 **/
public class TestQueue extends MyPriorityQueue{


    public static void main(String[] args) {
        TestQueue queue = new TestQueue();

        TestPriorityQueue();
    }



    private static void TestPriorityQueue(){
        MyPriorityQueue<Integer> queue = new MyPriorityQueue<>(10);
        queue.offer(1);
        queue.offer(3);
        queue.offer(4);
        queue.offer(9);
        queue.offer(2);
        queue.offer(5);
        queue.offer(6);
        System.out.println(queue.poll());
        System.out.println(queue.poll());

    }

}
