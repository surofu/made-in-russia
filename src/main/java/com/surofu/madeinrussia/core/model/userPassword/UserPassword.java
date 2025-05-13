package com.surofu.madeinrussia.core.model.userPassword;

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
@Table(name = "users_passwords")
public final class UserPassword implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    @Embedded
    private UserPasswordPassword password;

    @Embedded
    private UserPasswordCreationDate creationDate;

    @Embedded
    private UserPasswordLastModificationDate lastModificationDate;
}
