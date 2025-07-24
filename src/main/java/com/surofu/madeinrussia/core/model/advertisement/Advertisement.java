package com.surofu.madeinrussia.core.model.advertisement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private AdvertisementImage image;

    @Embedded
    private AdvertisementIsBig isBig;

    @Embedded
    private AdvertisementExpirationDate expirationDate;

    @Embedded
    private AdvertisementCreationDate creationDate;

    @Embedded
    private AdvertisementLastModificationDate lastModificationDate;
}
