package nl.inholland.mysecondapi.models.enums;

public enum TransactionType {
    INTERNAL_TRANSFER, // Between user's own accounts (e.g., checking to savings)
    PAYMENT,           // Sending money to another user (external or within bank)
    WITHDRAWAL,        // Taking money out via ATM or cash
    DEPOSIT,           // Adding money to account via ATM or salary
}

