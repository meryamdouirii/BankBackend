package nl.inholland.mysecondapi.repositories;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;  // Correct import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE (t.sender_account.owner.id = :userId OR t.reciever_account.owner.id = :userId) " +
            "AND (:startDate IS NULL OR t.dateTime >= :startDate) " +
            "AND (:endDate IS NULL OR t.dateTime <= :endDate) " +
            "AND (:amount IS NULL OR " +
            "    (:amountFilterType = 0 AND t.amount > :amount) OR " +  // GREATER
            "    (:amountFilterType = 1 AND t.amount < :amount) OR " +  // LESS
            "    (:amountFilterType = 2 AND t.amount = :amount)) " +    // EQUAL
            "AND (:iban IS NULL OR " +
            "    t.sender_account.IBAN = :iban OR " +      // Changed to exact match
            "    t.reciever_account.IBAN = :iban)")        // Changed to exact match
    Page<Transaction> findAllByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("amount") BigDecimal amount,
            @Param("amountFilterType") int amountFilterType,
            @Param("iban") String iban, // Changed parameter name from ibanContains to iban
            Pageable pageable);
}
