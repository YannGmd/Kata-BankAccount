package service.bank;

import service.exception.InvalidOperationException;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankService {
    void deposit(UUID accountId, BigDecimal amount) throws InvalidOperationException;
}
