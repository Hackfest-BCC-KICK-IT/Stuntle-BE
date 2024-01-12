package bcc.stuntle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationResult<T>{

    private int currentElement;
    private long totalElement;
    private int currentPage;
    private int totalPage;

    @JsonIgnore
    private T data;
}
