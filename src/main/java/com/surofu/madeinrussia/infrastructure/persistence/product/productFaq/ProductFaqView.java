package com.surofu.madeinrussia.infrastructure.persistence.product.productFaq;

import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqAnswer;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqCreationDate;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqLastModificationDate;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaqQuestion;

public interface ProductFaqView {
    Long getId();

    ProductFaqQuestion getQuestion();

    ProductFaqAnswer getAnswer();

    ProductFaqCreationDate getCreationDate();

    ProductFaqLastModificationDate getLastModificationDate();
}
