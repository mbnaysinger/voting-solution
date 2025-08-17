package br.com.naysinger.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "br.com.naysinger.infrastructure.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database}")
    private String authDatabase;

    @Value("${spring.data.mongodb.connection-pool.max-size:100}")
    private int maxPoolSize;

    @Value("${spring.data.mongodb.connection-pool.min-size:5}")
    private int minPoolSize;

    @Value("${spring.data.mongodb.connection-pool.max-wait-time:5000}")
    private int maxWaitTime;

    @Value("${spring.data.mongodb.socket-timeout:30000}")
    private int socketTimeout;

    @Value("${spring.data.mongodb.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${spring.data.mongodb.server-selection-timeout:30000}")
    private int serverSelectionTimeout;

    @Value("${spring.data.mongodb.max-wait-time:120000}")
    private int maxWaitTimeMs;

    @Value("${spring.data.mongodb.heartbeat-frequency:10000}")
    private int heartbeatFrequency;

    @Value("${spring.data.mongodb.min-heartbeat-frequency:500}")
    private int minHeartbeatFrequency;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
        String connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s",
                username, password, host, port, database, authDatabase);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .applyToConnectionPoolSettings(builder -> builder
                        .maxSize(maxPoolSize)
                        .minSize(minPoolSize)
                        .maxWaitTime(maxWaitTime, TimeUnit.MILLISECONDS))
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                        .readTimeout(socketTimeout, TimeUnit.MILLISECONDS))
                .applyToServerSettings(builder -> builder
                        .heartbeatFrequency(heartbeatFrequency, TimeUnit.MILLISECONDS)
                        .minHeartbeatFrequency(minHeartbeatFrequency, TimeUnit.MILLISECONDS))
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(serverSelectionTimeout, TimeUnit.MILLISECONDS))
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }
}
