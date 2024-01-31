package com.taskmanager.challenge.config;

import com.taskmanager.challenge.model.CountingTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Configuration
public class RedisConfig {

    @Autowired
    private RedisTemplate<String, CountingTask> redisTemplate;

    @Bean
    public RedisTemplate<String, CountingTask> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CountingTask> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        Jackson2JsonRedisSerializer<CountingTask> serializer = new Jackson2JsonRedisSerializer<>(CountingTask.class);
        serializer.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));

        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @PostConstruct
    public void init() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
    }
}
