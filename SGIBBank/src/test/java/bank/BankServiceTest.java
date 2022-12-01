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
import service.exception.InvalidOperationException;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static java.math.BigDecimal.ONE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static service.TransactionType.TransactionType.DEPOSIT;

@DisplayName("Bank service should")
@ExtendWith(MockitoExtension.class)
public class BankServiceTest {
    @Mock
    private TransactionRepository repository;

    @Mock
    private Supplier<LocalDateTime> timeSupplier;

    @InjectMocks
    private SGIBService service;

    private final UUID customerId = UUID.randomUUID();
    private final LocalDateTime timeRef = LocalDateTime.now();

    @Test
    @DisplayName("allow deposit")
    void allowDeposit(){
        var createdTransaction = new BankTransaction(
                DEPOSIT,
                timeRef,
                ONE.setScale(2, RoundingMode.HALF_UP),
                ONE.setScale(2, RoundingMode.HALF_UP)
        );

        doReturn(Optional.empty()).when(repository).getLast(any());
        doReturn(timeRef).when(timeSupplier).get();

        assertDoesNotThrow(() -> service.deposit(customerId, ONE));

        var checkOrder = inOrder(repository);
        checkOrder.verify(repository).getLast(customerId);
        checkOrder.verify(repository).add(customerId, createdTransaction);
        checkOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("throw erreor on deposit with negative amounts")
    void throwOnNegativeAmountDeposit(){
        assertThrows(InvalidOperationException.class, () -> service.deposit(customerId, ONE.negate()));
        verifyNoInteractions(repository, timeSupplier);
    }
}
