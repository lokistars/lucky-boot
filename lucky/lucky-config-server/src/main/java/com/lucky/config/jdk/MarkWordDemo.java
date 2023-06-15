package com.lucky.config.jdk;

import com.lucky.config.entity.User;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

/**
 * -XX:-UseCompressedOops 关闭指针压缩
 * -XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0 关闭偏向锁延迟
 * @author: Loki
 * @data: 2022-06-01 20:40
 */
public class MarkWordDemo {

    static User user;

    public static void main(String[] args) {
        sync();
    }

    /**
     * 因为只有一个程序调用,所以synchronized加的是偏向锁。
     */
    public static void sync(){
        user = new User();
        // 打印JVM的详细信息
        System.out.println("详细："+ VM.current().details());
        final String printable = ClassLayout.parseInstance(user).toPrintable();
        // biased 表示偏向锁标记,计算了hashCode就没有偏向锁了。
        System.out.println("没有："+printable);
        synchronized (user){
            System.out.println("加锁："+ ClassLayout.parseInstance(user).toPrintable());
        }
        final String printable2 = ClassLayout.parseInstance(user).toPrintable();

        System.out.println("计算："+printable2);
    }
}
