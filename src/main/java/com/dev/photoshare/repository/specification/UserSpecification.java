package com.dev.photoshare.repository.specification;

import com.dev.photoshare.dto.request.UserSearchRequest;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.Roles;
import com.dev.photoshare.entity.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<Users> filter(UserSearchRequest req) {

        return (root, query, cb) -> {

            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            Join<Users, Profiles> profileJoin = root.join("profile", JoinType.LEFT);
            Join<Users, Roles> roleJoin = root.join("role", JoinType.LEFT);

            if (req.getStatus() != null && !req.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), req.getStatus()));
            }

            if (req.getSearchKey() != null && !req.getSearchKey().isBlank()) {

                String keyword = "%" + req.getSearchKey().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("email")), keyword),
                                cb.like(cb.lower(profileJoin.get("displayName")), keyword)
                        )
                );
            }

            if (req.getRoleName() != null && !req.getRoleName().isBlank()) {
                predicates.add(cb.equal(roleJoin.get("roleName"), req.getRoleName()));
            }

            query.orderBy(cb.asc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}