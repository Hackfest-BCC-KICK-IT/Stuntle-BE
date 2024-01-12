package bcc.stuntle.configuration;

import bcc.stuntle.entity.Response;
import bcc.stuntle.security.filter.JwtFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Slf4j
public class SecurityConfiguration {

    String[] authPost = {
            "/ortu/**",
            "/kehamilan/**",
            "/anak/**",
            "/resep/makanan/**"
    };

    String[] authGet = {
            "/ortu/**",
            "/orangtua/{id}",
            "/anak",
            "/resep/makanan/**"
    };

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .cors((c) -> {
                    c.configurationSource(r -> {
                        CorsConfiguration cors = new CorsConfiguration();
                        cors.setAllowedHeaders(List.of(CorsConfiguration.ALL));
                        cors.setAllowedMethods(List.of(CorsConfiguration.ALL));
                        cors.setAllowedOrigins(List.of(CorsConfiguration.ALL));
                        return cors;
                    });
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic((it) -> {

                })
                .exceptionHandling((c) -> {
                    c.authenticationEntryPoint((exchange, ex) -> {
                        try {
                            log.error("masuk ke authentication entry point");
                            var response = exchange.getResponse();
                            response.setRawStatusCode(HttpStatus.FORBIDDEN.value());
                            response.getHeaders()
                                    .setContentType(MediaType.APPLICATION_JSON);
                            return response
                                    .writeWith(returnErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage()));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).accessDeniedHandler((exchange, ex) -> {
                        try {
                            log.error("masuk ke access denied handler");
                            return exchange.getResponse()
                                    .writeWith(returnErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage()));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                })
                .authorizeExchange((it) -> {
                    it.pathMatchers(HttpMethod.POST, this.authPost)
                            .authenticated()
                            .pathMatchers(HttpMethod.GET, this.authGet)
                            .authenticated()
                            .anyExchange()
                            .permitAll();
                })
                .addFilterAfter(jwtFilter(), SecurityWebFiltersOrder.EXCEPTION_TRANSLATION)
                .build();
    }

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }

    public Mono<DataBuffer> returnErrorResponse(HttpStatus status, String message) throws JsonProcessingException {
        byte[] wrapperByte = ObjectMapperFactory
                .createJson()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .writeValueAsBytes(
                        Response.builder().message(message)
                                .success(false)
                                .build()
                );
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(wrapperByte);
        return Mono.just(dataBuffer);
    }
}
