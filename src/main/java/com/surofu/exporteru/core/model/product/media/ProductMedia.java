package com.surofu.exporteru.core.model.product.media;

import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.Product;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;

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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_media_product_id")
    )
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMedia productMedia)) return false;
        return Objects.equals(url, productMedia.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
