package Model;

import java.util.Date;
import java.util.Objects;

public class RegressionCompareObject {

    public double longDurationSlopeLookback;
    public double shortDurationSlopeLookback;
    public double longDurationRocLookback;
    public double shortDurationRocLookback;
    public double longVolatilityLookback;
    public double shortVolatilityLookback;
    public double marketCapModifier;

    public RegressionCompareObject(double longDurationSlopeLookback, double shortDurationSlopeLookback, double longDurationRocLookback, double shortDurationRocLookback, double longVolatilityLookback, double shortVolatilityLookback, double marketCapModifier) {
        this.longDurationSlopeLookback = longDurationSlopeLookback;
        this.shortDurationSlopeLookback = shortDurationSlopeLookback;
        this.longDurationRocLookback = longDurationRocLookback;
        this.shortDurationRocLookback = shortDurationRocLookback;
        this.longVolatilityLookback = longVolatilityLookback;
        this.shortVolatilityLookback = shortVolatilityLookback;
        this.marketCapModifier = marketCapModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegressionCompareObject that = (RegressionCompareObject) o;
        return Double.compare(that.longDurationSlopeLookback, longDurationSlopeLookback) == 0 && Double.compare(that.shortDurationSlopeLookback, shortDurationSlopeLookback) == 0 &&
                Double.compare(that.longDurationRocLookback, longDurationRocLookback) == 0 &&
                Double.compare(that.shortDurationRocLookback, shortDurationRocLookback) == 0 &&
                Double.compare(that.longVolatilityLookback, longVolatilityLookback) == 0 &&
                Double.compare(that.shortVolatilityLookback, shortVolatilityLookback) == 0 &&
                Double.compare(that.marketCapModifier, marketCapModifier) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longDurationSlopeLookback, shortDurationSlopeLookback, longDurationRocLookback, shortDurationRocLookback, longVolatilityLookback, shortVolatilityLookback,marketCapModifier);
    }
}
