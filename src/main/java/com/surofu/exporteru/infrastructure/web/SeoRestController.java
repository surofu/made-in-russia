package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.core.service.seo.SeoService;
import com.surofu.exporteru.core.service.seo.operation.GetSeo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seo")
@Tag(name = "Seo")
public class SeoRestController {
    private final SeoService service;
    private final GetSeo.Result.Processor<ResponseEntity<?>> getSeoProcessor;

    @GetMapping
    @Operation(summary = "Get list of seo prepared products and vendors")
    public ResponseEntity<?> getProducts() {
        return service.getSeo().process(getSeoProcessor);
    }
}
