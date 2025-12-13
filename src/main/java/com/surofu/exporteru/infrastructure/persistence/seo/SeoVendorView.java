package com.surofu.exporteru.infrastructure.persistence.seo;


import java.time.Instant;

public interface SeoVendorView {
    Long getId();
    Instant getRegisteredAt();
    Instant getUpdatedAt();
}
