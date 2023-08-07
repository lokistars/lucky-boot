package com.lucky.gateway.config;

import com.github.xiaoymin.knife4j.spring.gateway.Knife4jGatewayProperties;
import com.github.xiaoymin.knife4j.spring.gateway.discover.ServiceDiscoverHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Map;

//@Primary
//@Component
public class SwaggerServiceDiscoverHandler extends ServiceDiscoverHandler {

    @Autowired
    private DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator;

    @Autowired
    private DiscoveryLocatorProperties locatorProperties;

    public SwaggerServiceDiscoverHandler(Knife4jGatewayProperties properties){
        super(properties);
    }

    @Override
    public void discover(List<String> service) {
        super.discover(service);

        this.getGatewayResources().forEach((item)->{
            item.setName(analysis(item.getName(),true));
            item.setContextPath(analysis(item.getContextPath(),true));
            item.setUrl(analysis(item.getUrl(),false));
        });

        /*discoveryClientRouteDefinitionLocator.getRouteDefinitions()
                .filter(route->routes.contains(route.getId()))
                .filter(route->appName!=null && !route.getId().contains(appName))
                .subscribe(route->route.getPredicates().stream()
                        .filter(predicate->null)
                        .forEach());*/
        List<PredicateDefinition> predicates = locatorProperties.getPredicates();
        if (CollectionUtils.isEmpty(predicates)) {
            return;
        }
        for (PredicateDefinition predicate : predicates) {
            if (predicate.getName().equals("Path")){
                final Map<String, String> args = predicate.getArgs();
                final String replaceStr = "serviceId.toLowerCase()" +
                        ".replace(\"lucky\",\"\")" +
                        ".replace(\"client\",\"\")" +
                        ".replace(\"service\",\"\")" +
                        ".replace(\"admin\",\"\")" +
                        ".replace(\"api\",\"\")" +
                        ".replace(\"app\",\"\")" +
                        ".replace(\"-\",\"\")";
                args.computeIfPresent(RoutePredicateFactory.PATTERN_KEY, (k, v) ->
                        v.replace("serviceId", replaceStr));
            }
        }
    }

    private String  analysis(String name,Boolean type){
        if (StringUtils.hasText(name)){
            String replace = name.replace("lucky-", "");
            int i = replace.indexOf("-");
            if (type){
                return replace.substring(0,i);
            }else{
                return replace.replaceFirst("^*-[a-zA-Z]+/","/");
            }
        }
        return null;
    }
}
