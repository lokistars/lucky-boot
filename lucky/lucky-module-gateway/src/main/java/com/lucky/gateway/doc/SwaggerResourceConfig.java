package com.lucky.gateway.doc;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Component
@Primary
public class SwaggerResourceConfig implements SwaggerResourcesProvider {
    private static final Logger log = LoggerFactory.getLogger(SwaggerResourceConfig.class);
    private final RouteLocator routeLocator;
    private final GatewayProperties gatewayProperties;
    private final DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator;
    private final Environment environment;

    public List<SwaggerResource> get() {
        String appName = this.environment.getProperty("spring.application.name");
        List<SwaggerResource> resources = new ArrayList();
        List<String> routes = new ArrayList();
        this.routeLocator.getRoutes().subscribe((route) -> {
            routes.add(route.getId());
        });
        this.discoveryClientRouteDefinitionLocator.getRouteDefinitions().filter((routeDefinition) -> {
            return routes.contains(routeDefinition.getId());
        }).filter((routeDefinition) -> {
            return appName != null && !routeDefinition.getId().contains(appName);
        }).filter((routeDefinition) -> {
            return this.gatewayProperties.getRoutes().stream().noneMatch((r) -> {
                return routeDefinition.getId().contains(r.getId());
            });
        }).subscribe((route) -> {
            route.getPredicates().stream().filter((predicateDefinition) -> {
                return "Path".equalsIgnoreCase(predicateDefinition.getName());
            }).forEach((predicateDefinition) -> {
                resources.add(this.swaggerResource(route.getId(), ((String)predicateDefinition.getArgs().get("pattern")).replace("**", "v3/api-docs")));
            });
        });

        try {
            Thread.sleep(500L);
        } catch (InterruptedException var5) {
            var5.printStackTrace();
        }

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        log.info("name:{},location:{}", name, location);
        SwaggerResource swaggerResource = new SwaggerResource();
        if (name.startsWith("ReactiveCompositeDiscoveryClient_")) {
            name = name.replace("ReactiveCompositeDiscoveryClient_", "");
        }

        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

    public SwaggerResourceConfig(final RouteLocator routeLocator, final GatewayProperties gatewayProperties, final DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator, final Environment environment) {
        this.routeLocator = routeLocator;
        this.gatewayProperties = gatewayProperties;
        this.discoveryClientRouteDefinitionLocator = discoveryClientRouteDefinitionLocator;
        this.environment = environment;
    }
}
