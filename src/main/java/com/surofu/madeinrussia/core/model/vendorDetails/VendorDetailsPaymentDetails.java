package com.surofu.madeinrussia.core.model.vendorDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsPaymentDetails implements Serializable {

    @Column(name = "payment_details", nullable = false)
    private String value;

    private VendorDetailsPaymentDetails(String paymentDetails) {
        if (paymentDetails == null || paymentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Реквизиты продавца не могут быть пустыми");
        }

        if (paymentDetails.length() > 255) {
            throw new IllegalArgumentException("Реквизиты продавца не могут быть больше 255 символов");
        }

        this.value = paymentDetails;
    }

    public static VendorDetailsPaymentDetails of(String paymentDetails) {
        return new VendorDetailsPaymentDetails(paymentDetails);
    }

    @Override
    public String toString() {
        return value;
    }
}
