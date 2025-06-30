package com.surofu.madeinrussia.core.model.user.password;

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
        name = "user_passwords",
        indexes = {
                @Index(
                        name = "idx_user_passwords_user_id",
                        columnList = "user_id"
                )
        }
)
public final class UserPassword implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_passwords_user_id")
    )
    private User user;

    @Embedded
    private UserPasswordPassword password;

    @Embedded
    private UserPasswordCreationDate creationDate;

    @Embedded
    private UserPasswordLastModificationDate lastModificationDate;
}
