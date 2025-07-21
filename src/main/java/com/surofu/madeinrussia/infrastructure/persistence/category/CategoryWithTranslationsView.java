package com.surofu.madeinrussia.infrastructure.persistence.category;

import java.time.Instant;

public interface CategoryWithTranslationsView {
    Long getId();

    Long getParentCategoryId();

    String getName();

    String getNameTranslations();

    String getSlug();

    String getImageUrl();

    Long getChildrenCount();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
