package com.stockexchange.account;

import com.stockexchange.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BalanceTest {

    @Test
    void shouldDepositMoney() {
        Balance balance = new Balance(10_000);

        balance.deposit(5_000);

        assertEquals(15_000, balance.getAvailableCash());
    }

    @Test
    void shouldWithdrawMoney() {
        Balance balance = new Balance(10_000);

        balance.withdraw(3_000);

        assertEquals(7_000, balance.getAvailableCash());
    }

    @Test
    void shouldDebitMoney() {
        Balance balance = new Balance(10_000);

        balance.debit(4_000);

        assertEquals(6_000, balance.getAvailableCash());
    }

    @Test
    void shouldCreditMoney() {
        Balance balance = new Balance(10_000);

        balance.credit(4_000);

        assertEquals(14_000, balance.getAvailableCash());
    }

    @Test
    void shouldRejectInsufficientBalance() {
        Balance balance = new Balance(10_000);

        assertThrows(
                InsufficientBalanceException.class,
                () -> balance.debit(15_000)
        );
    }

    @Test
    void shouldRejectNegativeAmount() {
        Balance balance = new Balance(10_000);

        assertThrows(
                IllegalArgumentException.class,
                () -> balance.deposit(-500)
        );
    }

    @Test
    void shouldRejectZeroAmount() {
        Balance balance = new Balance(10_000);

        assertThrows(
                IllegalArgumentException.class,
                () -> balance.deposit(0)
        );
    }
}
