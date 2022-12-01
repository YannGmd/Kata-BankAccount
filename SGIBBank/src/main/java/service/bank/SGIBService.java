package service.bank;

import data.BankTransaction;
import repository.TransactionRepository;
import service.bank.creator.BankTransactionCreator;
import service.exception.InvalidOperationException;

import java.math.BigDecimal;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;
import static service.transactiontype.TransactionType.DEPOSIT;
import static service.transactiontype.TransactionType.WITHDRAW;

public class SGIBService implements BankService {
    private final TransactionRepository transactionRepository;
    private final BankTransactionCreator transactionCreator;

    public SGIBService(TransactionRepository transactionRepository, BankTransactionCreator transactionCreator){
        this.transactionRepository = requireNonNull(transactionRepository);
        this.transactionCreator = transactionCreator;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) throws InvalidOperationException {
        checkNegativeAmount(amount);
        transactionRepository.add(accountId, transactionCreator.create(
                DEPOSIT,
                amount,
                transactionRepository.getLast(accountId)
                        .map(BankTransaction::balance)
                        .orElse(ZERO)
                        .add(amount))
        );
    }

    @Override
    public BigDecimal withdraw(UUID accountId, BigDecimal amount) throws InvalidOperationException {
        checkNegativeAmount(amount);

        var currentBalance = transactionRepository.getLast(accountId)
                .map(BankTransaction::balance)
                .orElse(ZERO);

        checkValidWithdraw(currentBalance, amount);

        transactionRepository.add(accountId, transactionCreator.create(WITHDRAW, amount, currentBalance.subtract(amount)));
        return amount;
    }

    private void checkNegativeAmount(BigDecimal amount) throws InvalidOperationException {
        if(amount.compareTo(ZERO) < 0){
            throw new InvalidOperationException("Cannot deposit negative amount");
        }
    }

    private void checkValidWithdraw(BigDecimal currentBalance, BigDecimal amount) throws InvalidOperationException {
        if(currentBalance.compareTo(amount) < 0) {
            throw new InvalidOperationException("Not enough balance");
        }
    }
}
