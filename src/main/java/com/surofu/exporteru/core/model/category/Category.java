package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.core.model.okved.OkvedCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public final class Category implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_category_id")
  private Category parent;

  @ToString.Exclude
  @OneToMany(
      mappedBy = "parent",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
      orphanRemoval = true
  )
  private Set<Category> children = new HashSet<>();

  @ToString.Exclude
  @OneToMany(
      mappedBy = "category",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
      orphanRemoval = true
  )
  private Set<OkvedCategory> okvedCategories = new HashSet<>();

  @Formula("(select count(*) from categories c where c.parent_category_id = id)")
  private Long childrenCount;
  @Embedded
  private CategorySlug slug;
  @Embedded
  private CategoryName name;
  @Embedded
  private CategoryTitle title;
  @Embedded
  private CategoryLabel label;
  @Embedded
  private CategoryDescription description;
  @Embedded
  private CategoryMetaDescription metaDescription;
  @Embedded
  private CategoryImageUrl imageUrl;
  @Embedded
  private CategoryIconUrl iconUrl;
  @Embedded
  private CategoryCreationDate creationDate;
  @Embedded
  private CategoryLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Category that)) {
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
