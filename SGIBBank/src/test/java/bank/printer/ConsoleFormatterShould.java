package bank.printer;

import data.BankTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.bank.TransactionType;
import service.exception.InvalidOperationException;
import service.print.ConsoleFormattedPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Console printer should")
@ExtendWith(MockitoExtension.class)
public class ConsoleFormatterShould {
    @Mock
    private Function<BankTransaction, String> formatter;

    @InjectMocks
    private ConsoleFormattedPrinter consolePrinter;

    private final PrintStream standardOutput = System.out;
    private final ByteArrayOutputStream outputReceiver = new ByteArrayOutputStream();
    private final LocalDateTime timeRef = LocalDateTime.now();

    @BeforeEach
    void setOutAndFlush() throws IOException {
        System.setOut(new PrintStream(outputReceiver));
        outputReceiver.flush();
        outputReceiver.reset();
    }

    @AfterEach
    void tearDown(){
        System.setOut(standardOutput);
    }

    @Test
    @DisplayName("print deposit")
    void printDeposit() throws InvalidOperationException {
        var deposit = new BankTransaction(TransactionType.DEPOSIT, timeRef, ZERO, TEN);
        doReturn(deposit.toString()).when(formatter).apply(any());

        consolePrinter.print(deposit);
        assertEquals(deposit.toString(), outputReceiver.toString().trim());

        verify(formatter).apply(deposit);
        verifyNoMoreInteractions(formatter);
    }

    @Test
    @DisplayName("print withdraw")
    void printWithdraw() {
        var withdraw = new BankTransaction(TransactionType.WITHDRAW, timeRef, ONE, ZERO);
        doReturn(withdraw.toString()).when(formatter).apply(any());

        consolePrinter.print(withdraw);
        assertEquals(withdraw.toString(), outputReceiver.toString().trim());

        verify(formatter).apply(withdraw);
        verifyNoMoreInteractions(formatter);
    }

    @Test
    @DisplayName("print history")
    void printHistory() throws InvalidOperationException {
        var deposit = new BankTransaction(TransactionType.DEPOSIT, timeRef, ZERO, TEN);
        var withdraw = new BankTransaction(TransactionType.WITHDRAW, timeRef, ONE, ZERO);

        when(formatter.apply(deposit)).thenReturn(deposit.toString());
        when(formatter.apply(withdraw)).thenReturn(withdraw.toString());

        consolePrinter.print(deposit);
        consolePrinter.print(withdraw);

        assertEquals(
                Stream.of(deposit, withdraw)
                        .map(BankTransaction::toString).collect(Collectors.joining("\r\n")),
                outputReceiver.toString().trim()
        );

        var checkOrder = inOrder(formatter);
        checkOrder.verify(formatter).apply(deposit);
        checkOrder.verify(formatter).apply(withdraw);
        verifyNoMoreInteractions(formatter);
    }
}
