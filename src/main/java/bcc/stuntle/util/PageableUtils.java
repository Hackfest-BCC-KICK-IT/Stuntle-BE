package bcc.stuntle.util;

import org.springframework.data.domain.Pageable;

public class PageableUtils {

    public static String toString(Pageable pageable){
        return "{" +
                String.format("\"currentPage\": %s", pageable.isUnpaged() ? "null" : pageable.getPageNumber()) +
                String.format("\"currentSize\": %s", pageable.isUnpaged() ? "null" : pageable.getPageSize()) +
                String.format("\"offset\": %s", pageable.isUnpaged() ? "null" : pageable.getOffset()) +
                String.format("\"sort\": %s", pageable.isUnpaged() ? "null" : pageable.getSort()) +
                "}";
    }
}
