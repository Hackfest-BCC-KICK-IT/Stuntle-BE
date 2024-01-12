package bcc.stuntle.app.chat.repository;

import bcc.stuntle.entity.ChatMessage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends R2dbcRepository<ChatMessage, Long> {
    @Query("SELECT * FROM chat_message WHERE fk_ortu_id=:fk_ortu_id")
    Flux<ChatMessage> findByOrtuId(@Param("fk_ortu_id") Long ortuId);
}
