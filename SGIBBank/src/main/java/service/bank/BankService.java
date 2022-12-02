package service.bank;

import service.exception.InvalidOperationException;
import service.print.Printer;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankService {
    void deposit(UUID accountId, BigDecimal amount) throws InvalidOperationException;
    BigDecimal withdraw(UUID accountId, BigDecimal amount) throws InvalidOperationException;
    void printHistory(UUID accountId, Printer printer);
}
