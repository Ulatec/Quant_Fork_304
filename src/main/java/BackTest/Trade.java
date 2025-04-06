package BackTest;

import Model.Bar;
import Model.ConfigurationTest;
import Model.OptionContract;
import Model.Ticker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Trade {

    double tradeBasis;

    double positionSize;


    boolean isLong;

    double closingPrice;

    boolean open;

    double percentage;


   // double thirtyDayForwardPerformance;
   // double fifteenDayForwardPerformance;
    Date openDate;

    Date closeDate;

    Date trimDate;
    Bar trimBar;

    //int volBucket;
    //  int economicQuadrant;

    // String ticker;

    Ticker ticker;
    Bar openBar;
    Bar closeBar;

    long daysHeld;
    boolean half;
    double openingLongVolatilitySignalLong;

    OptionContract associatedOptionTrade;
    int tradeNumber;
    int tradeCategory;
    int crossType;
    int closingCause;
    public Trade( double tradeBasis, boolean isLong, Date date,
                 int category, Bar bar, Ticker ticker, double tradeSize, OptionContract optionContract,int crossType) {
        this.tradeBasis = tradeBasis;
        this.isLong = isLong;
        this.openDate = date;
        this.crossType = crossType;
        //this.openingSignalValueShort = bar.getSignalSlopeShort();
        this.openingLongVolatilitySignalLong = bar.getBaseLongVolatility();

        this.openBar = bar;
        //        if(thirtyDayBar != null) {
//            this.thirtyDayForwardPerformance = (thirtyDayBar.getClose() - bar.getClose())/bar.getClose();
//        }
//        if( fifteenDayBar != null){
//            this.fifteenDayForwardPerformance = (fifteenDayBar.getClose() - bar.getClose())/bar.getClose();
//        }
        this.tradeCategory = category;
        //this.positionSize = 1/Math.sqrt(trendVol/100);
        //this.positionSize = (double) volBucket/18;

        this.positionSize = tradeSize;
        if(isLong) {
//            if((bar.getClose() - priorBar.getClose())/priorBar.getClose() < -0.04){
//                this.positionSize = this.positionSize *configurationTest.getWorkingArray()[0];
//            }




//            if (bar.getBaseLongVolatility() < 20) {
//                this.positionSize = this.positionSize * 0.8;
//            } else if (bar.getBaseLongVolatility() < 42) {
//                this.positionSize = this.positionSize * 1;
//            }else{
//                this.positionSize = this.positionSize * 1.2;
//            }
//                this.positionSize = this.positionSize * 1.2;
//            }else if (bar.getBaseLongVolatility() < 58) {
//                this.positionSize = this.positionSize * 2;
//            } else {
//                this.positionSize = this.positionSize;
//            }
        }else{
            this.positionSize = this.positionSize * 0.5;
//            if((bar.getClose() - priorBar.getClose())/priorBar.getClose() > 0.04){
//                this.positionSize = this.positionSize *configurationTest.getWorkingArray()[0];
//            }

//            if(bar.getBaseLongVolatility() < 39){
//                this.positionSize = this.positionSize * 2;
//            }else
//            if (bar.getBaseLongVolatility() < 57)
//                this.positionSize = this.positionSize * 2;
//            else if (bar.getBaseLongVolatility() < 75) {
//                this.positionSize = this.positionSize * 2;
//            } else {
//                this.positionSize = this.positionSize;
//            }
        }
//        if(!isLong){
//            this.positionSize = this.positionSize;
//        }
        this.open = true;
        // this.volBucket = volBucket;
        // this.economicQuadrant = bar.getEconomicQuadrant();
        //this.tradeConfidence = (double)3/confirmationCount;
//        if(ticker.contains("AMC")){
//            this.positionSize = 0.02;
//            this.tradeConfidence = 1;
//        }
        this.ticker = ticker;
        if(optionContract != null){
            associatedOptionTrade = optionContract;
        }
        half = false;
    }

    public int getTradeCategory() {
        return tradeCategory;
    }

    public void setTradeCategory(int tradeCategory) {
        this.tradeCategory = tradeCategory;
    }

    public double getTradeBasis() {
        return tradeBasis;
    }

    public void setTradeBasis(double tradeBasis) {
        this.tradeBasis = tradeBasis;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }

    public double getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(double closingPrice) {

        this.closingPrice = closingPrice;
        if(isLong){
            setPercentage((closingPrice - tradeBasis)/tradeBasis);
        }else{
            setPercentage((tradeBasis - closingPrice)/tradeBasis);
        }

    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate, Bar bar, int closingCause) {
        this.closeDate = closeDate;
        LocalDate tempOpenDate = openDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate tempCloseDate = closeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        daysHeld = ChronoUnit.DAYS.between(tempOpenDate,tempCloseDate);
        try {
            this.closeBar = (Bar) bar.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.closingCause = closingCause;
    }


    public double getPositionSize() {
        return positionSize;
    }

    public void setPositionSize(double positionSize) {
        this.positionSize = positionSize;
    }


//    public int getVolBucket() {
//        return volBucket;
//    }
//
//    public void setVolBucket(int volBucket) {
//        this.volBucket = volBucket;
//    }


    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public long getDaysHeld() {
        return daysHeld;
    }

    public void setDaysHeld(long daysHeld) {
        this.daysHeld = daysHeld;
    }


    public double getOpeningLongVolatilitySignalLong() {
        return openingLongVolatilitySignalLong;
    }

    public void setOpeningLongVolatilitySignalLong(double openingLongVolatilitySignalLong) {
        this.openingLongVolatilitySignalLong = openingLongVolatilitySignalLong;
    }



//    public void setCloseDate(Date closeDate) {
//        this.closeDate = closeDate;
//    }

    public Bar getOpenBar() {
        return openBar;
    }

    public void setOpenBar(Bar openBar) {
        try {
            this.openBar = (Bar) openBar.clone();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bar getCloseBar() {
        return closeBar;
    }

    public void setCloseBar(Bar closeBar) {
        try {
            this.closeBar = (Bar) closeBar.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }


//    public double getThirtyDayForwardPerformance() {
//        return thirtyDayForwardPerformance;
//    }
//
//    public void setThirtyDayForwardPerformance(double thirtyDayForwardPerformance) {
//        this.thirtyDayForwardPerformance = thirtyDayForwardPerformance;
//    }
//
//    public double getFifteenDayForwardPerformance() {
//        return fifteenDayForwardPerformance;
//    }
//
//    public void setFifteenDayForwardPerformance(double fifteenDayForwardPerformance) {
//        this.fifteenDayForwardPerformance = fifteenDayForwardPerformance;
//    }

    public int getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(int tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public OptionContract getAssociatedOptionTrade() {
        return associatedOptionTrade;
    }

    public void setAssociatedOptionTrade(OptionContract associatedOptionTrade) {
        this.associatedOptionTrade = associatedOptionTrade;
    }

    public boolean isHalf() {
        return half;
    }

    public void setHalf(boolean half) {
        this.half = half;
    }

    public Date getTrimDate() {
        return trimDate;
    }

    public void setTrimDate(Date trimDate) {
        this.trimDate = trimDate;
    }

    public Bar getTrimBar() {
        return trimBar;
    }

    public void setTrimBar(Bar trimBar) {
        this.trimBar = trimBar;
    }

    public int getCrossType() {
        return crossType;
    }

    public void setCrossType(int crossType) {
        this.crossType = crossType;
    }

    public int getClosingCause() {
        return closingCause;
    }

    public void setClosingCause(int closingCause) {
        this.closingCause = closingCause;
    }
}

