package com.surofu.madeinrussia.product;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/products")
public class ProductRestController {

    private final ProductService service;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok(service.getProducts());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        var product = service.getProductById(id);

        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
