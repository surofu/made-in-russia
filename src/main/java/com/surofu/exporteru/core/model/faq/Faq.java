package com.surofu.exporteru.core.model.faq;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Faq that)) {
      return false;
    }
    if (id == null || that.id == null) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
