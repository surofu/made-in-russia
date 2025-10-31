package com.surofu.exporteru.core.model.vendorDetails.media;

import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vendor_details_media")
public class VendorMedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_details_id", nullable = false)
    private VendorDetails vendorDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Embedded
    private VendorMediaMimeType mimeType;

    @Embedded
    private VendorMediaUrl url;

    @Embedded
    private VendorMediaPosition position;

    @Embedded
    private VendorMediaCreationDate creationDate;

    @Embedded
    private VendorMediaLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorMedia)) return false;
        return this.id != null && this.id.equals(((VendorMedia) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
