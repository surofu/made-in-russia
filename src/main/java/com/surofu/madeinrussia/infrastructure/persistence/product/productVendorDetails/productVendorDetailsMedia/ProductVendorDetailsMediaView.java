package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.productVendorDetailsMedia;

import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaCreationDate;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaImage;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaLastModificationDate;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaPosition;

public interface ProductVendorDetailsMediaView {
    Long getId();

    MediaType getMediaType();

    ProductVendorDetailsMediaImage getImage();

    ProductVendorDetailsMediaPosition getPosition();

    ProductVendorDetailsMediaCreationDate getCreationDate();

    ProductVendorDetailsMediaLastModificationDate getLastModificationDate();
}
