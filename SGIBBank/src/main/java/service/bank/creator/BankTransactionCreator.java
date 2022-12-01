package service.bank.creator;

import data.BankTransaction;
import service.transactiontype.TransactionType;

import java.math.BigDecimal;

public interface BankTransactionCreator {
    BankTransaction create(TransactionType type, BigDecimal amount, BigDecimal balance);
}
