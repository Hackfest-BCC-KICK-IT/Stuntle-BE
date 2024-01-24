package bcc.stuntle.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class OpenApiCreateMessage {
    private String message;
    private Boolean isKeywordExist;
    private String keywordType;
}
