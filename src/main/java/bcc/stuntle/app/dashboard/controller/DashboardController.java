package bcc.stuntle.app.dashboard.controller;

import bcc.stuntle.app.dashboard.service.IDashboardService;
import bcc.stuntle.entity.Dashboard;
import bcc.stuntle.entity.Response;
import bcc.stuntle.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController("/dashboard")
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('FASKES')")
public class DashboardController {

    @Autowired
    private IDashboardService service;

    @Operation(description = "mendapatkan data dashboard secara keseluruhan")
    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public Mono<ResponseEntity<Response<Dashboard>>> getList(
            JwtAuthentication<String> jwtAuthentication
    ){
        return this.service.getDashboard(Long.parseLong(jwtAuthentication.getId()));
    }
}
