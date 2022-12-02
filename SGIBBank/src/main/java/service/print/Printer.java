package service.print;

import data.BankTransaction;

@FunctionalInterface
public interface Printer {
    void print(BankTransaction transaction);
}
