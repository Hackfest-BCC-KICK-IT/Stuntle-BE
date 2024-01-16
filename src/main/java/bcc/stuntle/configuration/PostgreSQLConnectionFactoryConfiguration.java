package bcc.stuntle.configuration;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({
        FlywayProperties.class
})
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

    @Bean(initMethod = "migrate")
    public Flyway flyway(
        FlywayProperties props
    ){
        return Flyway
                .configure()
                .dataSource(
                        props.getUrl(),
                        this.username,
                        this.password
                )
                .baselineOnMigrate(true)
                .load();
    }
}
