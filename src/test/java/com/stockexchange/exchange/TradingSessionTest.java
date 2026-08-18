package com.stockexchange.exchange;

import com.stockexchange.enums.TradingSessionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TradingSessionTest {

    @Test
    void shouldStartInPreOpen() {

        TradingSession session = new TradingSession();

        assertEquals(
                TradingSessionState.PRE_OPEN,
                session.getState()
        );

        assertFalse(session.isOpen());
    }

    @Test
    void shouldOpenMarket() {

        TradingSession session = new TradingSession();

        session.open();

        assertTrue(session.isOpen());
        assertEquals(
                TradingSessionState.OPEN,
                session.getState()
        );
    }

    @Test
    void shouldHaltMarket() {

        TradingSession session = new TradingSession();

        session.open();
        session.halt();

        assertTrue(session.isHalted());
        assertFalse(session.isOpen());
    }

    @Test
    void shouldResumeMarket() {

        TradingSession session = new TradingSession();

        session.open();
        session.halt();
        session.resume();

        assertTrue(session.isOpen());
    }

    @Test
    void shouldCloseMarket() {

        TradingSession session = new TradingSession();

        session.open();
        session.close();

        assertEquals(
                TradingSessionState.CLOSED,
                session.getState()
        );

        assertFalse(session.isOpen());
    }

    @Test
    void shouldRejectInvalidOpenTransition() {

        TradingSession session = new TradingSession();

        assertThrows(
                IllegalStateException.class,
                session::resume
        );
    }
}
