package com.surofu.exporteru.core.repository.specification;

import com.surofu.exporteru.application.exception.InvalidRoleException;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserRole;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

public class UserSpecifications {
    public static Specification<User> byRole(@Param("role") String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null || role.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            try {
                UserRole userRole = UserRole.of(role);
                return criteriaBuilder.equal(root.get("role"), userRole);
            } catch (InvalidRoleException e) {
                return criteriaBuilder.disjunction();
            }
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
            if (login == null || login.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("login").get("value")), "%" + login.toUpperCase() + "%");
        };
    }

    public static Specification<User> byEmail(@Param("email") String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("email").get("value")), "%" + email.toUpperCase() + "%");
        };
    }

    public static Specification<User> byPhoneNumber(@Param("phoneNumber") String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("phoneNumber").get("value")), "%" + phoneNumber.toUpperCase() + "%");
        };
    }

    public static Specification<User> byRegion(@Param("region") String region) {
        return (root, query, criteriaBuilder) -> {
            if (region == null || region.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.upper(root.get("region").get("value")), "%" + region.toUpperCase() + "%");
        };
    }
}
