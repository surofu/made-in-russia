package com.surofu.madeinrussia.core.model.product.productMedia;

import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_media")
public final class ProductMedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Embedded
    private ProductMediaMimeType mimeType;

    @Embedded
    private ProductMediaPosition position;

    @Embedded
    private ProductMediaUrl url;

    @Embedded
    private ProductMediaAltText altText;

    @Embedded
    private ProductMediaCreationDate creationDate;

    @Embedded
    private ProductMediaLastModificationDate lastModificationDate;
}
