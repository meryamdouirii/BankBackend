package nl.inholland.mysecondapi.specifications;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class TransactionSpecifications {

    private static Specification<Transaction> hasAccountId(Long accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("sender_account").get("id"), accountId),
                    criteriaBuilder.equal(root.get("reciever_account").get("id"), accountId)
            );
        };
    }

    private static Specification<Transaction> hasDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Transaction> hasAmountFilter(BigDecimal amount, TransactionFilterRequest.AmountFilterType filterType) {
        return (root, query, criteriaBuilder) -> {
            if (amount == null || filterType == null) {
                return criteriaBuilder.conjunction();
            }

            switch (filterType) {
                case GREATER:
                    return criteriaBuilder.greaterThan(root.get("amount"), amount);
                case LESS:
                    return criteriaBuilder.lessThan(root.get("amount"), amount);
                case EQUAL:
                    return criteriaBuilder.equal(root.get("amount"), amount);
                default:
                    return criteriaBuilder.conjunction();
            }
        };
    }

    private static Specification<Transaction> hasIbanContains(String ibanContains) {
        return (root, query, criteriaBuilder) -> {
            if (ibanContains == null || ibanContains.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + ibanContains.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("sender_account").get("iban")),
                            likePattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("reciever_account").get("iban")),
                            likePattern
                    )
            );
        };
    }

    public static Specification<Transaction> buildSpecification(Long accountId, TransactionFilterRequest filters) {
        Specification<Transaction> spec = Specification.where(hasAccountId(accountId));

        // Voeg alleen filters toe die daadwerkelijk waarden hebben
        if (filters.getStartDate() != null || filters.getEndDate() != null) {
            spec = spec.and(hasDateTimeBetween(filters.getStartDate(), filters.getEndDate()));
        }

        if (filters.getAmount() != null && filters.getAmountFilterType() != null) {
            spec = spec.and(hasAmountFilter(filters.getAmount(), filters.getAmountFilterType()));
        }

        if (filters.getIban() != null && !filters.getIban().trim().isEmpty()) {
            spec = spec.and(hasIbanContains(filters.getIban()));
        }

        return spec;
    }
}