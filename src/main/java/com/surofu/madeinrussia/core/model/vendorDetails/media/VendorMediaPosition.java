package com.surofu.madeinrussia.core.model.vendorDetails.media;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorMediaPosition implements Serializable {

    @Column(name = "position", nullable = false)
    private Integer value = 0;

    private VendorMediaPosition(Integer position) {
        this.value = Objects.requireNonNullElse(position, 0);
    }

    public static VendorMediaPosition of(Integer position) {
        return new VendorMediaPosition(position);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
