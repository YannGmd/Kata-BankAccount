package data;

import service.transactiontype.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankTransaction(TransactionType type, LocalDateTime time, BigDecimal amount, BigDecimal balance) {
}
