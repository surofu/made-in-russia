package com.surofu.madeinrussia.core.repository.specification;

import com.surofu.madeinrussia.core.model.user.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

public class UserSpecifications {
    public static Specification<User> byRole(@Param("role") String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.or(
                    criteriaBuilder.equal(criteriaBuilder.upper(root.get("role")), role.toUpperCase()),
                    criteriaBuilder.equal(criteriaBuilder.upper(root.get("role")), "ROLE_" + role.toUpperCase())
            );
        };
    }

    public static Specification<User> byIsEnabled(@Param("isEnabled") Boolean isEnabled) {
        return (root, query, criteriaBuilder) -> {
            if (isEnabled == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("isEnabled").get("value"), isEnabled);
        };
    }

    public static Specification<User> byLogin(@Param("login") String login) {
        return (root, query, criteriaBuilder) -> {
            if (login == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("login").get("value")), "%" + login.toUpperCase() + "%");
        };
    }

    public static Specification<User> byEmail(@Param("email") String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("email").get("value")), "%" + email.toUpperCase() + "%");
        };
    }

    public static Specification<User> byPhoneNumber(@Param("phoneNumber") String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            if (phoneNumber == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("phoneNumber").get("value")), "%" + phoneNumber.toUpperCase() + "%");
        };
    }

    public static Specification<User> byRegion(@Param("region") String region) {
        return (root, query, criteriaBuilder) -> {
            if (region == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("region").get("value")), "%" + region.toUpperCase() + "%");
        };
    }
}
