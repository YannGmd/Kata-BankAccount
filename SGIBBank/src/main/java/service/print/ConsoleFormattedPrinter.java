package service.print;

import data.BankTransaction;

import java.util.Objects;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ConsoleFormattedPrinter implements Printer {
    private final Function<BankTransaction, String> formatter;

    public ConsoleFormattedPrinter(Function<BankTransaction, String> formatter){
        this.formatter = requireNonNull(formatter);
    }

    @Override
    public void print(BankTransaction transaction) {
        System.out.println(formatter.apply(transaction));
    }
}
