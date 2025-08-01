package com.surofu.madeinrussia.infrastructure.web;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
public class SwaggerController {

    @GetMapping("/swagger")
    public String RedirectToSwaggerPage() {
        return "redirect:/api/swagger-ui/index.html";
    }
}
