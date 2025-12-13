package com.surofu.exporteru.infrastructure.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/companies")
@Tag(
    name = "Companies",
    description = "API for accessing company information by categories"
)
public class CompaniesRestController {


  @GetMapping("{categorySlug}")
  public ResponseEntity<?> findByCategoryId() {
    return ResponseEntity.ok(new ArrayList<>());
  }
}
