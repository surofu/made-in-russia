package com.surofu.madeinrussia.core.model.vendorDetails;

import com.surofu.madeinrussia.core.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "vendor_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_vendor_details_inn",
                        columnNames = "inn"
                ),
                @UniqueConstraint(
                        name = "unique_vendor_details_company_name",
                        columnNames = "company_name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_vendor_details_user_id",
                        columnList = "user_id"
                )
        }
)
public final class VendorDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vendor_details_user_id")
    )
    private User user;
}
