package data;

import service.TransactionType.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankTransaction(TransactionType type, LocalDateTime time, BigDecimal amount, BigDecimal balance) {
}
