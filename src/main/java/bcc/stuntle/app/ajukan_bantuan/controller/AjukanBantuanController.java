package bcc.stuntle.app.ajukan_bantuan.controller;

import bcc.stuntle.app.ajukan_bantuan.service.IAjukanBantuanService;
import bcc.stuntle.dto.AjukanBantuanDto;
import bcc.stuntle.entity.AjukanBantuan;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bantuan")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ajukan Bantuan")
public class AjukanBantuanController {

    @Autowired
    private IAjukanBantuanService ajukanBantuanService;

    @Operation(description = "membuat data ajukan bantuan")
    @PreAuthorize("hasRole('ORANGTUA')")
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<AjukanBantuan>>> create(
            JwtAuthentication<String> jwtAuth,
            @RequestBody @Valid AjukanBantuanDto.Create dto
            ){
        return this.ajukanBantuanService.create(Long.parseLong(jwtAuth.getId()), dto);
    }

    @Operation(description = "mendapatkan data ajukan bantuan orangtua(dari sisi orangtua)")
    @PreAuthorize("hasRole('ORANGTUA')")
    @Parameter(name = "limit", in = ParameterIn.QUERY)
    @Parameter(name = "page", in = ParameterIn.QUERY)
    @GetMapping(
            value = "/ortu",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<AjukanBantuan>>>> getListOrangtua(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ){
        return this.ajukanBantuanService.getListOrtu(Long.parseLong(jwtAuth.getId()), PageRequest.of(page, limit));
    }

    @Operation(description = "mendapatkan data ajukan bantuan orangtua(dari sisi faskes)")
    @PreAuthorize("hasRole('FASKES')")
    @Parameter(name = "limit", in = ParameterIn.QUERY)
    @Parameter(name = "page", in = ParameterIn.QUERY)
    @GetMapping(
            value = "/faskes",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<AjukanBantuan>>>> getListFaskes(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "statusAjuan") String statusAjuan
    ){
        return this.ajukanBantuanService.getListFaskes(Long.parseLong(jwtAuth.getId()), statusAjuan, PageRequest.of(page, limit));
    }

    @Operation(description = "mendapatkan data ajukan bantuan berdasarkan id")
    @PreAuthorize("hasAnyRole('ORANGTUA', 'FASKES')")
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<AjukanBantuan>>> get(
            @PathVariable("id") Long id
    ){
        return this.ajukanBantuanService.get(id);
    }

    @Operation(description = "mengupdate data ajukan bantuan")
    @PreAuthorize("hasRole('FASKES')")
    @PutMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<AjukanBantuan>>> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid AjukanBantuanDto.Update dto
    ){
        return this.ajukanBantuanService.update(id, dto);
    }

    @Operation(description = "mendapatkan statistik data ajukan bantuan dari faskes")
    @PreAuthorize("hasRole('FASKES')")
    @GetMapping(
            value = "/count",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Map<String, ?>>>> count(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.ajukanBantuanService.count(Long.parseLong(jwtAuthentication.getId()));
    }
}
