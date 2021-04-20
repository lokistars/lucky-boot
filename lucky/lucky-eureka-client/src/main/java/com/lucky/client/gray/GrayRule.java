package com.lucky.client.gray;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Loki
 * @data: 2021-04-19 19:38
 * 灰度发布-ribbon
 **/
public class GrayRule extends AbstractLoadBalancerRule {

    private static ThreadLocal local = new ThreadLocal();

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {


        return choose(getLoadBalancer(),key);
    }

    public Server choose(ILoadBalancer lb, Object key) {
        System.out.println("灰度");
        Server server = null;
        while (server == null){
            //获取所有可达的服务
            List<Server> servers = lb.getReachableServers();

            //获取当前线程的参数
            Map<String,Object> map = new HashMap<String,Object>(){{put("version","v1");}};
            for (int i = 0; i < servers.size(); i++) {
                server = servers.get(i);
                Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
                String version = metadata.get("version");

            }


        }


        return server;
    }
}
