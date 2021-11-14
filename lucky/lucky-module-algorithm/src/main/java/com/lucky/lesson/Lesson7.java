package com.lucky.lesson;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 加强堆
 * @author: Loki
 * @data: 2021-11-13 17:44
 **/
public class Lesson7 {
    /**
     * 最大线段重合问题
     * 每个线段都有两个数[start,end] ,线段的开始和结束一定都是整数值
     * 线段重合区域长度必须>0,找出线段最多重合区域中,有几条线段
     * 先把线段根据开始位置进行排序, 把每个线段的结束位置存放到小根堆中
     * 判断当前线段开始位置是不是比堆中最小的结束位置要大,如果大弹出堆中元素
     */
    private static int maxCover(int[][] arr){
        final int len = arr.length;
        Line[] lines = new Line[len];
        for (int i = 0; i < len; i++) {
            lines[i] = new Line(arr[i][0],arr[i][1]);
        }
        Arrays.sort(lines,new EndComparator());
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        for (int i = 0; i < len; i++) {
            while (!queue.isEmpty() && queue.peek() <= lines[i].start){
                queue.poll();
            }
            queue.add(lines[i].end);
        }
        return queue.size();
    }

    private static class Line{
        private int start;
        private int end;
        public Line(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
    private static class EndComparator implements Comparator<Line>{
        @Override
        public int compare(Line o1, Line o2) {
            return o1.start - o2.start;
        }
    }

    /**
     * 给定一个整数数组arr[],和一个布尔类型数组op[], 两个数组等长
     * arr[] 表示客户编号,op[] 表示的客户操作
     */
}
