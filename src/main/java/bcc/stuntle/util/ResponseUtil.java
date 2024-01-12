package bcc.stuntle.util;

import bcc.stuntle.entity.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class ResponseUtil {

    public static <T> ResponseEntity<Response<T>> sendResponse(HttpStatus status, Response<T> data){
        return ResponseEntity
                .status(status.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
}
