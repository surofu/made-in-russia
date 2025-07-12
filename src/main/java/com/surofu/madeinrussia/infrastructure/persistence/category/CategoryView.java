package com.surofu.madeinrussia.infrastructure.persistence.category;

import java.time.Instant;

public interface CategoryView {
    Long getId();

    Long getParentCategoryId();

    String getName();

    String getSlug();

    String getImageUrl();

    Long getChildrenCount();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
