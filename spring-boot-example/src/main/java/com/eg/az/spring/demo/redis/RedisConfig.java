package com.eg.az.spring.demo.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

	@Autowired
	private Environment env;
	
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		// Add some specific configuration here. Key serializers, etc.
		return template;
	}

//	@Bean
//	public RedisConnectionFactory redisConnectionFactory() {
//
//		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//				.commandTimeout(Duration.ofMillis(1000)).build();
//
//		return new LettuceConnectionFactory(config, clientConfig);
//	}
	
//	@Bean
//	public LettuceClientConfiguration lettuceClientConfiguration() {
//		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//		.commandTimeout(Duration.ofMillis(Long.parseLong(env.getProperty("spring.redis.lettuce.xclient.command-timeout")))).build();
//
//		return clientConfig;
//	}

}
