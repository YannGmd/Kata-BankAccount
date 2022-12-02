package bank;

import data.BankTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TransactionRepository;
import service.bank.SGIBService;
import service.bank.creator.BankTransactionCreator;
import service.exception.InvalidOperationException;
import service.print.Printer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static java.math.BigDecimal.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static service.bank.TransactionType.DEPOSIT;
import static service.bank.TransactionType.WITHDRAW;

@DisplayName("Bank service should")
@ExtendWith(MockitoExtension.class)
public class BankServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Supplier<LocalDateTime> timeSupplier;

    @Mock
    private BankTransactionCreator transactionCreator;

    @InjectMocks
    private SGIBService bankService;

    @Mock
    private Printer printer;

    private final UUID customerId = UUID.randomUUID();
    private final LocalDateTime timeRef = LocalDateTime.now();

    @Test
    @DisplayName("allow deposit")
    void allowDeposit(){
        var createdTransaction = new BankTransaction(DEPOSIT, timeRef, ONE, ONE);

        doReturn(Optional.empty()).when(transactionRepository).getLast(any());
        doReturn(createdTransaction).when(transactionCreator).create(any(), any(), any());

        assertDoesNotThrow(() -> bankService.deposit(customerId, ONE));

        var checkOrder = inOrder(transactionRepository, transactionCreator, printer);
        checkOrder.verify(transactionRepository).getLast(customerId);
        checkOrder.verify(transactionCreator).create(DEPOSIT, ONE, ONE);
        checkOrder.verify(transactionRepository).add(customerId, createdTransaction);
        checkOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("throw error on deposit with negative amounts")
    void throwOnNegativeAmountDeposit(){
        assertThrows(InvalidOperationException.class, () -> bankService.deposit(customerId, ONE.negate()));
        verifyNoInteractions(transactionRepository, transactionCreator, printer);
    }

    @Test
    @DisplayName("allow positive withdraws")
    void allowWithdraws() throws InvalidOperationException {
        var createdTransaction = new BankTransaction(WITHDRAW, timeRef, ONE, BigDecimal.valueOf(9L));
        var lastTransaction = new BankTransaction(DEPOSIT, timeRef, TEN, TEN);

        doReturn(Optional.of(lastTransaction)).when(transactionRepository).getLast(any());
        doReturn(createdTransaction).when(transactionCreator).create(any(), any(), any());

        var withdrew = assertDoesNotThrow(() -> bankService.withdraw(customerId, ONE));
        assertEquals(ONE, withdrew);

        var checkOrder = inOrder(transactionRepository, transactionCreator, printer);
        checkOrder.verify(transactionRepository).getLast(customerId);
        checkOrder.verify(transactionCreator).create(WITHDRAW, ONE, BigDecimal.valueOf(9L));
        checkOrder.verify(transactionRepository).add(customerId, createdTransaction);
        checkOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("throw error with negative withdraw")
    void throwOnInvalidNegativeAmountsWithdraw(){
        assertThrows(InvalidOperationException.class,
                () -> bankService.withdraw(customerId, ONE.negate()));

        verifyNoInteractions(transactionRepository, transactionCreator, printer);
    }

    @Test
    @DisplayName("throw error with negative currentBalance")
    void throwOnNegativeBalance(){
        doReturn(Optional.of(new BankTransaction(
                DEPOSIT, timeRef, ZERO, ZERO
        ))).when(transactionRepository).getLast(any(UUID.class));

        assertThrows(InvalidOperationException.class, () -> bankService.withdraw(customerId, ONE));

        verify(transactionRepository).getLast(customerId);
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(transactionCreator, printer);
    }

    @Test
    @DisplayName("print history")
    void printHistory(){
        var deposit = new BankTransaction(DEPOSIT, timeRef, TEN, TEN);
        var withdraw = new BankTransaction(WITHDRAW, timeRef, ONE, BigDecimal.valueOf(9L));
        List<BankTransaction> transactions = List.of(deposit, withdraw);
        doNothing().when(printer).print(any());

        doReturn(transactions).when(transactionRepository).getAll(any());
        bankService.printHistory(customerId, printer);

        var checkOrder = inOrder(transactionRepository, printer, transactionCreator);
        checkOrder.verify(transactionRepository).getAll(customerId);
        checkOrder.verify(printer).print(deposit);
        checkOrder.verify(printer).print(withdraw);
        checkOrder.verifyNoMoreInteractions();
    }
}
