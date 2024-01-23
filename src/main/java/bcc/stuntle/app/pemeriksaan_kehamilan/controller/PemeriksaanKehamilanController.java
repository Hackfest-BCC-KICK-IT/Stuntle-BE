package bcc.stuntle.app.pemeriksaan_kehamilan.controller;

import bcc.stuntle.app.pemeriksaan_kehamilan.service.IPemeriksaanKehamilanService;
import bcc.stuntle.dto.DataPemeriksaanKehamilanDto;
import bcc.stuntle.entity.DataPemeriksaanKehamilan;
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

@Tag(name = "Pemeriksaan Kehamilan")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/pemeriksaan/kehamilan")
@PreAuthorize("hasAnyRole('ORANGTUA', FASKES')")
public class PemeriksaanKehamilanController {

    @Autowired
    private IPemeriksaanKehamilanService service;

    @Operation(summary = "membuat data pemeriksaan kehamilan")
    @Parameter(name = "id", in = ParameterIn.QUERY, required = true, description = "orangtua id")
    @Parameter(name = "data_kehamilan_id", in = ParameterIn.QUERY, required = true, description = "data kehamilan id")
    @PostMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<DataPemeriksaanKehamilan>>> create(
            @RequestParam("id") Long id,
            @RequestParam("data_kehamilan_id") Long dataKehamilanId,
            @Valid @RequestBody DataPemeriksaanKehamilanDto.Create dto,
            JwtAuthentication<String> jwtAuth
            ){
        return this.service.create(id, Long.parseLong(jwtAuth.getId()), dataKehamilanId, dto);
    }

    @Operation(summary = "mendapatkan data pemeriksaan kehamilan")
    @Parameter(name = "id", in = ParameterIn.QUERY, required = true, description = "orangtua id")
    @Parameter(name = "data_kehamilan_id", in = ParameterIn.QUERY, required = true, description = "data kehamilan id")
    @Parameter(name = "page", in = ParameterIn.QUERY, description = "page")
    @Parameter(name = "limit", in = ParameterIn.QUERY, description = "limit")
    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<DataPemeriksaanKehamilan>>>> getList(
            @RequestParam("id") Long id,
            @RequestParam("data_kehamilan_id") Long dataKehamilanId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            JwtAuthentication<String> jwtAuth
    ){
        return this.service.getList(id, Long.parseLong(jwtAuth.getId()), dataKehamilanId, PageRequest.of(page, limit));
    }

    @Operation(summary = "mendapatkan list data pemeriksaan kehamilan(mobile)")
    @GetMapping(
            value = "/ortu/list",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<DataPemeriksaanKehamilan>>>> getList(
            @RequestParam("id") List<Long> id
    ){
        return this.service.getList(id);
    }

    @Operation(summary = "mendapatkan data pemeriksaan kehamilan by id pemeriksaan(mobile)")
    @GetMapping(
            value = "/ortu/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<DataPemeriksaanKehamilan>>> get(
            @PathVariable("id") Long id
    ){
        return this.service
                .get(id);
    }
}