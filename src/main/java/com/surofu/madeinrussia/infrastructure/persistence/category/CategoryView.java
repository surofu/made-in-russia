package com.surofu.madeinrussia.infrastructure.persistence.category;

import java.time.ZonedDateTime;

public interface CategoryView {
    Long getId();

    String getName();

    String getSlug();

    String getImage();

    Long getParentId();

    Long getChildrenCount();

    ZonedDateTime getCreationDate();

    ZonedDateTime getLastModificationDate();
}
