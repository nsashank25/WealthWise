package com.pes.financemanager.pesfinancemanager.model;

public class TaxBracket {
    private final double min;
    private final double max;
    private final double ratePercent;

    public TaxBracket(double min, double max, double ratePercent) {
        this.min = min;
        this.max = max;
        this.ratePercent = ratePercent;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getRatePercent() {
        return ratePercent;
    }
}
