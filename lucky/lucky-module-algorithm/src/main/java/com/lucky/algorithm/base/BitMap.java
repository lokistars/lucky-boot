package com.lucky.algorithm.base;

import java.util.HashSet;

/**
 * @author: Loki
 * @data: 2021-04-09 19:31
 * 位图
 **/
public class BitMap {

    /** int 类型来作为位图 一个整数可以表示32个数 */
    private int[] bits;

    // 得到一个最大的数,初始化位图大小
    public BitMap(int max) {
        // 等同于 (max +32)/ 32
        bits = new int[(max+32)>>5];
    }

    /**
     * num&31 可以理解未 num%32  1<<(num&31) 表示第几位数,然后设置为1
     * 先确定这个数在那个下标
     *  bits[1] = bits[1] | (1<<3);
     */
    public void add(int num){
        bits[num >>5] |= (1<<(num&31));
    }

    /**
     * 找到这个数在那个下标,在第几位然后设置为 0
     * 1向左移动 3三位取反  11101111 其他数与上这个数  第三个数设置为 0
     * @param num
     */
    public void delete (int num){
        bits[num>>5] &= ~(1<<(num&31));
    }

    /**
     * 找到这个数, 然后 与一下 这个数的第3位 如果不等于 0 表示不存在
     * @param num
     * @return
     */
    public boolean contains (int num){
        return (bits[num>>5] & (1<<(num&31)))!=0;
    }

    public static void main(String[] args) {
        BitMap bitMap = new BitMap(31);
        bitMap.add(29);
        System.out.println(bitMap.contains(30));
        test();
    }

    public static void test(){
        System.out.println("测试开始！");
        int max = 10086;
        BitMap bitMap = new BitMap(max);
        HashSet<Integer> set = new HashSet<>();
        int testTime = 1000000;
        for (int i = 0; i < testTime; i++) {
            int num = (int) Math.random()*(max+1);
            double random = Math.random();
            if (random <0.33){
                bitMap.add(num);
                set.add(num);
            }else if (random <0.66){
                bitMap.delete(num);
                set.remove(num);
            }else{
                if (bitMap.contains(num) != set.contains(num)){
                    System.out.println("Oops!");
                    break;
                }
            }
        }
        for (int num = 0; num < max; num++) {
            if (bitMap.contains(num) != set.contains(num)){
                System.out.println("Oops!");
            }
        }
        System.out.println("测试结束！");
    }
}
