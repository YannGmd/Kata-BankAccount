package service.bank;

import data.BankTransaction;
import repository.TransactionRepository;
import service.TransactionType.TransactionType;
import service.exception.InvalidOperationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;

public class SGIBService implements BankService {
    private final TransactionRepository repository;
    private final Supplier<LocalDateTime> timeSupplier;

    public SGIBService(TransactionRepository repository, Supplier<LocalDateTime> timeSupplier){
        this.repository = requireNonNull(repository);
        this.timeSupplier = timeSupplier;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) throws InvalidOperationException {
        if(amount.compareTo(ZERO) < 0){
            throw new InvalidOperationException("Cannot deposit negative amount");
        }

        repository.add(accountId, new BankTransaction(
                TransactionType.DEPOSIT,
                timeSupplier.get(),
                amount.setScale(2, RoundingMode.HALF_UP),
                repository.getLast(accountId)
                        .map(BankTransaction::balance)
                        .orElse(ZERO)
                        .add(amount).setScale(2, RoundingMode.HALF_UP)
        ));
    }
}
