package bcc.stuntle.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckHealth {

    @GetMapping("/check")
    public String check(){
        return "OK!";
    }
}
