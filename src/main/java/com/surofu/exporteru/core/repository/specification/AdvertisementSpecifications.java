package com.surofu.exporteru.core.repository.specification;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class AdvertisementSpecifications {

  public static Specification<Advertisement> byText(String text) {
    return (root, query, criteriaBuilder) -> {
      if (StringUtils.trimToNull(text) == null) {
        return criteriaBuilder.conjunction();
      }

      String searchPattern = "%" + text.toLowerCase() + "%";

      Predicate titlePredicate = createJsonbSearchPredicate(
          root, criteriaBuilder, "titleTranslations", "title", searchPattern);

      Predicate subtitlePredicate = createJsonbSearchPredicate(
          root, criteriaBuilder, "subtitleTranslations", "subtitle", searchPattern);

      Predicate thirdTextPredicate = createJsonbSearchPredicate(
          root, criteriaBuilder, "thirdTextTranslations", "thirdText", searchPattern);

      return criteriaBuilder.or(titlePredicate, subtitlePredicate, thirdTextPredicate);
    };
  }

  public static Specification<Advertisement> byNotExpiredDate() {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(
        root.get("expirationDate").get("value"),
        criteriaBuilder.function("now", java.util.Date.class)
    ));
  }

  private static Predicate createJsonbSearchPredicate(
      Root<Advertisement> root,
      CriteriaBuilder cb,
      String jsonbField,
      String fallbackField,
      String searchPattern) {
    Expression<String> jsonbValues = cb.function(
        "jsonb_each_text_text",
        String.class,
        root.get(jsonbField)
    );

    Predicate jsonbSearch = cb.like(
        cb.lower(jsonbValues),
        searchPattern
    );

    Predicate fallbackSearch = cb.like(
        cb.lower(root.get(fallbackField)),
        searchPattern
    );

    return cb.or(jsonbSearch, fallbackSearch);
  }
}
