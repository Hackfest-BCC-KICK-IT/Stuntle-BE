package bcc.stuntle.configuration;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
@Import(PostgreSQLConnectionFactoryConfiguration.class)
class R2dbcTemplateConfiguration {

    @Autowired
    private PostgresqlConnectionFactory connectionFactory;

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(){
        return new R2dbcEntityTemplate(this.connectionFactory);
    }
}
