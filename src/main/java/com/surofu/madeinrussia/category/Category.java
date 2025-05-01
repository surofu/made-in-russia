package com.surofu.madeinrussia.category;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastModificationDate;
}
