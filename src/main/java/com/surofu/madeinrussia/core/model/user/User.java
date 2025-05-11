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
@Table(name = "users")
public final class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded
    private UserEmail email;

    @Embedded
    private UserLogin login;

    @Embedded
    private UserRegistrationDate registrationDate;

    @Embedded
    private UserLastModificationDate lastModificationDate;
}
