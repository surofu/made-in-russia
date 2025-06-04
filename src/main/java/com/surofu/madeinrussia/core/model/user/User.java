package com.surofu.madeinrussia.core.model.user;

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
        name = "users",
        indexes = @Index(columnList = "email, login, phoneNumber")
)
public final class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private UserRole role = UserRole.ROLE_USER;

    @Embedded
    private UserEmail email;

    @Embedded
    private UserLogin login;

    @Embedded
    private UserPhoneNumber phoneNumber;

    @Embedded
    private UserRegion region;

    @Embedded
    private UserRegistrationDate registrationDate;

    @Embedded
    private UserLastModificationDate lastModificationDate;
}
