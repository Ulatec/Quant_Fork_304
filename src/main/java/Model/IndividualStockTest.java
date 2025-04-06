package Model;

import BackTest.BasicBucketReadout;
import BackTest.TradeLog;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class IndividualStockTest {

    private String ticker;
    private int successfulTrades;

    private int failedTrades;

    private double successRate;
    private double dollars;
    private double staticDollars;

    private double longPct;

    private double shortPct;

    private double longDollars;

    private double shortDollars;

    private double trendAccuracy;
    public double shortPossibleProfitable;
    private TradeLog tradeLog;

    private double averageVol;
    private Bar lastBar;

    private List<LocalDate> earningsDates;
    public double successfulTrades1;
    public double successfulTrades2;
    public double successfulTrades3;
    public double failedTrades1;
    public double failedTrades2;
    public double failedTrades3;

    @Override
    public String toString() {
        return "IndividualStockTest{" +
                "ticker='" + ticker + '\'' +
                ", successfulTrades=" + successfulTrades +
                ", failedTrades=" + failedTrades +
                ", successRate=" + successRate +
                ", dollars=" + dollars +
                ", longPct=" + longPct +
                ", shortPct=" + shortPct +
                ", longDollars=" + longDollars +
                ", shortDollars=" + shortDollars +
                ", trendAccuracy=" + trendAccuracy +
                ", averageVol=" + averageVol +
                ", shortPossibleProfitable=" + shortPossibleProfitable +
               // ", basicBucketReadout=" + basicBucketReadout +
                //", quadrantBucketReadout=" + quadrantBucketReadout +
                '}';
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
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

    public double getDollars() {
        return dollars;
    }

    public void setDollars(double dollars) {
        this.dollars = dollars;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public TradeLog getTradeLog() {
        return tradeLog;
    }

    public void setTradeLog(TradeLog tradeLog) {
        this.tradeLog = tradeLog;
    }


    public double getTrendAccuracy() {
        return trendAccuracy;
    }

    public void setTrendAccuracy(double trendAccuracy) {
        this.trendAccuracy = trendAccuracy;
    }

    public double getAverageVol() {
        return averageVol;
    }

    public void setAverageVol(double averageVol) {
        this.averageVol = averageVol;
    }

    public double getLongPct() {
        return longPct;
    }

    public void setLongPct(double longPct) {
        this.longPct = longPct;
    }

    public double getShortPct() {
        return shortPct;
    }

    public void setShortPct(double shortPct) {
        this.shortPct = shortPct;
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

    public Bar getLastBar() {
        return lastBar;
    }

    public void setLastBar(Bar lastBar) {
        this.lastBar = lastBar;
    }

    public List<LocalDate> getEarningsDates() {
        return earningsDates;
    }

    public void setEarningsDates(List<LocalDate> earningsDates) {
        this.earningsDates = earningsDates;
    }

    public double getShortPossibleProfitable() {
        return shortPossibleProfitable;
    }

    public void setShortPossibleProfitable(double shortPossibleProfitable) {
        this.shortPossibleProfitable = shortPossibleProfitable;
    }


    public double getStaticDollars() {
        return staticDollars;
    }

    public void setStaticDollars(double staticDollars) {
        this.staticDollars = staticDollars;
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
}
