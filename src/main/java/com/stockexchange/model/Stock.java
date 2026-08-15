package com.stockexchange.model;

import java.util.Objects;

public class Stock {
    private final String symbol;
    private final String companyName;

    public Stock(String symbol, String companyName) {
        this.symbol = symbol;
        this.companyName = companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        Stock stock = (Stock) o;
        return Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode(){
        return Objects.hash(symbol);
    }

    @Override
    public String toString(){
        return "Stock{"+"Symbol="+symbol+"  "+"CompanyName="+companyName+"}";
    }
}
