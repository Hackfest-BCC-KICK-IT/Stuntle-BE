package bcc.stuntle.app.data_anak.controller;

import bcc.stuntle.app.data_anak.service.IDataAnakService;
import bcc.stuntle.dto.DataAnakDto;
import bcc.stuntle.entity.DataAnak;
import bcc.stuntle.entity.DataAnakOrtu;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Tag(name = "Data Anak")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ORANGTUA', 'FASKES')")
@RestController
@RequestMapping("/anak")
@Slf4j
public class DataAnakController {

    @Autowired
    private IDataAnakService service;

    @Operation(description = "membuat data anak")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses membuat data anak",
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
    public Mono<ResponseEntity<Response<DataAnak>>> create(
            @Valid @RequestBody DataAnakDto.Create dto,
            JwtAuthentication<String> jwtAuthentication
            ){
        return this.service.create(Long.parseLong(jwtAuthentication.getId()), dto);
    }

    @Operation(description = "mendapatkan list data anak")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan data anak",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @Parameter(description = "page", in = ParameterIn.QUERY, name = "page")
    @Parameter(description = "limit", in = ParameterIn.QUERY, name = "limit")
    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<DataAnak>>>> getList(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ){
        return this.service.getList(Long.parseLong(jwtAuth.getId()), PageRequest.of(page, limit));
    }

    @PreAuthorize("hasRole('FASKES')")
    @Operation(description = "mendapatkan statistik data anak dari faskes")
    @GetMapping(
            value = {
                    "/statistik"
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Map<String, Long>>>> count(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.count(Long.parseLong(jwtAuthentication.getId()));
    }

    @PreAuthorize("hasRole('FASKES')")
    @Operation(description = "mendapatkan data anak berdasarkan nama orangtua")
    @GetMapping(
            value = {
                    "/ortuname"
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<DataAnakOrtu>>> getByOrangtuaName(
            @RequestParam("orangtua_name") String orangtuaName,
            JwtAuthentication<String> jwtAuthentication,
            @RequestParam(value = "limit", defaultValue = "10", required = false) Long limit,
            @RequestParam(value = "page", defaultValue = "0", required = false) Long page
    ){
        DataAnakDto.SearchByName dto = new DataAnakDto.SearchByName(orangtuaName);
        return this.service.getList(Long.parseLong(jwtAuthentication.getId()), dto, PageRequest.of(page.intValue(), limit.intValue()));
    }

    @PreAuthorize("hasRole('ORANGTUA')")
    @Operation(description = "mengupdate data anak")
    @Parameter(name = "id", description = "data anak id", in = ParameterIn.PATH)
    @PutMapping(
            value = {
                    "/{id}"
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid DataAnakDto.Update dto
    ){
        return this.service.update(id, dto);
    }

    @PreAuthorize("hasRole('ORANGTUA')")
    @Operation(description = "menghapus data anak")
    @Parameter(name = "id", description = "data anak id", in = ParameterIn.PATH)
    @DeleteMapping(
            value = {
                    "/{id}"
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> delete(
            @PathVariable("id") Long id
    ){
        return this.service.delete(id);
    }

    @PreAuthorize("hasRole('ORANGTUA')")
    @Operation(description = "mendapatkan data anak")
    @Parameter(name = "id", description = "data anak id", in = ParameterIn.PATH)
    @GetMapping(
            value = {
                    "/{id}"
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<DataAnak>>> get(
            @PathVariable("id") Long id
    ){
        return this.service.get(id);
    }
}
