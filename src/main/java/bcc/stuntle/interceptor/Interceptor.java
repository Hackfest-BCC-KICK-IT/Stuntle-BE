package bcc.stuntle.interceptor;

import bcc.stuntle.entity.Response;
import bcc.stuntle.exception.*;
import bcc.stuntle.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class Interceptor {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<Response<Object>>> handleException(RuntimeException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        Response.builder()
                                .build()
                                .putMessage(ex.getMessage() + "with line " + ex.getStackTrace()[0].getLineNumber() + " on class " + ex.getStackTrace()[0].getClassName())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<Response<Object>>> handleException(MethodArgumentNotValidException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                   HttpStatus.BAD_REQUEST,
                   Response.builder()
                           .build()
                           .putMessage(ex.getMessage())
                           .putData(null)
                           .putSuccess(false)
           )
        );
    }

    @ExceptionHandler(EmptyAuthorizationHeader.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<Response<Object>>> handleException(EmptyAuthorizationHeader ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.BAD_REQUEST,
                        Response.builder().build()
                                .putMessage(ex.getMessage())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ResponseEntity<Response<Object>>> handleException(UnauthorizedException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.UNAUTHORIZED,
                        Response.builder().build()
                                .putMessage(ex.getMessage())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }

    @ExceptionHandler(EmailSudahAdaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ResponseEntity<Response<Object>>> handleException(EmailSudahAdaException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.CONFLICT,
                        Response.builder()
                                .build()
                                .putMessage(ex.getMessage())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }

    @ExceptionHandler(KredensialTidakValidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ResponseEntity<Response<Object>>> handleException(KredensialTidakValidException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.UNAUTHORIZED,
                        Response.builder()
                                .build()
                                .putMessage(ex.getMessage())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }

    @ExceptionHandler(DataTidakDitemukanException.class)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Response<Object>>> handleException(DataTidakDitemukanException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.OK,
                        Response.builder()
                                .build()
                                .putMessage(ex.getMessage())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<ResponseEntity<Response<Object>>> handleException(ForbiddenException ex){
        return Mono.fromCallable(() -> ResponseUtil.sendResponse(
                        HttpStatus.FORBIDDEN,
                        Response.builder()
                                .build()
                                .putMessage(ex.getMessage())
                                .putData(null)
                                .putSuccess(false)
                )
        );
    }
}
