package bcc.stuntle.app.ortu_resep_makanan_artikel_tersimpan.controller;

import bcc.stuntle.app.ortu_resep_makanan_artikel_tersimpan.service.IOrtuResepMakananArtikelService;
import bcc.stuntle.dto.ResepMakananArtikelTersimpanDto;
import bcc.stuntle.entity.ResepMakananArtikelTersimpan;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/ortu/resepmakanan/artikel")
@Tag(name = "ResepMakanan/Artikel tersimpan")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ORANGTUA')")
@Slf4j
public class OrtuResepMakananArtikelController {

    @Autowired
    private IOrtuResepMakananArtikelService service;

    @Operation(description = "membuat resep makanan dengan artikel dan orangtua")
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<ResepMakananArtikelTersimpan>>> create(
            JwtAuthentication<String> jwtAuthentication,
            @RequestBody @Valid ResepMakananArtikelTersimpanDto.Create dto
            ){
        log.info("masuk ke controller create");
        return this.service.create(Long.parseLong(jwtAuthentication.getId()), dto);
    }

    @Operation(description = "mendapatkan resep makanan dengan artikel dan orangtua")
    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<ResepMakananArtikelTersimpan>>> get(
            JwtAuthentication<String> jwtAuthentication,
            @RequestParam("fk_artikel_id") Long artikelId,
            @RequestParam("fk_resep_makanan_id") Long resepMakananId,
            @RequestParam("jenis") String jenis
    ){
        return this.service.get(Long.parseLong(jwtAuthentication.getId()), resepMakananId, artikelId, jenis);
    }

    @Operation(description = "mengaktifkan resep makanan dengan artikel dan orangtua yang sudah dihapus")
    @PatchMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> activate(
            JwtAuthentication<String> jwtAuthentication,
            @RequestParam("fk_artikel_id") Long artikelId,
            @RequestParam("fk_resep_makanan_id") Long resepMakananId,
            @RequestParam("jenis") String jenis
    ){
        return this.service.activate(Long.parseLong(jwtAuthentication.getId()), resepMakananId, artikelId, jenis);
    }

    @Operation(description = "menghapus resep makanan dengan artikel dan orangtua yang sudah dihapus")
    @DeleteMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Void>>> delete(
            JwtAuthentication<String> jwtAuthentication,
            @RequestParam("fk_artikel_id") Long artikelId,
            @RequestParam("fk_resep_makanan_id") Long resepMakananId,
            @RequestParam("jenis") String jenis
    ){
        return this.service.delete(Long.parseLong(jwtAuthentication.getId()), resepMakananId, artikelId, jenis);
    }

    @Operation(description = "mendapatkan list resep makanan dengan artikel dan orangtua")
    @GetMapping(
            value = "/list",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<List<ResepMakananArtikelTersimpan>>>> getList(
            JwtAuthentication<String> jwtAuthentication,
            @RequestParam("limit") Integer limit,
            @RequestParam("page") Integer page
    ){
        return this.service.getList(Long.parseLong(jwtAuthentication.getId()), PageRequest.of(page, limit));
    }
}
