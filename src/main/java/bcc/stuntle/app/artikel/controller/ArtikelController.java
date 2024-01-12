package bcc.stuntle.app.artikel.controller;

import bcc.stuntle.app.artikel.service.IArtikelService;
import bcc.stuntle.dto.ArtikelDto;
import bcc.stuntle.entity.Artikel;
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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/artikel")
@Tag(name = "Artikel")
@SecurityRequirement(name = "bearerAuth")
public class ArtikelController {

    @Autowired
    private IArtikelService artikelService;

    @Operation(description = "membuat artikel")
    @PreAuthorize("hasRole('FASKES')")
    @PostMapping(
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Artikel>>> create(
            @RequestPart("dto") @Valid ArtikelDto.Create dto,
            @RequestPart("image") Mono<FilePart> file,
            JwtAuthentication<String> jwtAuth
            ){
        return this.artikelService.create(dto, Long.parseLong(jwtAuth.getId()), file);
    }

    @PreAuthorize("hasRole('FASKES')")
    @Operation(description = "mendapatkan list artikel")
    @Parameter(name = "page", in = ParameterIn.QUERY)
    @Parameter(name = "limit", in = ParameterIn.QUERY)
    @GetMapping(
            value = "/faskes/list",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<Artikel>>>> getList(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ){
        return this.artikelService.getList(Long.parseLong(jwtAuth.getId()), PageRequest.of(page, limit));
    }

    @PreAuthorize("hasRole('FASKES')")
    @Operation(description = "menghapus artikel")
    @Parameter(name = "id", in = ParameterIn.PATH)
    @DeleteMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> delete(
            @PathVariable("id") Long id
    ){
        return this.artikelService.delete(id);
    }

    @PreAuthorize("hasRole('FASKES')")
    @GetMapping(
            value = "/count",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Long>>> count(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.artikelService.count(Long.parseLong(jwtAuthentication.getId()));
    }

    @PreAuthorize("hasRole('ORANGTUA')")
    @Operation(description = "mendapatkan list artikel berdasarkan judul artikel(orangtua)")
    @Parameter(name = "page", in = ParameterIn.QUERY)
    @Parameter(name = "limit", in = ParameterIn.QUERY)
    @Parameter(name = "judulArtikel", in = ParameterIn.QUERY)
    @GetMapping(
            value = "/ortu/judul",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<Artikel>>>> getList(
            JwtAuthentication<String> jwtAuth,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "judulArtikel") String judulArtikel
    ){
        return this.artikelService.getList(judulArtikel, PageRequest.of(page, limit));
    }

    @PreAuthorize("hasRole('ORANGTUA')")
    @Operation(description = "mendapatkan list artikel(orangtua)")
    @Parameter(name = "page", in = ParameterIn.QUERY)
    @Parameter(name = "limit", in = ParameterIn.QUERY)
    @GetMapping(
            value = "/ortu/list",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<Artikel>>>> getList(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ){
        return this.artikelService.getList(PageRequest.of(page, limit));
    }
}
