package service.bank.creator;

import data.BankTransaction;
import service.bank.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;

public class SGIBTransactionCreator implements BankTransactionCreator {
    private final Supplier<LocalDateTime> timeSupplier;

    public SGIBTransactionCreator(Supplier<LocalDateTime> timeSupplier){
        this.timeSupplier = timeSupplier;
    }

    @Override
    public BankTransaction create(TransactionType type, BigDecimal amount, BigDecimal balance) {
        return new BankTransaction(
                type,
                timeSupplier.get(),
                amount.setScale(2, RoundingMode.HALF_UP),
                balance.setScale(2, RoundingMode.HALF_UP)
        );
    }
}
