package com.dev.photoshare.repository.specification;

import com.dev.photoshare.dto.request.UserSearchRequest;
import com.dev.photoshare.dto.request.ViolationSearchRequest;
import com.dev.photoshare.entity.ViolationReport;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ViolationReportSpecification {
    public static Specification<ViolationReport> filterViolationReport(ViolationSearchRequest req) {
        return (root, query, cb) -> {

            List<Order> orders = new ArrayList<>();

            if (req.getSortBy() != null && !req.getSortBy().isBlank()) {
                String[] fields = req.getSortBy().split(",");

                for (String field : fields) {
                    field = field.trim();

                    if (field.startsWith("-")) {
                        String f = field.substring(1);
                        orders.add(cb.desc(root.get(f)));
                    } else {
                        orders.add(cb.asc(root.get(field)));
                    }
                }
            }

            if (!orders.isEmpty()) {
                query.orderBy(orders);
            }

            if (req.getStatus() != null) {
                return cb.equal(root.get("status"), req.getStatus());
            }

            return cb.conjunction();
        };
    }
}
