package com.lucky.platform.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: lucky
 * @description: es 配置类
 * @author: Loki
 * @data: 2023-04-18 10:03
 **/
@Configuration
@ConditionalOnProperty(prefix="spring.elasticsearch",name = "enable",havingValue = "true",matchIfMissing = true)
public class ElasticConfig {

    @Autowired
    private ElasticsearchProperties elasticsearch;

    public ElasticsearchTransport elasticsearchTransport(){
        RestClient restClient  = RestClient.builder(createHttpHost(elasticsearch.getUris().get(0)))
                .setHttpClientConfigCallback(  httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(new BasicCredentialsProvider() {{
                setCredentials(AuthScope.ANY
                        , new UsernamePasswordCredentials(elasticsearch.getUsername(), elasticsearch.getPassword()));
            }});
            return httpClientBuilder;
        }).build();

        return new RestClientTransport(
                restClient, new JacksonJsonpMapper());
    }

    private HttpHost createHttpHost(String uri) {
        try {
            return this.createHttpHost(URI.create(uri));
        } catch (IllegalArgumentException var3) {
            return HttpHost.create(uri);
        }
    }

    private HttpHost createHttpHost(URI uri) {
        if (!StringUtils.hasLength(uri.getUserInfo())) {
            return HttpHost.create(uri.toString());
        } else {
            try {
                return HttpHost.create((new URI(uri.getScheme(), (String)null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment())).toString());
            } catch (URISyntaxException var3) {
                throw new IllegalStateException(var3);
            }
        }
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(){
        return new ElasticsearchClient(elasticsearchTransport());
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient(){
        return new ElasticsearchAsyncClient(elasticsearchTransport());
    }

}
