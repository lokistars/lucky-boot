package com.lucky.config.demo;

import com.lucky.config.entity.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author: Loki
 * @data: 2021-12-03 20:41
 **/
public class StreamDemo {

    public static void main(String[] args) throws Exception{
        streamList();
        //.forEach(System.out::println);
        int str = 1;
        System.in.read();
    }

    private static void stream(){
        //创建一个空流
        Stream<String> streamEmpty = Stream.empty();
        //创建一个字符串流
        Stream<String> streamOfArray = Stream.of("a", "b", "c");
        //创建一个对象流
        Stream<User> streamUser = Stream.of(new User());
        //9的新特性,允许在创建Stream的时候放入一个null
        Stream<Object> objectStream = Stream.ofNullable(null);
        //使用builder构建流时，要指定类型
        Stream<String> build = Stream.<String>builder().add("a").add("b").build();
        //需要就收一个Supplier对象作为元素生成. 还需指定流的大小，否则会一直generate()
        Stream<String> limit = Stream.generate(() -> "31").limit(10);
        Stream<Double> random = Stream.generate(Math::random).limit(10);
        //通过迭代的方式来创建流,1 ~ 10
        Stream<Integer> iterate = Stream.iterate(1, n -> n + 1).limit(10);
        //9的新特性,目标参数,中断条件,遍历方式
        Stream<Integer> iterate1 = Stream.iterate(1, n -> n > 10, t -> t + 1);
        //把两个流合并一起
        Stream<String> concat = Stream.concat(streamOfArray, limit);
    }

    private static void streamList(){
        List<String> list = new ArrayList<>();
        list.add("123");
        List<Integer> arr = Arrays.asList(1,3,4,5,8);
        //9的新特性,从头开始取数据,直到遇见不符合要求的为止,从头开始取奇数,遇到偶数为止
        Stream<Integer> takeWhile = arr.stream().takeWhile(l -> l % 2 == 1);
        //9的新特性,从头开始剔除满足条件的数据,直到遇见一个满足的为止,保留满足的
        Stream<Integer> dropWhile = arr.stream().dropWhile(l -> l % 2 == 1);
        //遍历没一个元素进行封装,映射方法去对流进行操作
        List<User> users = list.stream().map(l -> {
            User user = new User();
            user.setName(l);
            return user;
        }).collect(Collectors.toList());
        // 遍历输出单个元素
        Stream<String> nameStream = users.stream().map(User::getName);
        // 并集,合集就是不去重
        List<List<String>> lists = new ArrayList<>();
        lists.add(Collections.singletonList("321"));
        lists.add(list);
        Stream<String> distinct = lists.stream().flatMap(Collection::stream).distinct();
        // filter 过滤 交集,差集在filter中修改为不等于 contains 判断某个元素是否存在
        Stream<String> contains = list.stream().filter(list::contains);
        //接收另一个Stream流,将流中的流数据整合为一个流,再进行映射方法的操作,通过t找s,返回找到的学生
        Stream<String>  stream = list.stream().flatMap(t -> distinct.filter(s->s.equals(t)));
        stream.forEach(System.out::println);
        //返回一个IntStream,LongStream,DoubleStream 把流转换为对应类型的流
        IntStream intStream = list.stream().mapToInt(Integer::parseInt);
        //返回描述此流的第一个元素的Optional
        String first = list.stream().findFirst().orElse("");
        //返回当前的任意元素
        String findAny = list.stream().findAny().orElse("");
        //检查是否匹配所有元素123
        boolean allMatch = list.stream().allMatch("123"::equals);
        //检查是否匹配一个元素123
        boolean anyMatch = list.stream().anyMatch("123"::equals);
        // 检查123是否不匹配所属有元素
        boolean noneMatch = list.stream().noneMatch("123"::equals);
        // 合并流的元素并产生单个值,identity = 默认值或初始值,BinaryOperator = 函数式接口，取两个值并产生一个新值
        String reduce = list.stream().reduce(",", String::concat);
        // 跳过元素，返回一个丢掉前N个元素，如3只要元素3后面的数
        Stream<String> skip = list.stream().skip(2);
        //16 新特性 和flatMap类似,
        //list.stream().mapMulti()
    }

}
