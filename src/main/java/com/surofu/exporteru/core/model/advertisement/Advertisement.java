package com.surofu.exporteru.core.model.advertisement;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "advertisements")
public final class Advertisement implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Embedded
  private AdvertisementTitle title;
  @Embedded
  private AdvertisementSubtitle subtitle;
  @Embedded
  private AdvertisementThirdText thirdText;
  @Embedded
  private AdvertisementImage image;
  @Embedded
  private AdvertisementLink link;
  @Embedded
  private AdvertisementIsBig isBig;
  @Embedded
  private AdvertisementExpirationDate expirationDate;
  @Embedded
  private AdvertisementCreationDate creationDate;
  @Embedded
  private AdvertisementLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Advertisement that)) {
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
