package bcc.stuntle.app.dashboard.service;

import bcc.stuntle.entity.Dashboard;
import bcc.stuntle.entity.Response;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IDashboardService {
    Mono<ResponseEntity<Response<Dashboard>>> getDashboard(Long faskesId);
}
