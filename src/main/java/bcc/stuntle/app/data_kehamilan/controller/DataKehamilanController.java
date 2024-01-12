package bcc.stuntle.app.data_kehamilan.controller;

import bcc.stuntle.app.data_kehamilan.service.IDataKehamilanService;
import bcc.stuntle.dto.DataKehamilanDto;
import bcc.stuntle.entity.DataKehamilan;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Data Kehamilan")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/kehamilan")
@PreAuthorize("hasAnyRole('ORANGTUA', 'FASKES')")
public class DataKehamilanController {

    @Autowired
    private IDataKehamilanService service;

    @Operation(description = "membuat data kehamilan")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses membuat data kehamilan",
                    useReturnTypeSchema = true,
                    responseCode = "201"
            )
    })
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<DataKehamilan>>> create(
            @Valid @RequestBody DataKehamilanDto.Create dto,
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.create(Long.parseLong(jwtAuthentication.getId()), dto);
    }

    @Operation(description = "mendapatkan data kehamilan")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan data kehamilan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "data kehamilan id")
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<DataKehamilan>>> get(
            @PathVariable("id") Long id
    ){
        return this.service.get(id);
    }

    @Operation(description = "mendapatkan statistik data kehamilan dari faskes")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan data kehamilan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @GetMapping(
            value = "/statistik",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Map<String, Long>>>> getDataKehamilanStatistic(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.count(Long.parseLong(jwtAuthentication.getId()));
    }

    @Operation(description = "mendapatkan list data kehamilan dari orangtua")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan list data kehamilan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @GetMapping(
            value = "/list",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<DataKehamilan>>>> getList(
            JwtAuthentication<String> jwtAuthentication,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit
    ){
        return this.service.getList(Long.parseLong(jwtAuthentication.getId()), PageRequest.of(page, limit));
    }

    @Operation(description = "mengupdate data kehamilan")
    @Parameter(name = "id", description = "data kehamilan id",in = ParameterIn.PATH)
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan list data kehamilan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @PutMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid DataKehamilanDto.Update dto
    ){
        return this.service.update(id, dto);
    }

    @Operation(description = "mengupdate data kehamilan")
    @Parameter(name = "id", description = "data kehamilan id",in = ParameterIn.PATH)
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan list data kehamilan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @DeleteMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> delete(
            @PathVariable("id") Long dataKehamilanId
    ){
        return this.service.delete(dataKehamilanId);
    }
}
