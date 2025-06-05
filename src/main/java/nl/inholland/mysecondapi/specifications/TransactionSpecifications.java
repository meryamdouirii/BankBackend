package nl.inholland.mysecondapi.specifications;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class TransactionSpecifications {

    public static Specification<Transaction> accountInvolved(Long accountId) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("sender_account").get("id"), accountId),
                cb.equal(root.get("reciever_account").get("id"), accountId)
        );
    }

    public static Specification<Transaction> startDateAfter(LocalDateTime startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("dateTime"), startDate);
    }

    public static Specification<Transaction> endDateBefore(LocalDateTime endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("dateTime"), endDate);
    }

    public static Specification<Transaction> amountFilter(BigDecimal amount, int filterType) {
        return (root, query, cb) -> {
            if (amount == null) return null;
            switch (filterType) {
                case 0: return cb.greaterThan(root.get("amount"), amount);
                case 1: return cb.lessThan(root.get("amount"), amount);
                case 2: return cb.equal(root.get("amount"), amount);
                default: return null;
            }
        };
    }

    public static Specification<Transaction> ibanContains(String ibanContains) {
        return (root, query, cb) -> {
            if (ibanContains == null || ibanContains.isBlank()) return null;
            String pattern = "%" + ibanContains + "%";
            return cb.or(
                    cb.like(root.get("sender_account").get("iban"), pattern),
                    cb.like(root.get("reciever_account").get("iban"), pattern)
            );
        };
    }
}
