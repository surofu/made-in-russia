package com.surofu.exporteru.infrastructure.persistence.category;

import java.time.Instant;

public interface CategoryView {
    Long getId();

    Long getParentCategoryId();

    String getName();

    String getTitle();

    String getLabel();

    String getDescription();

    String getSlug();

    String getImageUrl();

    String getIconUrl();

    Long getChildrenCount();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
