package bcc.stuntle.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class DataAnakOrtu {
    private List<DataAnak> dataAnak;
    private List<Orangtua> ortu;
}
