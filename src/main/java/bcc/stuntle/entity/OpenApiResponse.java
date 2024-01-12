package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class OpenApiResponse {
    public record MessagePayload(String role, String content){}

    public record ChoicesPayload(@JsonIgnore Long index, MessagePayload message, @JsonIgnore String finishReason){}

    public record UsagePayload(Long promptTokens, Long completionTokens, Long totalTokens){}

    private String id;

    private String object;

    private Long created;

    private List<ChoicesPayload> choices;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UsagePayload usage;
}
