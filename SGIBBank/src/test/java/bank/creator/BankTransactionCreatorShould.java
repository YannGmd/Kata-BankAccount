package bank.creator;

import data.BankTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.bank.TransactionType;
import service.bank.creator.SGIBTransactionCreator;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Bank transaction creator should")
@ExtendWith(MockitoExtension.class)
public class BankTransactionCreatorShould {
    @Mock
    private Supplier<LocalDateTime> timeSupplier;

    @InjectMocks
    private SGIBTransactionCreator transactionCreator;

    @ParameterizedTest(name = "{0}")
    @DisplayName("create with specific date")
    @MethodSource("specificLocalDateTimes")
    void returnFirstDate(String timeName, LocalDateTime timeRef){
        doReturn(timeRef).when(timeSupplier).get();

        assertThat(transactionCreator.create(TransactionType.DEPOSIT, ZERO, ZERO))
                .isEqualTo(new BankTransaction(TransactionType.DEPOSIT,
                        timeRef,
                        ZERO.setScale(2, RoundingMode.HALF_UP),
                        ZERO.setScale(2, RoundingMode.HALF_UP))
                );

        verify(timeSupplier).get();
        verifyNoMoreInteractions(timeSupplier);
    }

    private static Stream<Arguments> specificLocalDateTimes(){
        return Stream.of(
                Arguments.of("now", now()),
                Arguments.of("specific date time", LocalDateTime.of(1999, 7, 27, 4, 20))
        );
    }
}