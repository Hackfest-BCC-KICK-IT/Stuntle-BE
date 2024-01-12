package bcc.stuntle.app.chat.repository;

import bcc.stuntle.entity.ChatResponse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatResponseRepository extends R2dbcRepository<ChatResponse, Long>{
    @Query("SELECT * FROM chat_response WHERE fk_ortu_id=:fk_ortu_id")
    Flux<ChatResponse> findByOrtuId(@Param("fk_ortu_id") Long ortuId);
}
