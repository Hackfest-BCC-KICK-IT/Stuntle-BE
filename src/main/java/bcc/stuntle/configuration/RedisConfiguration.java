package bcc.stuntle.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConfigurationProperties("application.yaml")
public class RedisConfiguration {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private Integer port;

    @Value("${redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisClientConfiguration(){
        var connection = new RedisStandaloneConfiguration(this.host, this.port);
        connection.setPassword(RedisPassword.of(password.isEmpty() ? "" : password));
        return new LettuceConnectionFactory(
                connection
        );
    }

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, String> redisTemplateConfiguration(){
        var stringSerializer = new StringRedisSerializer();
        var ctx = RedisSerializationContext
                .<String, String>newSerializationContext(stringSerializer)
                .build();
        return new ReactiveRedisTemplate<String, String>(this.redisClientConfiguration(), ctx);
    }
}
