package bcc.stuntle.entity;

import lombok.*;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class StorageResponse {
    private String publicId;
    private String signature;
    private String secureUrl;
}
