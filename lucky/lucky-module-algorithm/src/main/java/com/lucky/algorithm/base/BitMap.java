package com.lucky.algorithm.base;

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

    //num&31 可以理解未 num%32
    public void add(int num){
        bits[num >>5] |= (1<<(num&31));
    }

    public void delete (int num){
        bits[num>>5] &= ~(1<<(num&31));
    }

    public boolean contains (int num){
        return (bits[num>>5] & (1<<(num&31)))!=0;
    }

    public static void main(String[] args) {
        BitMap bitMap = new BitMap(31);
        bitMap.add(29);
        System.out.println(bitMap.contains(30));
    }

    public void test(){

    }
}
