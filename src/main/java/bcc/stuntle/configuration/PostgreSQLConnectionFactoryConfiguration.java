package bcc.stuntle.configuration;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.Duration;

@Configuration
class PostgreSQLConnectionFactoryConfiguration {

    @Value("${db.r2dbc.username}")
    private String username;
    @Value("${db.r2dbc.password}")
    private String password;
    @Value("${db.r2dbc.url}")
    private String url;

    @Value("${db.r2dbc.host}")
    private String host;

    @Value("${db.r2dbc.port}")
    private String port;

    @Value("${db.r2dbc.database}")
    private String db;

    @Bean
    public PostgresqlConnectionFactory postgresqlConnectionFactory(){
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration
                        .builder()
                        .host(this.host)
                        .port(Integer.parseInt(this.port))
                        .database(this.db)
                        .username(this.username)
                        .password(this.password)
                        .connectTimeout(Duration.ofSeconds(5))
                        .build()
        );
    }

    @Bean
    public DatabaseClient databaseClient(){
        return DatabaseClient
                .builder()
                .connectionFactory(postgresqlConnectionFactory())
                .build();
    }
}
