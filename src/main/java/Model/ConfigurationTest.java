package Model;

import BackTest.TradeLog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConfigurationTest  implements Cloneable{

    private int tradeLength;

    private int trendLength;

    private int trendConfirmationLength;

    private int trendBreakLength;

    private double rangeThreshold;

    private int successfulTrades;

    private int failedTrades;

    private int ivAdjustmentType;

    private int ivTrendCalcType;

    private double successRate;

    private double dollars;

    private double daysVol;

    private boolean priceRocFlip;
    private boolean priceSlopeFlip;
    private boolean volatilityRocFlip;

    private boolean volatilitySlopeFlips;

    private boolean signalValueFlip;

    private double stopLoss;

    private double percentOfVolatility;

    private boolean signalRocFlips;

    private double ivWeighting;

    private double volumeWeighting;

    private double discountWeighting;

    private double realizedVolWeighting;
    private double treasuryWeighting;
    private int dateChecks;
    private double trendAccuracy;
    private int[] movingTradeLength;
    private int[] movingTrendLength;

    private double[] movingThreshold;

    private double dollarPercent;

    public boolean useVolatilitySurface;

    public int volLookback;

    public double averageDollars;

    public double equalWeightedSuccess;

    public double commodityWeighting;
    public double fedTotalAssetsWeighting;

    public double revenueRocWeighting;

    public double dollarWeighting;

    public double dollarsBelowThreshold;

    public double percentPositiveTests;

    public int correlationDays;

    public double oilWeighting;

    public double volOfReturn;

    public double vixWeighting;
    public double volOfVol;
    public double replayDollars;

    public double longDollars;

    public double shortDollars;

    public double shortPct;
    public double shortPossibleProfitable;
    public double longPossibleProfitable;
    public double longOpenVolatilityRocThreshold;
    public double shortOpenVolatilityRocThreshold;

    public double averageTradingDaysHeld;
    public double averageReturnPerTradingDayHeld;
    public double averageCapitalInUse;

    public double returnRsquared;
    public double shortRsquared;
    double longOpenSignalValueThreshold;
    double longOpenSignalRocThreshold;
    double longOpenVolatilitySlopeThreshold;
    double[] longOpenVolatilitySlopes;
    double[] shortOpenVolatilitySloeps;
    double shortOpenSignalValueThreshold;
    double shortOpenSignalRocThreshold;
    double shortOpenVolatilitySlopeThreshold;
    double shortExitTarget;
    double shortExitVolatility1;
    double shortExitVolatility2;
    double shortExitVolatility3;
    double longExitTarget;
    double longExitVolatility1;
    double longExitVolatility2;
    double longExitVolatility3;
    public double staticDollars;
    public double successRate1;
    public double successRate2;
    public double successRate3;
    public double successfulTrades1;
    public double successfulTrades2;
    public double successfulTrades3;
    public double failedTrades1;
    public double failedTrades2;
    public double failedTrades3;
    double expectedDollars;
    public double[] categorySuccesses;
    public double[] categoryCounts;
    public boolean volumeFlip;

    public ConfigurationTest(int tradeLength, int trendLength, int trendConfirmationLength,
                             int trendBreakLength, double rangeThreshold,
                             int ivAdjustmentType, int ivTrendCalcType, double daysVol,
                             boolean priceRocFlip, boolean volatilityRocFlip, boolean volatilitySlopeFlips,
                             double stopLoss, double percentOfVolatility, boolean signalRocFlips,
                             double ivWeighting, double volumeWeighting, double discountWeighting,
                             double realizedVolWeighting, boolean useVolatilitySurface, int volLookback) {
        this.tradeLength = tradeLength;
        this.trendLength = trendLength;
        this.trendConfirmationLength = trendConfirmationLength;
        this.trendBreakLength = trendBreakLength;
        this.rangeThreshold = rangeThreshold;
        this.ivAdjustmentType = ivAdjustmentType;
        this.ivTrendCalcType = ivTrendCalcType;
        this.daysVol = daysVol;
        this.priceRocFlip = priceRocFlip;
        this.volatilityRocFlip = volatilityRocFlip;
        this.volatilitySlopeFlips = volatilitySlopeFlips;
        this.stopLoss = stopLoss;
        this.percentOfVolatility = percentOfVolatility;
        this.signalRocFlips = signalRocFlips;
        this.ivWeighting = ivWeighting;
        this.volumeWeighting = volumeWeighting;
        this.discountWeighting = discountWeighting;
        this.realizedVolWeighting = realizedVolWeighting;
        this.useVolatilitySurface = useVolatilitySurface;
        this.volLookback = volLookback;
        this.dateChecks = 0;
    }

    private TradeLog tradeLog;

    private double averageDelta;
    public ConfigurationTest() {
        stockTestList = new ArrayList<>();
        this.useVolatilitySurface = false;
    }

    private List<IndividualStockTest> stockTestList;

    public int getTradeLength() {
        return tradeLength;
    }

    public void setTradeLength(int tradeLength) {
        this.tradeLength = tradeLength;
    }

    public int getTrendLength() {
        return trendLength;
    }

    public void setTrendLength(int trendLength) {
        this.trendLength = trendLength;
    }

    public int getTrendConfirmationLength() {
        return trendConfirmationLength;
    }

    public void setTrendConfirmationLength(int trendConfirmationLength) {
        this.trendConfirmationLength = trendConfirmationLength;
    }

    public int getTrendBreakLength() {
        return trendBreakLength;
    }

    public void setTrendBreakLength(int trendBreakLength) {
        this.trendBreakLength = trendBreakLength;
    }

    public double getRangeThreshold() {
        return rangeThreshold;
    }

    public void setRangeThreshold(double rangeThreshold) {
        this.rangeThreshold = rangeThreshold;
    }

    public int getSuccessfulTrades() {
        return successfulTrades;
    }

    public void setSuccessfulTrades(int successfulTrades) {
        this.successfulTrades = successfulTrades;
    }

    public int getFailedTrades() {
        return failedTrades;
    }

    public void setFailedTrades(int failedTrades) {
        this.failedTrades = failedTrades;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return "ConfigurationTest{" +
                "tradeLength=" + tradeLength +
                ", trendLength=" + trendLength +
                ", trendConfirmationLength=" + trendConfirmationLength +
                ", trendBreakLength=" + trendBreakLength +
                ", rangeThreshold=" + rangeThreshold +
                ", successfulTrades=" + successfulTrades +
                ", failedTrades=" + failedTrades +
                ", ivAdjustmentType=" + ivAdjustmentType +
                ", ivTrendCalcType=" + ivTrendCalcType +
                ", successRate=" + decimalFormat.format(successRate) +
                ", dollars=" + dollars +
                ", daysVol=" + daysVol +
                ", priceRocFlip=" + priceRocFlip +
                ", priceSlopeFlip=" + priceSlopeFlip +
                ", volatilityRocFlip=" + volatilityRocFlip +
                ", volatilitySlopeFlips=" + volatilitySlopeFlips +
                ", signalValueFlip=" + signalValueFlip +
                ", volumeFlip=" + volumeFlip +
                ", stopLoss=" + stopLoss +
                ", percentOfVolatility=" + percentOfVolatility +
                ", signalRocFlips=" + signalRocFlips +
                ", ivWeighting=" + ivWeighting +
                ", volumeWeighting=" + volumeWeighting +
                ", discountWeighting=" + discountWeighting +
                ", realizedVolWeighting=" + realizedVolWeighting +
                ", treasuryWeighting=" + treasuryWeighting +
                ", dateChecks=" + dateChecks +
                ", trendAccuracy=" + trendAccuracy +
                ", movingTradeLength=" + Arrays.toString(movingTradeLength) +
                ", movingTrendLength=" + Arrays.toString(movingTrendLength) +
                ", movingThreshold=" + Arrays.toString(movingThreshold) +
                ", dollarPercent=" + dollarPercent +
                ", useVolatilitySurface=" + useVolatilitySurface +
                ", volLookback=" + volLookback +
                ", averageDollars=" + averageDollars +
                ", equalWeightedSuccess=" + equalWeightedSuccess +
                ", commodityWeighting=" + commodityWeighting +
                ", fedTotalAssetsWeighting=" + fedTotalAssetsWeighting +
                ", revenueRocWeighting=" + revenueRocWeighting +
                ", dollarWeighting=" + dollarWeighting +
                ", percentPositiveTests=" + percentPositiveTests +
                ", correlationDays=" + correlationDays +
                ", oilWeighting=" + oilWeighting +
                ", volOfReturn=" + volOfReturn +
                ", vixWeighting=" + vixWeighting +
                ", volOfVol=" + volOfVol +
                ", replayDollars=" + nf.format(replayDollars) +
                ", longDollars=" +nf.format(longDollars) +
                ", shortDollars=" + nf.format(shortDollars) +
                ", shortPct=" + shortPct +
                ", shortPossibleProfitable=" + shortPossibleProfitable +
                ", longPossibleProfitable=" + longPossibleProfitable +
                ", longOpenVolatilityRocThreshold=" + longOpenVolatilityRocThreshold +
                ", shortOpenVolatilityRocThreshold=" + shortOpenVolatilityRocThreshold +
                ", averageTradingDaysHeld=" + averageTradingDaysHeld +
                ", averageReturnPerTradingDayHeld=" + averageReturnPerTradingDayHeld +
                ", averageCapitalInUse=" + averageCapitalInUse +
                ", returnRsquared=" + returnRsquared +
                ", shortRsquared=" + shortRsquared +
                ", longOpenSignalValueThreshold=" + longOpenSignalValueThreshold +
                ", longOpenSignalRocThreshold=" + longOpenSignalRocThreshold +
                ", longOpenVolatilitySlopeThreshold=" + longOpenVolatilitySlopeThreshold +
                ", longOpenVolatilitySlopes=" + Arrays.toString(longOpenVolatilitySlopes) +
                ", shortOpenVolatilitySloeps=" + Arrays.toString(shortOpenVolatilitySloeps) +
                ", shortOpenSignalValueThreshold=" + shortOpenSignalValueThreshold +
                ", shortOpenSignalRocThreshold=" + shortOpenSignalRocThreshold +
                ", shortOpenVolatilitySlopeThreshold=" + shortOpenVolatilitySlopeThreshold +
                ", shortExitTarget=" + shortExitTarget +
                ", shortExitVolatility1=" + shortExitVolatility1 +
                ", shortExitVolatility2=" + shortExitVolatility2 +
                ", shortExitVolatility3=" + shortExitVolatility3 +
                ", longExitTarget=" + longExitTarget +
                ", longExitVolatility1=" + longExitVolatility1 +
                ", longExitVolatility2=" + longExitVolatility2 +
                ", longExitVolatility3=" + longExitVolatility3 +
                ", staticDollars=" + nf.format(staticDollars) +
                ", successRate1=" + successRate1 +
                ", successRate2=" + successRate2 +
                ", successRate3=" + successRate3 +
                ", successfulTrades1=" + successfulTrades1 +
                ", successfulTrades2=" + successfulTrades2 +
                ", successfulTrades3=" + successfulTrades3 +
                ", failedTrades1=" + failedTrades1 +
                ", failedTrades2=" + failedTrades2 +
                ", failedTrades3=" + failedTrades3 +
                ", expectedDollars=" + expectedDollars +
                ", categorySuccesses=" + Arrays.toString(categorySuccesses) +
                ", categoryCount =" + Arrays.toString(categoryCounts) +
                ", averageDelta=" + averageDelta +
                '}';
    }

    public double getDollars() {
        return dollars;
    }

    public void setDollars(double dollars) {
        this.dollars = dollars;
    }

    public double getDaysVol() {
        return daysVol;
    }

    public void setDaysVol(double daysVol) {
        this.daysVol = daysVol;
    }

    public int getIvAdjustmentType() {
        return ivAdjustmentType;
    }

    public void setIvAdjustmentType(int ivAdjustmentType) {
        this.ivAdjustmentType = ivAdjustmentType;
    }

    public boolean isPriceRocFlip() {
        return priceRocFlip;
    }

    public void setPriceRocFlip(boolean priceRocFlip) {
        this.priceRocFlip = priceRocFlip;
    }

    public boolean isVolatilityRocFlip() {
        return volatilityRocFlip;
    }

    public void setVolatilityRocFlip(boolean volatilityRocFlip) {
        this.volatilityRocFlip = volatilityRocFlip;
    }

    public boolean isVolatilitySlopeFlips() {
        return volatilitySlopeFlips;
    }

    public void setVolatilitySlopeFlips(boolean volatilitySlopeFlips) {
        this.volatilitySlopeFlips = volatilitySlopeFlips;
    }

    public int getIvTrendCalcType() {
        return ivTrendCalcType;
    }

    public void setIvTrendCalcType(int ivTrendCalcType) {
        this.ivTrendCalcType = ivTrendCalcType;
    }

    public double getStoploss(){
        return stopLoss;
    }
    public void setStopLoss(double stopLoss){
        this.stopLoss = stopLoss;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public double getPercentOfVolatility() {
        return percentOfVolatility;
    }

    public void setPercentOfVolatility(double percentOfVolatility) {
        this.percentOfVolatility = percentOfVolatility;
    }

    public List<IndividualStockTest> getStockTestList() {
        return stockTestList;
    }

    public void setStockTestList(List<IndividualStockTest> stockTestList) {
        this.stockTestList = stockTestList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationTest that = (ConfigurationTest) o;
        return tradeLength == that.tradeLength && trendLength == that.trendLength && trendConfirmationLength == that.trendConfirmationLength && trendBreakLength == that.trendBreakLength && Double.compare(that.rangeThreshold, rangeThreshold) == 0 && ivAdjustmentType == that.ivAdjustmentType && ivTrendCalcType == that.ivTrendCalcType && Double.compare(that.daysVol, daysVol) == 0 && priceRocFlip == that.priceRocFlip && volatilityRocFlip == that.volatilityRocFlip && volatilitySlopeFlips == that.volatilitySlopeFlips && Double.compare(that.stopLoss, stopLoss) == 0 && Double.compare(that.percentOfVolatility, percentOfVolatility) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeLength, trendLength, trendConfirmationLength, trendBreakLength, rangeThreshold, ivAdjustmentType, ivTrendCalcType, daysVol, priceRocFlip, volatilityRocFlip, volatilitySlopeFlips, stopLoss, percentOfVolatility);
    }

    public boolean isSignalRocFlips() {
        return signalRocFlips;
    }

    public void setSignalRocFlips(boolean signalRocFlips) {
        this.signalRocFlips = signalRocFlips;
    }

    public double getIvWeighting() {
        return ivWeighting;
    }

    public void setIvWeighting(double ivWeighting) {
        this.ivWeighting = ivWeighting;
    }

    public int getDateChecks() {
        return dateChecks;
    }

    public void setDateChecks(int dateChecks) {
        this.dateChecks = dateChecks;
    }

    public double getVolumeWeighting() {
        return volumeWeighting;
    }

    public void setVolumeWeighting(double volumeWeighting) {
        this.volumeWeighting = volumeWeighting;
    }

    public double getDiscountWeighting() {
        return discountWeighting;
    }

    public void setDiscountWeighting(double discountWeighting) {
        this.discountWeighting = discountWeighting;
    }

    public double getRealizedVolWeighting() {
        return realizedVolWeighting;
    }

    public void setRealizedVolWeighting(double realizedVolWeighting) {
        this.realizedVolWeighting = realizedVolWeighting;
    }

    public TradeLog getTradeLog() {
        return tradeLog;
    }

    public void setTradeLog(TradeLog tradeLog) {
        this.tradeLog = tradeLog;
    }

    public double getAverageDelta() {
        return averageDelta;
    }

    public void setAverageDelta(double averageDelta) {
        this.averageDelta = averageDelta;
    }

    public int[] getMovingTradeLength() {
        return movingTradeLength;
    }

    public void setMovingTradeLength(int[] movingTradeLength) {
        this.movingTradeLength = movingTradeLength;
    }

    public int[] getMovingTrendLength() {
        return movingTrendLength;
    }

    public void setMovingTrendLength(int[] movingTrendLength) {
        this.movingTrendLength = movingTrendLength;
    }

    public double[] getMovingThreshold() {
        return movingThreshold;
    }

    public void setMovingThreshold(double[] movingThreshold) {
        this.movingThreshold = movingThreshold;
    }

    public boolean isUseVolatilitySurface() {
        return useVolatilitySurface;
    }

    public void setUseVolatilitySurface(boolean useVolatilitySurface) {
        this.useVolatilitySurface = useVolatilitySurface;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        ConfigurationTest clone = null;
        try  {
            clone = (ConfigurationTest) super.clone();
            clone.setStockTestList(new ArrayList<>());
            //Copy new date object to cloned method
//            clone.setDob((Date) this.getDob().clone());
        }
        catch (CloneNotSupportedException e)  {
            throw new RuntimeException(e);
        }
        return clone;
    }

    public int getVolLookback() {
        return volLookback;
    }

    public void setVolLookback(int volLookback) {
        this.volLookback = volLookback;
    }

    public double getDollarPercent() {
        return dollarPercent;
    }

    public void setDollarPercent(double dollarPercent) {
        this.dollarPercent = dollarPercent;
    }

    public double getEqualWeightedSuccess() {
        return equalWeightedSuccess;
    }

    public void setEqualWeightedSuccess(double equalWeightedSuccess) {
        this.equalWeightedSuccess = equalWeightedSuccess;
    }

    public double getTreasuryWeighting() {
        return treasuryWeighting;
    }

    public void setTreasuryWeighting(double treasuryWeighting) {
        this.treasuryWeighting = treasuryWeighting;
    }

    public double getTrendAccuracy() {
        return trendAccuracy;
    }

    public void setTrendAccuracy(double trendAccuracy) {
        this.trendAccuracy = trendAccuracy;
    }

    public double getAverageDollars() {
        return averageDollars;
    }

    public void setAverageDollars(double averageDollars) {
        this.averageDollars = averageDollars;
    }

    public double getCommodityWeighting() {
        return commodityWeighting;
    }

    public void setCommodityWeighting(double commodityWeighting) {
        this.commodityWeighting = commodityWeighting;
    }

    public double getFedTotalAssetsWeighting() {
        return fedTotalAssetsWeighting;
    }

    public void setFedTotalAssetsWeighting(double fedTotalAssetsWeighting) {
        this.fedTotalAssetsWeighting = fedTotalAssetsWeighting;
    }

    public double getRevenueRocWeighting() {
        return revenueRocWeighting;
    }

    public void setRevenueRocWeighting(double revenueRocWeighting) {
        this.revenueRocWeighting = revenueRocWeighting;
    }

    public double getDollarsBelowThreshold() {
        return dollarsBelowThreshold;
    }

    public void setDollarsBelowThreshold(double dollarsBelowThreshold) {
        this.dollarsBelowThreshold = dollarsBelowThreshold;
    }

    public double getDollarWeighting() {
        return dollarWeighting;
    }

    public void setDollarWeighting(double dollarWeighting) {
        this.dollarWeighting = dollarWeighting;
    }

    public double getPercentPositiveTests() {
        return percentPositiveTests;
    }

    public void setPercentPositiveTests(double percentPositiveTests) {
        this.percentPositiveTests = percentPositiveTests;
    }

    public int getCorrelationDays() {
        return correlationDays;
    }

    public void setCorrelationDays(int correlationDays) {
        this.correlationDays = correlationDays;
    }

    public double getOilWeighting() {
        return oilWeighting;
    }

    public void setOilWeighting(double oilWeighting) {
        this.oilWeighting = oilWeighting;
    }

    public double getVolOfReturn() {
        return volOfReturn;
    }

    public void setVolOfReturn(double volOfReturn) {
        this.volOfReturn = volOfReturn;
    }

    public double getVixWeighting() {
        return vixWeighting;
    }

    public void setVixWeighting(double vixWeighting) {
        this.vixWeighting = vixWeighting;
    }

    public double getVolOfVol() {
        return volOfVol;
    }

    public void setVolOfVol(double volOfVol) {
        this.volOfVol = volOfVol;
    }

    public double getReplayDollars() {
        return replayDollars;
    }

    public void setReplayDollars(double replayDollars) {
        this.replayDollars = replayDollars;
    }

    public double getLongDollars() {
        return longDollars;
    }

    public void setLongDollars(double longDollars) {
        this.longDollars = longDollars;
    }

    public double getShortDollars() {
        return shortDollars;
    }

    public void setShortDollars(double shortDollars) {
        this.shortDollars = shortDollars;
    }

    public double getShortPct() {
        return shortPct;
    }

    public void setShortPct(double shortPct) {
        this.shortPct = shortPct;
    }

    public double getShortPossibleProfitable() {
        return shortPossibleProfitable;
    }

    public void setShortPossibleProfitable(double shortPossibleProfitable) {
        this.shortPossibleProfitable = shortPossibleProfitable;
    }

    public double getLongPossibleProfitable() {
        return longPossibleProfitable;
    }

    public void setLongPossibleProfitable(double longPossibleProfitable) {
        this.longPossibleProfitable = longPossibleProfitable;
    }

    public double getAverageTradingDaysHeld() {
        return averageTradingDaysHeld;
    }

    public void setAverageTradingDaysHeld(double averageTradingDaysHeld) {
        this.averageTradingDaysHeld = averageTradingDaysHeld;
    }

    public double getAverageReturnPerTradingDayHeld() {
        return averageReturnPerTradingDayHeld;
    }

    public void setAverageReturnPerTradingDayHeld(double averageReturnPerTradingDayHeld) {
        this.averageReturnPerTradingDayHeld = averageReturnPerTradingDayHeld;
    }

    public double getReturnRsquared() {
        return returnRsquared;
    }

    public void setReturnRsquared(double returnRsquared) {
        this.returnRsquared = returnRsquared;
    }

    public double getLongOpenSignalValueThreshold() {
        return longOpenSignalValueThreshold;
    }

    public void setLongOpenSignalValueThreshold(double longOpenSignalValueThreshold) {
        this.longOpenSignalValueThreshold = longOpenSignalValueThreshold;
    }

    public double getLongOpenSignalRocThreshold() {
        return longOpenSignalRocThreshold;
    }

    public void setLongOpenSignalRocThreshold(double longOpenSignalRocThreshold) {
        this.longOpenSignalRocThreshold = longOpenSignalRocThreshold;
    }

    public double getLongOpenVolatilitySlopeThreshold() {
        return longOpenVolatilitySlopeThreshold;
    }

    public void setLongOpenVolatilitySlopeThreshold(double longOpenVolatilitySlopeThreshold) {
        this.longOpenVolatilitySlopeThreshold = longOpenVolatilitySlopeThreshold;
    }

    public double getShortOpenSignalValueThreshold() {
        return shortOpenSignalValueThreshold;
    }

    public void setShortOpenSignalValueThreshold(double shortOpenSignalValueThreshold) {
        this.shortOpenSignalValueThreshold = shortOpenSignalValueThreshold;
    }

    public double getShortOpenSignalRocThreshold() {
        return shortOpenSignalRocThreshold;
    }

    public void setShortOpenSignalRocThreshold(double shortOpenSignalRocThreshold) {
        this.shortOpenSignalRocThreshold = shortOpenSignalRocThreshold;
    }

    public double getShortOpenVolatilitySlopeThreshold() {
        return shortOpenVolatilitySlopeThreshold;
    }

    public void setShortOpenVolatilitySlopeThreshold(double shortOpenVolatilitySlopeThreshold) {
        this.shortOpenVolatilitySlopeThreshold = shortOpenVolatilitySlopeThreshold;
    }

    public double getShortExitTarget() {
        return shortExitTarget;
    }

    public void setShortExitTarget(double shortExitTarget) {
        this.shortExitTarget = shortExitTarget;
    }

    public double getShortExitVolatility1() {
        return shortExitVolatility1;
    }

    public void setShortExitVolatility1(double shortExitVolatility1) {
        this.shortExitVolatility1 = shortExitVolatility1;
    }

    public double getShortExitVolatility2() {
        return shortExitVolatility2;
    }

    public void setShortExitVolatility2(double shortExitVolatility2) {
        this.shortExitVolatility2 = shortExitVolatility2;
    }

    public double getShortExitVolatility3() {
        return shortExitVolatility3;
    }

    public void setShortExitVolatility3(double shortExitVolatility3) {
        this.shortExitVolatility3 = shortExitVolatility3;
    }

    public double getLongExitTarget() {
        return longExitTarget;
    }

    public void setLongExitTarget(double longExitTarget) {
        this.longExitTarget = longExitTarget;
    }

    public double getLongExitVolatility1() {
        return longExitVolatility1;
    }

    public void setLongExitVolatility1(double longExitVolatility1) {
        this.longExitVolatility1 = longExitVolatility1;
    }

    public double getLongExitVolatility2() {
        return longExitVolatility2;
    }

    public void setLongExitVolatility2(double longExitVolatility2) {
        this.longExitVolatility2 = longExitVolatility2;
    }

    public double getLongExitVolatility3() {
        return longExitVolatility3;
    }

    public void setLongExitVolatility3(double longExitVolatility3) {
        this.longExitVolatility3 = longExitVolatility3;
    }

    public double getExpectedDollars() {
        return expectedDollars;
    }

    public void setExpectedDollars(double expectedDollars) {
        this.expectedDollars = expectedDollars;
    }

    public double getAverageCapitalInUse() {
        return averageCapitalInUse;
    }

    public void setAverageCapitalInUse(double averageCapitalInUse) {
        this.averageCapitalInUse = averageCapitalInUse;
    }

    public boolean isSignalValueFlip() {
        return signalValueFlip;
    }

    public void setSignalValueFlip(boolean signalValueFlip) {
        this.signalValueFlip = signalValueFlip;
    }

    public boolean isPriceSlopeFlip() {
        return priceSlopeFlip;
    }

    public void setPriceSlopeFlip(boolean priceSlopeFlip) {
        this.priceSlopeFlip = priceSlopeFlip;
    }

    public double getLongOpenVolatilityRocThreshold() {
        return longOpenVolatilityRocThreshold;
    }

    public void setLongOpenVolatilityRocThreshold(double longOpenVolatilityRocThreshold) {
        this.longOpenVolatilityRocThreshold = longOpenVolatilityRocThreshold;
    }

    public double getShortOpenVolatilityRocThreshold() {
        return shortOpenVolatilityRocThreshold;
    }

    public void setShortOpenVolatilityRocThreshold(double shortOpenVolatilityRocThreshold) {
        this.shortOpenVolatilityRocThreshold = shortOpenVolatilityRocThreshold;
    }

    public double getStaticDollars() {
        return staticDollars;
    }

    public void setStaticDollars(double staticDollars) {
        this.staticDollars = staticDollars;
    }

    public double getShortRsquared() {
        return shortRsquared;
    }

    public void setShortRsquared(double shortRsquared) {
        this.shortRsquared = shortRsquared;
    }

    public double getSuccessRate1() {
        return successRate1;
    }

    public void setSuccessRate1(double successRate1) {
        this.successRate1 = successRate1;
    }

    public double getSuccessRate2() {
        return successRate2;
    }

    public void setSuccessRate2(double successRate2) {
        this.successRate2 = successRate2;
    }

    public double getSuccessRate3() {
        return successRate3;
    }

    public void setSuccessRate3(double successRate3) {
        this.successRate3 = successRate3;
    }

    public double getSuccessfulTrades1() {
        return successfulTrades1;
    }

    public void setSuccessfulTrades1(double successfulTrades1) {
        this.successfulTrades1 = successfulTrades1;
    }

    public double getSuccessfulTrades2() {
        return successfulTrades2;
    }

    public void setSuccessfulTrades2(double successfulTrades2) {
        this.successfulTrades2 = successfulTrades2;
    }

    public double getSuccessfulTrades3() {
        return successfulTrades3;
    }

    public void setSuccessfulTrades3(double successfulTrades3) {
        this.successfulTrades3 = successfulTrades3;
    }

    public double getFailedTrades1() {
        return failedTrades1;
    }

    public void setFailedTrades1(double failedTrades1) {
        this.failedTrades1 = failedTrades1;
    }

    public double getFailedTrades2() {
        return failedTrades2;
    }

    public void setFailedTrades2(double failedTrades2) {
        this.failedTrades2 = failedTrades2;
    }

    public double getFailedTrades3() {
        return failedTrades3;
    }

    public void setFailedTrades3(double failedTrades3) {
        this.failedTrades3 = failedTrades3;
    }

    public double[] getLongOpenVolatilitySlopes() {
        return longOpenVolatilitySlopes;
    }

    public void setLongOpenVolatilitySlopes(double[] longOpenVolatilitySlopes) {
        this.longOpenVolatilitySlopes = longOpenVolatilitySlopes;
    }

    public double[] getShortOpenVolatilitySloeps() {
        return shortOpenVolatilitySloeps;
    }

    public void setShortOpenVolatilitySloeps(double[] shortOpenVolatilitySloeps) {
        this.shortOpenVolatilitySloeps = shortOpenVolatilitySloeps;
    }


    public double[] getCategorySuccesses() {
        return categorySuccesses;
    }

    public void setCategorySuccesses(double[] categorySuccesses) {
        this.categorySuccesses = categorySuccesses;
    }

    public double[] getCategoryCounts() {
        return categoryCounts;
    }

    public void setCategoryCounts(double[] categoryCounts) {
        this.categoryCounts = categoryCounts;
    }

    public boolean isVolumeFlip() {
        return volumeFlip;
    }

    public void setVolumeFlip(boolean volumeFlip) {
        this.volumeFlip = volumeFlip;
    }
}
