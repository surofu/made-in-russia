package com.surofu.madeinrussia.application.query;

import lombok.Data;
import lombok.Value;

@Data
@Value(staticConstructor = "of")
public class GetProductByIdQuery {
    Long productId;
}
