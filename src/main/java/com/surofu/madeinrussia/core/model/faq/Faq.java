package com.surofu.madeinrussia.core.model.faq;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq")
public final class Faq implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private FaqQuestion question;

    @Embedded
    private FaqAnswer answer;

    @Embedded
    private FaqCreationDate creationDate;

    @Embedded
    private FaqLastModificationDate lastModificationDate;
}
