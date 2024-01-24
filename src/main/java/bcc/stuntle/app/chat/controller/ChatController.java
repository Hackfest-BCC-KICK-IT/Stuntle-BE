package bcc.stuntle.app.chat.controller;

import bcc.stuntle.app.chat.service.IChatService;
import bcc.stuntle.dto.ChatDto;
import bcc.stuntle.entity.OpenApiClientResponse;
import bcc.stuntle.entity.OpenApiCreateMessage;
import bcc.stuntle.entity.OpenApiResponse;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Chat with OpenAI")
@RestController
@RequestMapping("/chat")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ORANGTUA')")
public class ChatController {

    @Autowired
    private IChatService service;

    @Operation(description = "melakukan chat dengan open ai")
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<OpenApiCreateMessage>>> create(
            @RequestBody @Valid ChatDto.Create dto,
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.create(Long.parseLong(jwtAuthentication.getId()), dto.message());
    }

    @Operation(description = "mendapatkan list chat dengan open ai grouped by tanggal chat")
    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<OpenApiClientResponse>>> get(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.get(Long.parseLong(jwtAuthentication.getId()));
    }
}
