package bcc.stuntle.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public final class Response<T>{

    private String message;
    private T data;
    private boolean success;
    private String jwtToken;
    private PaginationResult<T> pagination;

    public Response<T> putMessage(String message){
        this.message = message;
        return this;
    }
    public Response<T> putData(T data){
        this.data = data;
        return this;
    }
    public Response<T> putSuccess(boolean success){
        this.success = success;
        return this;
    }
}
