package repository;

import data.BankTransaction;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    void add(UUID accountId, BankTransaction transaction);
    Optional<BankTransaction> getLast(UUID accountId);
    Collection<BankTransaction> getAll(UUID accountId);
}
