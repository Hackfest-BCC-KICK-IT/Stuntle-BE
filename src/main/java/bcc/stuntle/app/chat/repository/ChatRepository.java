package bcc.stuntle.app.chat.repository;

import bcc.stuntle.entity.ChatResponseUsage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends R2dbcRepository<ChatResponseUsage, Long> {}
