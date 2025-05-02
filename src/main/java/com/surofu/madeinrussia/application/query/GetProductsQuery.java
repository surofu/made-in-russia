package com.surofu.madeinrussia.application.query;

import lombok.Data;
import lombok.Value;
import org.springframework.data.domain.Pageable;

@Data
@Value(staticConstructor = "of")
public class GetProductsQuery {
    Pageable pageable;
}
