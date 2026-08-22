package com.stockexchange;

import com.stockexchange.concurrency.ConcurrencyGuard;

public class ConcurrencyInvariantTest {

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("      CONCURRENCY INVARIANT TEST");
        System.out.println("======================================");

        boolean passed = true;

        passed &= testValidBalance();
        passed &= testInvalidBalance();

        passed &= testValidHoldings();
        passed &= testInvalidHoldings();

        passed &= testValidOrderQuantity();
        passed &= testInvalidOrderQuantity();

        passed &= testValidRemainingQuantity();
        passed &= testInvalidRemainingQuantity();

        passed &= testValidTrade();
        passed &= testInvalidTrade();

        System.out.println();

        if (passed) {

            System.out.println("PASS: All concurrency invariants verified.");

        } else {

            System.out.println("FAIL: One or more invariants failed.");
        }

        System.out.println("======================================");
    }

    private static boolean testValidBalance() {

        try {

            ConcurrencyGuard.verifyNonNegativeBalance(10_000);

            System.out.println("PASS: Valid balance accepted.");

            return true;

        } catch (Exception e) {

            System.out.println("FAIL: Valid balance rejected.");

            return false;
        }
    }

    private static boolean testInvalidBalance() {

        try {

            ConcurrencyGuard.verifyNonNegativeBalance(-1);

            System.out.println("FAIL: Negative balance accepted.");

            return false;

        } catch (IllegalStateException e) {

            System.out.println("PASS: Negative balance rejected.");

            return true;
        }
    }

    private static boolean testValidHoldings() {

        try {

            ConcurrencyGuard.verifyNonNegativeHoldings(100);

            System.out.println("PASS: Valid holdings accepted.");

            return true;

        } catch (Exception e) {

            System.out.println("FAIL: Valid holdings rejected.");

            return false;
        }
    }

    private static boolean testInvalidHoldings() {

        try {

            ConcurrencyGuard.verifyNonNegativeHoldings(-10);

            System.out.println("FAIL: Negative holdings accepted.");

            return false;

        } catch (IllegalStateException e) {

            System.out.println("PASS: Negative holdings rejected.");

            return true;
        }
    }

    private static boolean testValidOrderQuantity() {

        try {

            ConcurrencyGuard.verifyOrderQuantity(100);

            System.out.println("PASS: Valid order quantity accepted.");

            return true;

        } catch (Exception e) {

            System.out.println("FAIL: Valid order quantity rejected.");

            return false;
        }
    }

    private static boolean testInvalidOrderQuantity() {

        try {

            ConcurrencyGuard.verifyOrderQuantity(0);

            System.out.println("FAIL: Zero order quantity accepted.");

            return false;

        } catch (IllegalStateException e) {

            System.out.println("PASS: Invalid order quantity rejected.");

            return true;
        }
    }

    private static boolean testValidRemainingQuantity() {

        try {

            ConcurrencyGuard.verifyRemainingQuantity(100, 50);

            System.out.println("PASS: Valid remaining quantity accepted.");

            return true;

        } catch (Exception e) {

            System.out.println("FAIL: Valid remaining quantity rejected.");

            return false;
        }
    }

    private static boolean testInvalidRemainingQuantity() {

        try {

            ConcurrencyGuard.verifyRemainingQuantity(100, 150);

            System.out.println("FAIL: Invalid remaining quantity accepted.");

            return false;

        } catch (IllegalStateException e) {

            System.out.println("PASS: Invalid remaining quantity rejected.");

            return true;
        }
    }

    private static boolean testValidTrade() {

        try {

            ConcurrencyGuard.verifyTrade(100, 250);

            System.out.println("PASS: Valid trade accepted.");

            return true;

        } catch (Exception e) {

            System.out.println("FAIL: Valid trade rejected.");

            return false;
        }
    }

    private static boolean testInvalidTrade() {

        try {

            ConcurrencyGuard.verifyTrade(100, -250);

            System.out.println("FAIL: Invalid trade accepted.");

            return false;

        } catch (IllegalStateException e) {

            System.out.println("PASS: Invalid trade rejected.");

            return true;
        }
    }
}
