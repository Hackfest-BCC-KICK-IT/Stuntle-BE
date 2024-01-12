package bcc.stuntle.app.faskes.controller;

import bcc.stuntle.app.faskes.service.IFasilitasKesehatanService;
import bcc.stuntle.dto.FasilitasKesehatanDto;
import bcc.stuntle.entity.FasilitasKesehatan;
import bcc.stuntle.entity.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/faskes")
@Tag(name = "Fasilitas Kesehatan")
public class FasilitasKesehatanController {

    @Autowired
    private IFasilitasKesehatanService service;

    @Operation(description = "membuat fasilitas kesehatan")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses membuat fasilitas kesehatan",
                    useReturnTypeSchema = true,
                    responseCode = "201"
            )
    })
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Mono<ResponseEntity<Response<FasilitasKesehatan>>> create(
            @Valid
            @RequestBody
            FasilitasKesehatanDto.Create dto
    ){
        return this.service.create(dto);
    }

    @Operation(description = "login fasilitas kesehatan")
    @ApiResponses({
            @ApiResponse(
                    description = "sukses membuat fasilitas kesehatan",
                    useReturnTypeSchema = true,
                    responseCode = "200"
            )
    })
    @PostMapping(
            value = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Mono<ResponseEntity<Response<FasilitasKesehatan>>> login(
            @Valid
            @RequestBody
            FasilitasKesehatanDto.Login dto
    ){
        return this.service.login(dto);
    }
}
