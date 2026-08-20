package com.stockexchange.portfolio;

import com.stockexchange.account.Position;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private final Map<String, Position> positions;

    public Portfolio() {
        this.positions = new HashMap<>();
    }

    public Position getPosition(String symbol) {
        return positions.get(symbol);
    }

    public void addPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException(
                    "Position cannot be null"
            );
        }

        positions.put(position.getSymbol(), position);
    }

    public void removePosition(String symbol) {
        positions.remove(symbol);
    }

    public Map<String, Position> getHoldings() {
        return Collections.unmodifiableMap(positions);
    }

    public long getTotalQuantity(String symbol) {
        Position position = positions.get(symbol);

        if (position == null) {
            return 0;
        }

        return position.getQuantity();
    }
}