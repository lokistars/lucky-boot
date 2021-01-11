package com.lucky.platform;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @program: lucky
 * @description: zookeeper
 * @author: Loki
 * @create: 2021-01-11 16:41
 **/
public class ZookeeperTest {

    public static void main(String[]  args) throws Exception{
        String url = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        // 创建链接  传入的这个watcher 是session级别的。
        /**
         *  watcher
         */
        CountDownLatch cd = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper(url, 3000, (watcher) -> {
            Watcher.Event.KeeperState state = watcher.getState();
            Watcher.Event.EventType type = watcher.getType();
            System.out.println("watcher:"+watcher.toString());
            switch (state) {
                case Unknown:
                    break;
                case Disconnected:
                    break;
                case NoSyncConnected:
                    break;
                case SyncConnected:
                    System.out.println("链接成功");
                    cd.countDown();
                    break;
                case AuthFailed:
                    break;
                case ConnectedReadOnly:
                    break;
                case SaslAuthenticated:
                    break;
                case Expired:
                    break;
                case Closed:
                    break;
            }
        });
        cd.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("start------");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("end-------");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }
        // watch true or false
        Stat exists = zk.exists("/lucky",false);
        // ZooDefs.Ids  是否有ACL权限  CreateMode 节点类型
        if (exists == null){
            String pathName = zk.create("/lucky", "abc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            //
            Stat stat = new Stat();
            byte[] data = zk.getData("/lucky", (watcher) -> {
                System.out.println("getDate watcher :" + watcher.toString());
                try {
                    // 重复注册
                    zk.getData("/lucky",(Watcher) watcher,stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, stat);

            System.out.println("元数据:"+new String(data));

            //触发回调
            Stat stat1 = zk.setData("/lucky", "321".getBytes(), 0);

        }else {
            System.out.println("删除数据:"+exists.getVersion());
            zk.delete("/lucky",exists.getVersion());
        }
    }
}
