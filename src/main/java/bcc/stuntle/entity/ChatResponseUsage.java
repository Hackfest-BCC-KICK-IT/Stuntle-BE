package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "chat_response_usage")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ChatResponseUsage {

    @Id
    private Long id;

    private Long totalTokens;

    private Long fkOrtuId;

    private Long fkChatResponse;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate deletedAt;
}
