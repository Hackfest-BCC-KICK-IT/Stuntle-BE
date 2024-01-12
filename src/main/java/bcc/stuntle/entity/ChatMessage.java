package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ChatMessage {

    @Id
    private Long id;

    private String message;

    private Long fkOrtuId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;
}
