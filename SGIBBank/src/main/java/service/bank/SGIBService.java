package service.bank;

import data.BankTransaction;
import repository.TransactionRepository;
import service.TransactionType.TransactionType;
import service.bank.creator.BankTransactionCreator;
import service.exception.InvalidOperationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;
import static service.TransactionType.TransactionType.DEPOSIT;

public class SGIBService implements BankService {
    private final TransactionRepository repository;
    private final BankTransactionCreator creator;

    public SGIBService(TransactionRepository repository, BankTransactionCreator creator){
        this.repository = requireNonNull(repository);
        this.creator = creator;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) throws InvalidOperationException {
        if(amount.compareTo(ZERO) < 0){
            throw new InvalidOperationException("Cannot deposit negative amount");
        }

        repository.add(accountId, creator.create(
                DEPOSIT,
                amount,
                repository.getLast(accountId)
                        .map(BankTransaction::balance)
                        .orElse(ZERO)
                        .add(amount))
        );
    }
}
