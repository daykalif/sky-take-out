package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration    // 表示当前类是一个配置类，会被Spring IOC容器扫描到并管理
@Slf4j
public class RedisConfiguration {
	@Bean
	public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		log.info("开始创建RedisTemplate对象...");
		RedisTemplate redisTemplate = new RedisTemplate();

		// 设置redis的连接工厂对象
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// 设置key的序列化器；如果不设置，默认使用JDK的序列化方式，存储的key和value会是乱码
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		return redisTemplate;
	}
}
