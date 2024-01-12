package bcc.stuntle.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class OpenApiClientResponse {
    private List<?> messages;
    private List<?> responses;
}
