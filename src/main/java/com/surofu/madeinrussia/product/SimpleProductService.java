package com.surofu.madeinrussia.product;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleProductService implements ProductService {

    private final ProductJpaRepository repository;

    @Override
    public List<Product> getProducts() {
        return repository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }
}
