package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ResponseMessage {

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate createdAt;
}
