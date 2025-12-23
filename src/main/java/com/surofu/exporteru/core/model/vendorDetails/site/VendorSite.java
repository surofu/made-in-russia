package com.surofu.exporteru.core.model.vendorDetails.site;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vendor_details_sites")
public final class VendorSite implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vendor_details_id")
  private VendorDetails vendorDetails;

  @Embedded
  private VendorSiteUrl url;
  @Embedded
  private VendorSiteCreationDate creationDate;
  @Embedded
  private VendorSiteLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorSite that)) {
      return false;
    }
    if (id == null || that.id == null) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
