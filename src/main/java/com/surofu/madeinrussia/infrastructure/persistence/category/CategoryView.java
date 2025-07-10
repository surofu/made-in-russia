package com.surofu.madeinrussia.infrastructure.persistence.category;

import java.time.Instant;

public interface CategoryView {
    Long getId();

    Long getParentId();

    String getName();

    String getSlug();

    String getImage();

    Long getChildrenCount();

    Instant getCreationDate();

    Instant getLastModificationDate();
}
