package bcc.stuntle.entity;

import bcc.stuntle.constant.OpenApiConstant;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class OpenApiRequest {

    public record Payload(String role, String content){}

    private String model = OpenApiConstant.MODEL;

    private List<Payload> messages;

    public void add(Payload payload){
        this.messages.add(payload);
    }

    public List<Payload> get(){
        return this.messages;
    }
}
