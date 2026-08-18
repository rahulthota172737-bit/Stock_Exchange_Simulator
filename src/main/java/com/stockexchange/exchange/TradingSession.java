package com.stockexchange.exchange;

import com.stockexchange.enums.TradingSessionState;
import com.stockexchange.exception.TradingHaltedException;

public class TradingSession {

    private TradingSessionState state;

    public TradingSession() {
        this.state = TradingSessionState.PRE_OPEN;
    }

    public void open() {
        if (state != TradingSessionState.PRE_OPEN) {
            throw new IllegalStateException(
                    "Market cannot be opened from state: " + state
            );
        }

        state = TradingSessionState.OPEN;
    }

    public void halt() {
        if (state != TradingSessionState.OPEN) {
            throw new IllegalStateException(
                    "Market cannot be halted from state: " + state
            );
        }

        state = TradingSessionState.HALTED;
    }

    public void resume() {
        if (state != TradingSessionState.HALTED) {
            throw new IllegalStateException(
                    "Market cannot be resumed from state: " + state
            );
        }

        state = TradingSessionState.OPEN;
    }

    public void close() {
        if (state != TradingSessionState.OPEN
                && state != TradingSessionState.HALTED) {
            throw new IllegalStateException(
                    "Market cannot be closed from state: " + state
            );
        }

        state = TradingSessionState.CLOSED;
    }

    public boolean isOpen() {
        return state == TradingSessionState.OPEN;
    }

    public boolean isHalted() {
        return state == TradingSessionState.HALTED;
    }

    public TradingSessionState getState() {
        return state;
    }
}
