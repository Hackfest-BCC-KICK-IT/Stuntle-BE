package bcc.stuntle.app.orang_tua_faskes.controller;

import bcc.stuntle.app.orang_tua_faskes.service.IOrangtuaFaskesService;
import bcc.stuntle.entity.OrangtuaFaskes;
import bcc.stuntle.entity.OrangtuaFaskesDescription;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Orangtua Faskes")
@RequestMapping("/ortu")
@SecurityRequirement(name = "bearerAuth")
@RestController
@Slf4j
public class OrangtuaFaskesController {

    @Autowired
    private IOrangtuaFaskesService service;

    @Parameter(hidden = true)
    @Operation(description = "membuat koneksi antara orangtua dengan faskes")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses membuat fasilitas kesehatan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @Parameter(description = "kode unik faskes", in = ParameterIn.QUERY, name = "kode_unik", required = true)
    @PreAuthorize("hasRole('ORANGTUA')")
    @PostMapping("/faskes/kode/{kode_unik}")
    public Mono<ResponseEntity<Response<OrangtuaFaskes>>> create(
            @RequestParam("kode_unik") String kodeUnik,
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.connectFaskes(Long.parseLong(jwtAuthentication.getId()), kodeUnik);
    }

    @Operation(description = "mencari jumlah koneksi orangtua pada suatu faskes")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses membuat fasilitas kesehatan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @Parameter(description = "page", in = ParameterIn.QUERY, name = "page")
    @Parameter(description = "limit", in = ParameterIn.QUERY, name = "limit")
    @PreAuthorize("hasRole('FASKES')")
    @GetMapping("/faskes")
    public Mono<ResponseEntity<Response<List<OrangtuaFaskes>>>> getList(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            JwtAuthentication<String> jwtAuthentication
    ){
        return Mono.from(this.service.getList(Long.parseLong(jwtAuthentication.getId()), PageRequest.of(page, limit)));
    }

    @Operation(description = "mencari deskripsi hubungan orangtua pada suatu faskes")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses mendapatkan data deskripsi ortu fasilitas kesehatan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @PreAuthorize("hasRole('ORANGTUA')")
    @GetMapping("/faskes/description")
    public Mono<ResponseEntity<Response<OrangtuaFaskesDescription>>> getOrtuFaskes(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.getOrtuFaskes(Long.parseLong(jwtAuthentication.getId()));
    }
}
