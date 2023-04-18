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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: lucky
 * @description: es 配置类
 * @author: Loki
 * @data: 2023-04-18 10:03
 **/
@Configuration
public class ElasticConfig {

    public ElasticsearchTransport elasticsearchTransport(){
        String username = "elastic";
        String password = "elastic@123";

        RestClient restClient  = RestClient.builder(
                new HttpHost("127.0.0.1", 9200, "http")
        ).setHttpClientConfigCallback(  httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(new BasicCredentialsProvider() {{
                setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            }});
            return httpClientBuilder;
        }).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        return transport;
    }

    //@Bean
    public ElasticsearchClient elasticsearchClient(){
        return new ElasticsearchClient(elasticsearchTransport());
    }

    //@Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient(){
        return new ElasticsearchAsyncClient(elasticsearchTransport());
    }

}
