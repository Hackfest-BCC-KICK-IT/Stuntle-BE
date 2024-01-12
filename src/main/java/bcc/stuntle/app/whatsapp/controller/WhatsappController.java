package bcc.stuntle.app.whatsapp.controller;

import bcc.stuntle.app.whatsapp.service.IWhatsappService;
import bcc.stuntle.dto.WhatsappDto;
import bcc.stuntle.entity.GrupWhatsapp;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Grup Whatsapp")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/whatsapp")
@Slf4j
public class WhatsappController {

    @Autowired
    private IWhatsappService service;

    @Operation(summary = "membuat grup whatsapp")
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('FASKES')")
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> create(
            JwtAuthentication<String> jwtAuth,
            @RequestBody WhatsappDto.CreateUpdate dto
            ){
        return this.service.create(Long.parseLong(jwtAuth.getId()), dto);
    }

    @Operation(summary = "menghapus grup whatsapp")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "whatsapp id")
    @DeleteMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('FASKES')")
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> delete(
            @PathVariable("id") Long whatsappId
    ){
        return this.service.delete(whatsappId);
    }

    @Operation(summary = "mengupdate grup whatsapp")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "whatsapp id")
    @PutMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('FASKES')")
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> update(
            @PathVariable("id") Long whatsappId,
            @RequestBody WhatsappDto.CreateUpdate dto
    ){
        return this.service.update(whatsappId, dto);
    }

    @Operation(summary = "mendapatkan grup whatsapp")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "whatsapp id")
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('FASKES')")
    public Mono<ResponseEntity<Response<GrupWhatsapp>>> get(
            @PathVariable("id") Long whatsappId
    ){
        return this.service.get(whatsappId);
    }

    @Operation(summary = "mendapatkan list grup whatsapp dari faskes")
    @Parameter(name = "limit", in = ParameterIn.QUERY, description = "limit")
    @Parameter(name = "page", in = ParameterIn.QUERY, description = "page")
    @GetMapping(
            value = "/faskes",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('FASKES')")
    public Mono<ResponseEntity<Response<List<GrupWhatsapp>>>> getListFaskes(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page
    ){
        return this.service.getList(Long.parseLong(jwtAuth.getId()), PageRequest.of(page, limit));
    }

    @Operation(summary = "mendapatkan list grup whatsapp dari ortu")
    @Parameter(name = "limit", in = ParameterIn.QUERY, description = "limit")
    @Parameter(name = "page", in = ParameterIn.QUERY, description = "page")
    @GetMapping(
            value = "/ortu",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('ORANGTUA')")
    public Mono<ResponseEntity<Response<List<GrupWhatsapp>>>> getListOrtu(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page
    ){
        return this.service.getListForOrtu(Long.parseLong(jwtAuth.getId()), PageRequest.of(page, limit));
    }
}
