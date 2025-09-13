package com.surofu.madeinrussia.infrastructure.persistence.category;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.List;

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
