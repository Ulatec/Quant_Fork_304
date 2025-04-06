package Threads;

import BackTest.*;
import Libraries.StockCalculationLibrary;
import Model.*;
import Repository.RealizedVolatilityRepository;
import Util.DateFormatter;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.h2.result.SimpleResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ObjectInputFilter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static Fetchers.StockRangeTester.*;


public class BackTestThread extends Thread {
    private final int threadNum;
    public boolean log;


    public List<ConfigurationTest> configurationTestList;


    public HashMap<Ticker,HashMap<Integer,List<Bar>>> completedBarCache;

    public List<Bar> completedWithIV;

    public Ticker ticker;
    Instant now;
    Instant start;
    double delta;
    double rate;
    public int complete;
    BackTestThreadMonitor backTestThreadMonitor;
    private RealizedVolatilityRepository realizedVolatilityRepository;
   // long exclusionBegin = Date.from(LocalDate.of(2002,2,15).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
   // long exclusionEnd = Date.from(LocalDate.of(2002,4,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();

    LocalDate finalDate;
    private boolean logData = false;
    private boolean multiTicker;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");



    public BackTestThread(List<ConfigurationTest> configurationTestList,
                          List<Bar> completedWithIV,
                           boolean log,
                          BackTestThreadMonitor backTestThreadMonitor, Ticker ticker, int threadNum, RealizedVolatilityRepository realizedVolatilityRepository,
                           boolean multiTicker, LocalDate finalDate
    ){
        this.completedBarCache = new HashMap<>();
        this.configurationTestList = configurationTestList;
        this.completedWithIV = completedWithIV;

        this.log = log;
        this.backTestThreadMonitor = backTestThreadMonitor;
        this.ticker =ticker;
        this.threadNum = threadNum;
        this.realizedVolatilityRepository = realizedVolatilityRepository;
        this.multiTicker = multiTicker;
        this.finalDate = finalDate;
        complete = 0;
    }
    public void run(){

        double[] longOpenVolatilityRow = {0,0,0};
        start = Instant.now();
        StockCalculationLibrary stockCalculationLibrary = new StockCalculationLibrary();

        int completedSize = completedWithIV.size();
        for (int z = 0; z < completedSize; z++) {
            completedWithIV.get(z).setBaseVolatility(stockCalculationLibrary.getLogVarianceReverse(completedWithIV, z, (int) (63 + 1), ticker.getTicker()));
        completedWithIV.get(z).setBaseLongVolatility(stockCalculationLibrary.getLogVarianceReverse(completedWithIV, z, 90 + 1, ticker.getTicker()));
            setPreviousEarningsDates(completedWithIV,z);
        }
        completedBarCache.put(ticker,new HashMap<>());

        int configSize = configurationTestList.size();
        for (int configurationIndex = 0; configurationIndex<configSize; configurationIndex++ ) {
                ConfigurationTest configurationTest = configurationTestList.get(configurationIndex);


            ArrayList<Bar> barList = new ArrayList<>(completedWithIV.size());
            RegressionCompareObject regressionCompareObject = new RegressionCompareObject(
                    configurationTest.getMovingTrendLength()[1], configurationTest.getRevenueRocWeighting(), configurationTest.getMovingTrendLength()[2],
                    configurationTest.getOilWeighting(), configurationTest.getMovingTrendLength()[0], configurationTest.getCommodityWeighting(),configurationTest.getMovingTrendLength()[5]
            );

            int hashCode = regressionCompareObject.hashCode();
            if (completedBarCache.get(ticker).get(hashCode) != null) {

                barList = (ArrayList<Bar>) completedBarCache.get(ticker).get(hashCode);

            } else {

                int completedWithIVSize = completedWithIV.size();
                //barList = (ArrayList<Bar>) completedWithIV;
                for (int itemIndex = 0; itemIndex < completedWithIVSize; itemIndex++) {
                    Bar item = completedWithIV.get(itemIndex);
                    try {
                        barList.add((Bar) item.clone());
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
                for (int z = 0; z < completedSize; z++) {
                //    barList.get(z).setBaseVolatility(stockCalculationLibrary.getLogVarianceReverse(barList, z, (int) (configurationTest.getVolLookback() + 1), ticker.getTicker()));
                    // barList.get(z).setNasdaqVol(stockCalculationLibrary.getNasdaqLogVarianceReverse(nasdaqBars, nasdaqBars.get(z), nasdaqBars.get(z).getDate(), (int) (configurationTest.getVolLookback() + 1), z));
                }
                // Collections.copy(barListSafe, barList);
                Collections.reverse(barList);

                int size = barList.size();




                for (int z = 0; z < size; z++) {
                //    calculateTrendLookback(stockCalculationLibrary, configurationTest, barList, z, false);
                }



                for (int x = 0; x < size; x++) {
                }

                getVolumeChange(barList, configurationTest.getTreasuryWeighting(), configurationTest);
                completedBarCache.get(ticker).put(hashCode, barList);
            }

            Collections.reverse(barList);


            double dollars;
            //double optionDollars = 10000;
            if (configurationTest.getDollars() == 0.0) {
                dollars = 100.00;
            } else {
                dollars = configurationTest.getDollars();
            }

            //   double longTradeBasis = 0.0;
            double longsuccess = 0.0;
            double longfail = 0.0;
            double shortsuccess = 0.0;
            double shortfail = 0.0;
            int longconsecutiveTrendBreaks = 0;
            int shortconsecutiveTrendBreaks = 0;
            int longconsecutiveTrendConfirm = 0;
            // int shortconsecutiveTrendConfirm = 0;
            boolean stopLossActive = false;
            // List<String> orderedStrings = new ArrayList<>();
            TradeLog tradeLog = new TradeLog();
            int listSize = barList.size();
            for (int z = 0; z < listSize; z++) {
                if (z > 60) {
                    Bar bar = barList.get(z);

                    boolean longAction = false;
                    boolean shortAction = false;




//  LONG CLOSE //
                        if (tradeLog.isLongActive()) {
                            TradeIntent longTradeIntent = longExitConditions(bar, barList.get(z - 1), configurationTest.getPercentOfVolatility(), configurationTest.getStopLoss(),
                                    configurationTest, tradeLog.getLongBasis(), longconsecutiveTrendBreaks, tradeLog, barList, z);
                            if (longTradeIntent != null) {
                                if (longTradeIntent.getTradeComment().contains("Trend Break") ||
                                        longTradeIntent.getTradeComment().contains("Stop Loss") || longTradeIntent.getTradeComment().contains("Earnings")) {
//                                    for (Trade trade : tradeLog.getActiveTradeList()) {
//                                        if (trade.getAssociatedOptionTrade() != null) {
//                                            double close = getLastforContract(trade.getAssociatedOptionTrade(), bar);
//                                            trade.setOptionClosePrice(close);
//                                        }
//                                    }
                                    int cause = 0;
                                    if(longTradeIntent.getTradeComment().contains("Stop Loss")){
                                        cause = 1;
                                    }else if(longTradeIntent.getTradeComment().contains("Earnings")){
                                        cause = 2;
                                    }else if(longTradeIntent.getTradeComment().contains("Trend Break")){
                                        cause = 3;
                                    }
                                    exitAllTradesOnSide(tradeLog, bar, barList.get(z - 1), true, longTradeIntent.isEscapeFlag(), cause);

                                    //stopLossActive = true;
                                    //longTradeActive = false;
                                } else if (longTradeIntent.getTradeComment().contains("Trim")) {
                                    trimTrade(tradeLog, true, bar, configurationTest, longTradeIntent.getTargetTrade());
                                } else {
//                                    if (longTradeIntent.getTargetTrade().getAssociatedOptionTrade() != null) {
//                                        double close = getLastforContract(longTradeIntent.getTargetTrade().getAssociatedOptionTrade(), bar);
//                                        longTradeIntent.getTargetTrade().setOptionClosePrice(close);
//                                    }
                                    int cause = 4;

                                    exitOneTrade(tradeLog, true, bar, configurationTest, longTradeIntent.getTargetTrade(), cause);


                                }
                               // action.append(longTradeIntent.getTradeComment());
                                longAction = true;
                            }
                        }
                        // SHORT CLOSE //
                        if (tradeLog.isShortActive()) {
                            TradeIntent shortTradeIntent = shortExitConditions(bar, barList.get(z - 1), configurationTest.getPercentOfVolatility(), configurationTest.getStopLoss(),
                                    configurationTest, tradeLog, shortconsecutiveTrendBreaks, barList, z);
                            if (shortTradeIntent != null) {
                                if (shortTradeIntent.getTradeComment().contains("Trend Break") ||
                                        shortTradeIntent.getTradeComment().contains("Stop Loss") || shortTradeIntent.getTradeComment().contains("Earnings")) {

                                    int cause = 0;
                                    if(shortTradeIntent.getTradeComment().contains("Stop Loss")){
                                        cause = 1;
                                    }else if(shortTradeIntent.getTradeComment().contains("Earnings")){
                                        cause = 2;
                                    }else if(shortTradeIntent.getTradeComment().contains("Trend Break")){
                                        cause = 3;
                                    }
                                    exitAllTradesOnSide(tradeLog, bar, barList.get(z - 1), false, shortTradeIntent.isEscapeFlag(), cause);

                                } else {
                                    int cause = 4;
                                    exitOneTrade(tradeLog, false, bar, configurationTest, shortTradeIntent.getTargetTrade(), cause);
                                }
                                //action.append(shortTradeIntent.getTradeComment());
                                shortAction = true;
                            }

                        }
                        boolean openNewLong = false;
                        // OPEN //
                        if (!longAction) {
//                        if ( bar.getTrend() != 0.0 && bar.getTrend() != Double.POSITIVE_INFINITY) {
                            boolean found = false;
                            for (Trade tradeInLog : tradeLog.getActiveTradeList()) {
                                if (!tradeInLog.isLong()) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                TradeIntent longTradeIntent = longOpenConditions(barList.get(z - 2), barList.get(z - 1), bar, null, tradeLog, longconsecutiveTrendConfirm,
                                        configurationTest, stopLossActive, barList, null, z);
                                if (longTradeIntent != null) {
                                    //  OptionContract optionContract = getOptionContract(ticker.getTicker(),bar);
                                    OptionContract optionContract = null;
                                    //System.out.println(bar.getDate() + " has a best option contact of: " + optionContract);
                                    tradeLog.pushNewActiveTrade(createNewTrade(barList, z, bar, true,longTradeIntent.getSizeFactor(), ticker, configurationTest.getDollarPercent(), optionContract, configurationTest,longTradeIntent.getCrossType()));

                                    //  action.append(longTradeIntent.getTradeComment());
                                    openNewLong = true;

                                }
                            }
                            //     else {
                            //     else {
                        }
                        if (!shortAction && !openNewLong) {
                            boolean found = false;
                            for (Trade tradeInLog : tradeLog.getActiveTradeList()) {
                                if (tradeInLog.isLong()) {
                                    found = true;
                                    break;
                                }
                            }
                            //  }
                            // else if (bar.getTrend() != 0.0 && bar.getTrend() != Double.POSITIVE_INFINITY) {
                            if (!found) {
                                TradeIntent shortTradeIntent = shortOpenConditions(barList.get(z - 2), barList.get(z - 1), bar, null, tradeLog,
                                        0, configurationTest,
                                        stopLossActive, barList, null, null, null, z);
                                OptionContract optionContract = null;
                                if (shortTradeIntent != null) {
                                    tradeLog.pushNewActiveTrade(createNewTrade(barList, z, bar, false, shortTradeIntent.getSizeFactor(), ticker, configurationTest.getDollarPercent(), optionContract, configurationTest,0));

                               //    action.append(shortTradeIntent.getTradeComment());

                                }
                            }
                        }
                }
            }

            double longDollars = 0.0;
            double shortDollars = 0.0;
            double staticChange = 0;
            int tradeSize = tradeLog.getClosedTradeList().size();
            double successfulTrades1 = 0.0;
            double successfulTrades2 = 0.0;
            double successfulTrades3 = 0.0;
            double failedTrades1 = 0.0;
            double failedTrades2 = 0.0;
            double failedTrades3 = 0.0;
            for(int tradeIndex = 0; tradeIndex < tradeSize; tradeIndex++ ){
                Trade closedTrade = tradeLog.getClosedTradeList().get(tradeIndex);
                double mktCap = (double) closedTrade.getCloseBar().getMarketCap();



            //for (Trade closedTrade : tradeLog.getClosedTradeList()) {
                double exit = closedTrade.getClosingPrice();
                double dollarChange;

                // boolean success;
                if (closedTrade.isLong()) {
                    double delta = (((exit - closedTrade.getTradeBasis()) / closedTrade.getTradeBasis()));
                    staticChange = staticChange + ((100 * closedTrade.getPositionSize()  * configurationTest.getDollarPercent()) * delta);
                    // dollarChange = ((dollars * configurationTest.getDollarPercent()) * (((exit - closedTrade.getTradeBasis()) / closedTrade.getTradeBasis())));
                    //dollarChange = ((dollars * closedTrade.getPositionSize()) * (((exit - closedTrade.getTradeBasis()) / closedTrade.getTradeBasis())));
                    dollarChange = ((dollars * closedTrade.getPositionSize()  * configurationTest.getDollarPercent()) * delta);

                    longDollars += dollarChange;
                    dollars = dollars + dollarChange;
                    // endingDollarList.add(dollars);
                    // closedTrade.setEndingDollarTotal(dollars);
                    if (exit > closedTrade.getTradeBasis()) {
                        longsuccess = longsuccess + 1;
                        if(mktCap < 2000000000){
                            successfulTrades1++;
                        }else if(mktCap < 50000000000L){
                            successfulTrades2++;
                        }else if(mktCap > 50000000000L){
                            successfulTrades3++;
                        }
                        //     success = true;
                    } else {
                        longfail = longfail + 1;
                        if(mktCap < 2000000000){
                            failedTrades1++;
                        }else if(mktCap < 50000000000L){
                            failedTrades2++;
                        }else if(mktCap > 50000000000L){
                            failedTrades3++;
                        }
                        //    success = false;
                    }
                } else {
                    double delta = (((closedTrade.getTradeBasis() - exit) / closedTrade.getTradeBasis()));
                    staticChange = staticChange + ((100 * closedTrade.getPositionSize()  * configurationTest.getDollarPercent()) * delta);
                    // dollarChange = ((dollars * configurationTest.getDollarPercent()) * (((closedTrade.getTradeBasis() - exit) / closedTrade.getTradeBasis())));
                    //dollarChange = ((dollars * closedTrade.getPositionSize()) * (((closedTrade.getTradeBasis() - exit) / closedTrade.getTradeBasis())));
                    dollarChange = ((dollars * closedTrade.getPositionSize()  * configurationTest.getDollarPercent()) * delta);

                    shortDollars += dollarChange;
                    dollars = dollars + dollarChange;
                    // endingDollarList.add(dollars);
                    //closedTrade.setEndingDollarTotal(dollars);
                    if (exit < closedTrade.getTradeBasis()) {
                        shortsuccess = shortsuccess + 1;
                        if(mktCap < 2000000000){
                            successfulTrades1++;
                        }else if(mktCap < 50000000000L){
                            successfulTrades2++;
                        }else if(mktCap > 50000000000L){
                            successfulTrades3++;
                        }
                        //    success = true;
                    } else {
                        shortfail = shortfail + 1;
                        if(mktCap < 2000000000){
                            failedTrades1++;
                        }else if(mktCap < 50000000000L){
                            failedTrades2++;
                        }else if(mktCap > 50000000000L){
                            failedTrades3++;
                        }
                         //   success = false;
                    }
                }
            }
            Collections.reverse(barList);
            double volTotal = 0.0;
            int volCount = 0;

            double successRate = (longsuccess + shortsuccess) / ((longsuccess + shortsuccess) + (longfail + shortfail));
            extracted(configurationTest, dollars, longsuccess, longfail, shortsuccess, shortfail, volTotal, volCount, successRate);
            //configurationTest.setDaysVol(daysVol);

            IndividualStockTest individualStockTest = new IndividualStockTest();
            individualStockTest.setAverageVol(volTotal / volCount);
            individualStockTest.setTicker(ticker.getTicker());
            individualStockTest.setSuccessfulTrades((int) (longsuccess + shortsuccess));
            individualStockTest.setFailedTrades((int) (longfail + shortfail));
            individualStockTest.setSuccessRate(successRate);
            individualStockTest.setDollars(dollars);
            individualStockTest.setTradeLog(tradeLog);
            individualStockTest.setStaticDollars(staticChange);
            individualStockTest.setLongPct((double) longsuccess / (longsuccess + longfail));
            individualStockTest.setShortPct((double) shortsuccess / (shortsuccess + shortfail));
            individualStockTest.setShortDollars(shortDollars);
            individualStockTest.setLongDollars(longDollars);
            individualStockTest.setFailedTrades1(failedTrades1);
            individualStockTest.setFailedTrades2(failedTrades2);
            individualStockTest.setFailedTrades3(failedTrades3);
            individualStockTest.setSuccessfulTrades1(successfulTrades1);
            individualStockTest.setSuccessfulTrades2(successfulTrades2);
            individualStockTest.setSuccessfulTrades3(successfulTrades3);
            if(barList.size()>0) {
                individualStockTest.setLastBar(barList.get(barList.size() - 1));
            }
           // individualStockTest.setLongPossibleProfitable(longsPossibleProfitable);
           // individualStockTest.setShortPossibleProfitable(shortsPossibleProfitable);

            configurationTest.getStockTestList().add(individualStockTest);

        }
        completedBarCache = null;
        completedWithIV = null;
        backTestThreadMonitor.threadFinished(threadNum, configurationTestList);
    }

    private void extracted(ConfigurationTest configurationTest, double dollars, double longsuccess, double longfail, double shortsuccess, double shortfail, double volTotal, int volCount, double successRate) {
        complete++;
        now = Instant.now();
        delta = Duration.between(start, now).toMillis();
        rate = ((float) complete / delta) * 1000;
        boolean sizeOne = configurationTestList.size() != 1;


    }






    public Trade createNewTrade(List<Bar> barList, int index, Bar bar, boolean isLong, int category, Ticker ticker, double tradeSize, OptionContract optionContract,ConfigurationTest configurationTest, int crossType){
        Bar thirtyBar = null;
        Bar fifteenBar = null;
        Bar priorBar = barList.get(index - 1);
        if(index + 30 < barList.size()){
            thirtyBar = barList.get(index +30);
        }
        if(index + 15 < barList.size()){
            fifteenBar = barList.get(index +15);
        }

        Double quadReturn = 0.0;

        int confirmationCount = 0;

        return new Trade( bar.getClose(), isLong, bar.getDate(), category, bar, ticker, tradeSize, optionContract,crossType);
    }

    public void exitAllTradesOnSide(TradeLog tradeLog, Bar bar, Bar priorBar, boolean isLong, boolean escapeFlag, int closingCause){
        double exitPrice = bar.getClose();
        if(escapeFlag){
            exitPrice = 0;
        }
        Date date = bar.getDate();
        if(escapeFlag){
            date = priorBar.getDate();
        }
        tradeLog.closeAllOnSide(isLong, exitPrice, date, bar, escapeFlag, closingCause);
    }
    public void trimTrade(TradeLog tradeLog, boolean isLong, Bar bar, ConfigurationTest configurationTest, Trade trade){

        // if(isLong) {
        //          if(tradeLog.getActiveTrades() >1) {
        // tradeLog.closeSpecificTrade(trade, bar.getClose(), bar.getDate(), bar);
        //        }
        //  }else{
        tradeLog.trimPosition(trade,bar.getClose(), bar.getDate(), bar);
        //  }

    }
    public void exitOneTrade(TradeLog tradeLog, boolean isLong, Bar bar, ConfigurationTest configurationTest, Trade trade, int closingCause){

        // if(isLong) {
        //          if(tradeLog.getActiveTrades() >1) {
        // tradeLog.closeSpecificTrade(trade, bar.getClose(), bar.getDate(), bar);
        //        }
        //  }else{
        tradeLog.closeSpecificTrade(trade,bar.getClose(), bar.getDate(), bar, closingCause);
        //  }

    }

    public TradeIntent longOpenConditions(Bar twoBarsPrior, Bar priorBar, Bar bar, List<QuarterlyQuad> quads, TradeLog tradeLog,
                                          int longconsecutiveTrendConfirm,
                                          ConfigurationTest configurationTest, boolean stopLossActive,
                                          List<Bar> barList, List<Double> allSlopes, int x) {
        boolean activeTrades = tradeLog.getActiveTrades() > 0;
        //Logic driver for  cross1
        boolean cross1 = false;
        if (activeTrades) {
            int crossType = tradeLog.getActiveTradeList().get(0).getCrossType();
            if (crossType == 1) {
                cross1 = true;
            }
        }

        //Logic driver for cross2
        boolean cross2 = false;
        if (activeTrades) {
            int crossType = tradeLog.getActiveTradeList().get(0).getCrossType();
            if (crossType == 2) {
                cross2 = true;
            }
        }





        int category = 0;


        //check if earnings
        boolean tradeCriteria = true;
        //LocalDate convertedDate = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int bound = 3;
        if (x + 3 > barList.size()) {
            bound = barList.size() - x - 1;
        }
        for (int i = 1; i <= bound; i++) {
            if (barList.get(x + i - 1).isEarningsDate()) {
                tradeCriteria = false;
                return null;
             //   break;
            }
        }

        double average3First = 0.0;
        double average3Second = 0.0;
        boolean pass = false;
            if (x - 4 >= 0) {

            for (int i = 1; i <= 2; i++) {
                if (x-i>= 0){
                    average3First += (barList.get(x-i).getClose());
                }
            }
            average3First /= 2;
            for (int i = 3; i <= 4; i++) {
                if (x-i>= 0){
                    average3Second += (barList.get(x-i).getClose());
                }
            }
                average3Second /= 2;
             pass = average3First < average3Second;
        }else{
                pass = true;
            }
            if(!pass){
                return null;
            }
//        double sum = 0;
//        int limit;
//        if (x > 5) {
//            limit = 5;
//        } else {
//            limit = x;
//        }
        long convertedTimeStamp = barList.get(x).getDate().getTime();
        if (bar.getMostRecentEarnings() != 0) {
            long upperBound = bar.getMostRecentEarnings() + (98L * 24 * 3600000);
            long lowerBound = bar.getMostRecentEarnings() + (82L * 24 * 3600000);
            if((convertedTimeStamp > lowerBound) && (convertedTimeStamp < upperBound)){
                return null;
            }

        }
        if (bar.isYearAgoEarnings()) {

            /* 98 days  * 24 hours * 3600000 ms per day*/
            //long upperBound = bar.getYearAgoEarnings() + (260L * 24 * 3600000);
           // long lowerBound = bar.getYearAgoEarnings() + (242L * 24 * 3600000);
           // if ((convertedTimeStamp > lowerBound) && (convertedTimeStamp < upperBound)) {
                return null;
           // }
        }
        // if(bar.getMarketCap() != 0){
        if (bar.getMarketCap() < configurationTest.getMovingTrendLength()[7] * 1000000) {
            tradeCriteria = false;
            return null;
        }


//        for (int d = 0; d < limit; d++) {
//            sum = sum + (barList.get(x - d).getVolume() * barList.get(x - d).getClose());
//        }
        if(bar.getAverageVolume() < configurationTest.getMovingTrendLength()[6] * 1000){
            tradeCriteria = false;
            return null;
        }

        double volSlopeThreshold = -0.25;
        //volSlopeThreshold = configurationTest.getLongOpenVolatilitySlopeThreshold();
        double volRocThreshold = -0.01;
        //volRocThreshold = configurationTest.getLongOpenVolatilityRocThreshold();
        double signalRocThresholdx = -7e-11;
        signalRocThresholdx = configurationTest.getLongOpenSignalRocThreshold();
        double signalValueThreshold = -10e-11;
        //signalValueThreshold = configurationTest.getLongOpenSignalValueThreshold();
        boolean volatilityRocFlips = false;
        //volatilityRocFlips = configurationTest.isVolatilityRocFlip();
        boolean volatilitySlopeFlips = false;
        //volatilitySlopeFlips = configurationTest.isVolatilitySlopeFlips();
        boolean signalRocFlips = true;
        //signalRocFlips = configurationTest.isSignalRocFlips();
        boolean signalValueFlips = false;
        //signalValueFlips = configurationTest.isSignalValueFlip();
        double priceSlopeThreshold = 1.0e-11;
        //priceSlopeThreshold = configurationTest.getIvWeighting();
        double volumeThreshold = -0.2;
        //volumeThreshold = configurationTest.getVolumeWeighting();
        double countLimit = 4;
        boolean priceSlopeFlip = false;
        //priceSlopeFlip = configurationTest.isPriceSlopeFlip();
        boolean volumeFlip = false;
        volumeFlip = configurationTest.isVolumeFlip();


        if ((cross1 && !cross2) || (!cross1 && !cross2)) {
            if (tradeCriteria) {
                int longCount = 0;
                for (Trade activeTrade : tradeLog.getActiveTradeList()) {
                    if (activeTrade.isLong()) {
                        longCount++;
                    }
                }
                if(longCount < countLimit){
          //          if(priorBar.getSignalroccdf() < 0.5 && bar.getSignalroccdf() > 0.5){
         //               if(bar.getSignalcdf() < 0.5){
                if (tradeLog.getActiveTrades() == 0) {

                    if (bar.getBaseVolatility() > bar.getBaseLongVolatility()) {
                        boolean cross = true;
                        if (!cross) {
                            return null;
                        }

                        boolean signalValue = false;
                        if (signalValueFlips) {
                            signalValue = bar.getSignalcdf() > 0.2 && bar.getSignalcdf() < 0.9;
                        } else {
                            signalValue =  bar.getSignalcdf() < 0.2 || bar.getSignalcdf() > 0.9;
                        }
                        if (!signalValue) {
                            return null;
                        }
                        boolean signalRoc = false;
                        if (signalRocFlips) {
                            signalRoc = bar.getSignalroccdf() > 0.2 && bar.getSignalroccdf() < 0.8;
                        } else {
                            signalRoc = bar.getSignalroccdf() < 0.2 || bar.getSignalroccdf() > 0.8;
                        }
                        if (signalRoc) {
                            boolean volatilitySlope = (bar.getVolcdf() != 0.0);
                            if (volatilitySlopeFlips) {
                                if(!volatilitySlope) {
                                    volatilitySlope = bar.getVolcdf() > 0.3 && bar.getVolcdf() < 0.7;
                                }
                            } else {
                                if(!volatilitySlope) {
                                    volatilitySlope = bar.getVolcdf() < 0.3 || bar.getVolcdf() > 0.7;
                                }
                            }
                            if (volatilitySlope) {
                                boolean volatilityRoc = (bar.getVolcdf()!= 0.0);
                                if (volatilityRocFlips) {
                                    if(!volatilityRoc) {
                                        volatilityRoc = bar.getVolroccdf() > 0.3  && bar.getVolroccdf() < 0.7;
                                    }
                                } else {
                                    if(!volatilityRoc) {
                                        volatilityRoc =  bar.getVolroccdf() < 0.3  || bar.getVolroccdf() > 0.7;
                                    }
                                }
                                if (volatilityRoc) {

                                                                        return new TradeIntent("Long Open", "long", "open", category, null, false, 0);

                                            }
                                        }
                                    }
                                }

//}
//                        }
                    }
                } else {

                }
            }
        }



        return null;

    }

    public void setPreviousEarningsDates(List<Bar> barList, int x){

        Bar foundBar = null;
        int limit1;
        if(x > 270){
            limit1 = 270;
        }else{
            limit1 = x-1;
        }
        for(int i = 1; i <=limit1-1; i++){
            if(x-i-1 > 0) {
                if (barList.get(x - i - 1).isEarningsDate()) {
                    foundBar = barList.get(x - i - 1);
                    barList.get(x).setMostRecentEarnings(foundBar.getDate().getTime());
                    break;
                }
            }
        }

        //foundBar = null;
        if(x > 256){
            limit1 = 256;
        }else{
            limit1 = x-1;
        }
        //one year ago long = 252 * 1440 *1000
        for(int i = 248; i <=limit1-1; i++){
            if(x-i-1 > 0) {
                if (barList.get(x - i - 1).isEarningsDate()) {
                       // foundBar = barList.get(x - i - 1);
                        barList.get(x).setYearAgoEarnings(true);

                    break;
                }
            }
        }

        double sum = 0;
        int limit = 0;
        if(x > 5){
            limit = 5;
        }else{
            limit = x;
        }
        for(int d = 0; d < limit; d++){
            sum = sum + (barList.get(x - d).getVolume() * barList.get(x - d).getClose());
        }
        barList.get(x).setAverageVolume(sum/limit);

    }

    public void getVolumeChange(List<Bar> bars, double test, ConfigurationTest configurationTest){
        for(int z = 0; z < bars.size(); z++) {
            if( bars.get(z).getMarketCap() > 50000000000L){
                test = test;
            }
            if(z + test < bars.size()) {

                bars.get(z).setVolume(bars.get(z).getVolume());
                double sum = 0.0;
                double l = test/2;
                for (int x = 0; x < l; x++) {
                    sum += bars.get(z + x).getVolume();
                }
                sum = sum / l;
                double oldSum = 0.0;
                for (int x = (int) l; x < test; x++) {
                    oldSum += bars.get(z + x).getVolume();
                }
                oldSum = oldSum / (int) l;

                bars.get(z).setVolumeChange((sum - oldSum) / oldSum);
            }

        }
    }

    public TradeIntent shortOpenConditions(Bar twoBarsPrior, Bar priorBar, Bar bar, List<QuarterlyQuad> quads, TradeLog tradeLog,
                                           int shortconsecutiveTrendConfirm,
                                           ConfigurationTest configurationTest, boolean stopLossActive,
                                           List<Bar> barList, List<Double> allSlopes, List<Double> allPriceSlopes, List<Double> allVolatilitySlopes, int x){

        boolean activeTrades = tradeLog.getActiveTrades() > 0;
//        boolean cross1 = false;
//        if (activeTrades) {
//            int crossType = tradeLog.getActiveTradeList().get(0).getCrossType();
//            if (crossType == 1) {
//                cross1 = true;
//            }
//        }
//
//        //Logic driver for cross2
//        boolean cross2 = false;
//        if (activeTrades) {
//            int crossType = tradeLog.getActiveTradeList().get(0).getCrossType();
//            if (crossType == 2) {
//                cross2 = true;
//            }
//        }

        int category = 0;




        long convertedTimeStamp = barList.get(x).getDate().getTime();
        if(bar.getMostRecentEarnings() != 0){
            long upperBound = bar.getMostRecentEarnings() + (98L * 24 * 3600000);
            long lowerBound = bar.getMostRecentEarnings() + (82L * 24 * 3600000);
            if((convertedTimeStamp > lowerBound) && (convertedTimeStamp < upperBound)){
                return null;
            }
        }
        if (bar.isYearAgoEarnings()) {

            /* 98 days  * 24 hours * 3600000 ms per day*/
            //long upperBound = bar.getYearAgoEarnings() + (260L * 24 * 3600000);
            // long lowerBound = bar.getYearAgoEarnings() + (242L * 24 * 3600000);
            // if ((convertedTimeStamp > lowerBound) && (convertedTimeStamp < upperBound)) {
            return null;
            // }
        }
        int limit;
        //check if earnings
        boolean tradeCriteria = true;
        //LocalDate convertedDate = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int bound = 3;
        if(x + 3 > barList.size()){
            bound = barList.size()-x-1;
        }
        for(int i = 1; i <=bound; i++){
            if(barList.get(x+i-1).isEarningsDate()){
                tradeCriteria = false;
                break;
            }
        }

        double average3First = 0.0;
        double average3Second = 0.0;
        boolean pass = false;
        if (x - 4 >= 0) {

            for (int i = 1; i <= 2; i++) {
                if (x-i>= 0){
                    average3First += (barList.get(x-i).getClose());
                }
            }
            average3First /= 2;
            for (int i = 3; i <= 4; i++) {
                if (x-i>= 0){
                    average3Second += (barList.get(x-i).getClose());
                }
            }
            average3Second /= 2;
            pass = average3First > average3Second;
        }else{
            pass = true;
        }
        if(!pass){
            return null;
        }
        // if(bar.getMarketCap() != 0){
        if(bar.getMarketCap() < configurationTest.getMovingTrendLength()[7] * 1000000) {
            tradeCriteria = false;
        }

        if(bar.getAverageVolume() < configurationTest.getMovingTrendLength()[6] * 1000){
            tradeCriteria = false;
        }


       // if ((cross1 && !cross2) || (!cross1 && !cross2)) {
        double volSlopeThreshold = -0.25;
        //volSlopeThreshold = configurationTest.getLongOpenVolatilitySlopeThreshold();
        double volRocThreshold = -0.01;
        //volRocThreshold = configurationTest.getLongOpenVolatilityRocThreshold();
        double signalRocThresholdx = -7e-11;
        signalRocThresholdx = configurationTest.getLongOpenSignalRocThreshold();
        double signalValueThreshold = -10e-11;
        //signalValueThreshold = configurationTest.getLongOpenSignalValueThreshold();
        boolean volatilityRocFlips = false;
        volatilityRocFlips = configurationTest.isVolatilityRocFlip();
        boolean volatilitySlopeFlips = false;
        volatilitySlopeFlips = configurationTest.isVolatilitySlopeFlips();
        boolean signalRocFlips = false;
        signalRocFlips = configurationTest.isSignalRocFlips();
        boolean signalValueFlips = true;
        signalValueFlips = configurationTest.isSignalValueFlip();
        double priceSlopeThreshold = 1.0e-11;
        //priceSlopeThreshold = configurationTest.getIvWeighting();
        double volumeThreshold = -0.2;
        //volumeThreshold = configurationTest.getVolumeWeighting();
        double countLimit = 4;
        boolean priceSlopeFlip = false;
        //priceSlopeFlip = configurationTest.isPriceSlopeFlip();
        boolean volumeFlip = false;
        volumeFlip = configurationTest.isVolumeFlip();


            if (tradeCriteria) {
                int longCount = 0;
                for (Trade activeTrade : tradeLog.getActiveTradeList()) {
                    if (activeTrade.isLong()) {
                        longCount++;
                    }
                }
                if(longCount < countLimit){
                    //    if (tradeLog.getActiveTrades() == 0) {

                    if (bar.getBaseVolatility() < bar.getBaseLongVolatility()) {
                        boolean cross = true;
//                        if (signalRocFlips) {
//                            cross = priorBar.getSignalRocLong() > 0 && bar.getSignalRocLong() < 0;
//                        } else {
//                            cross = priorBar.getSignalRocLong() < 0 && bar.getSignalRocLong() > 0;
//                        }
                        if (!cross) {
                            return null;
                        }
                    //    if(bar.getSignalroccdf() > 0.5 && priorBar.getSignalroccdf() < 0.5){
                   //         if(bar.getSignalcdf() > 0.5){
                        boolean signalValue = false;
//                        if (signalValueFlips) {
//                            signalValue = bar.getSignalSlopeLong() > signalValueThreshold;
//                        } else {
//                            signalValue = bar.getSignalSlopeLong() < signalValueThreshold;
//                        }
                        if (signalValueFlips) {
                            signalValue = bar.getSignalcdf() < configurationTest.getShortOpenSignalValueThreshold() || bar.getSignalcdf() > configurationTest.getLongOpenSignalValueThreshold();
                        } else {
                            signalValue =  bar.getSignalcdf() > configurationTest.getShortOpenSignalValueThreshold() && bar.getSignalcdf() < configurationTest.getLongOpenSignalValueThreshold();
                        }
                        if (!signalValue) {
                            return null;
                        }
                        boolean signalRoc = false;
                        if (signalRocFlips) {
                            signalRoc = bar.getSignalroccdf() < configurationTest.getShortOpenSignalRocThreshold() || bar.getSignalroccdf() > configurationTest.getLongOpenSignalRocThreshold();
                        } else {
                            signalRoc = bar.getSignalroccdf() > configurationTest.getShortOpenSignalRocThreshold() && bar.getSignalroccdf() < configurationTest.getLongOpenSignalRocThreshold();
                        }
                        if (signalRoc) {
                            boolean volatilitySlope = (bar.getVolcdf() != 0.0);
                            if (volatilitySlopeFlips) {
                                if(!volatilitySlope) {
                                    volatilitySlope = bar.getVolcdf() < configurationTest.getShortOpenVolatilitySlopeThreshold() || bar.getVolcdf() > configurationTest.getLongOpenVolatilitySlopeThreshold();
                                }
                            } else {
                                if(!volatilitySlope) {
                                    volatilitySlope = bar.getVolcdf() > configurationTest.getShortOpenVolatilitySlopeThreshold() && bar.getVolcdf() < configurationTest.getLongOpenVolatilitySlopeThreshold();
                                }
                            }
                            if (volatilitySlope) {
                                boolean volatilityRoc = (bar.getVolroccdf() == 0.0);
                                if (volatilityRocFlips) {
                                    if(!volatilityRoc) {
                                        volatilityRoc = bar.getVolroccdf() < configurationTest.getShortOpenVolatilityRocThreshold()  || bar.getVolroccdf() > configurationTest.getLongOpenVolatilityRocThreshold();
                                    }
                                } else {
                                    if(!volatilityRoc) {
                                        volatilityRoc =  bar.getVolroccdf() > configurationTest.getShortOpenVolatilityRocThreshold()  && bar.getVolroccdf() < configurationTest.getLongOpenVolatilityRocThreshold();
                                    }
                                }
                                if (volatilityRoc) {

                                                                        return new TradeIntent("Short Open", "short", "open", category, null, false, 1);

                                                                }
                                                            }
                                                        }
                                                    }

                }

            }

        return null;

    }


    public TradeIntent longExitConditions(Bar bar, Bar priorBar, double percentageOfVolatility, double stopLossPercentage,
                                          ConfigurationTest configurationTest, double longTradeBasis, int longConsecutiveTrendBreaks, TradeLog tradeLog, List<Bar> bars, int z){

        boolean earningsFound = false;
        long timestamp = bar.getDate().getTime();
        long priortimestamp = priorBar.getDate().getTime();
        if(priortimestamp + (3600000*24*15) < (timestamp)){
            return new TradeIntent("Trend Break", "long", "close", 1, null,true,0);
        }

        int bound = 3;
        if(z + 3 > bars.size()){
            bound = bars.size()-z-1;
        }
        for(int i = 1; i <=bound; i++){
            if(bars.get(z+i-1).isEarningsDate()){
                earningsFound = true;
                break;
            }
        }
        if (earningsFound){
            return new TradeIntent("Earnings", "long", "close", 1, null, false, 0);
        } else {
            for (Trade trade : tradeLog.getActiveTradeList()) {
                //check for trend break first

                double openingCdf = trade.getOpenBar().getSignalcdf();
                double target = ((double) configurationTest.getLongExitTarget());
                double stopTarget = configurationTest.getStopLoss();
                if (bar.getBaseVolatility() < configurationTest.getLongExitVolatility1()) {
                    target = target * 0.20;
                    stopTarget = stopTarget*1.25;
                } else if (bar.getBaseVolatility() < configurationTest.getLongExitVolatility2()) {
                    target = target * 0.50;
                    stopTarget = stopTarget*1.5;
                } else if (bar.getBaseVolatility() < configurationTest.getLongExitVolatility3()) {
                    target = target * 0.75;
                    stopTarget = stopTarget *1.75;
                } else {
                    target = target * 1;
                    stopTarget = stopTarget * 2;
                }
                double profitTarget = (trade.getTradeBasis() * (1 + target));

                if((openingCdf < 0.5 && bar.getSignalcdf() < 0.25) || (trade.getOpenBar().getVolumecdf() < 0.5 && bar.getVolumecdf() < 0.25)) {
                    if (bar.getClose() > longTradeBasis) {
                        return new TradeIntent("Take Profit", "long", "close", 1, trade, false, 0);
                    }
                }else if((openingCdf > 0.5 && bar.getSignalcdf() > 0.75) || (trade.getOpenBar().getVolumecdf() > 0.5 && bar.getVolumecdf() > 0.75)){
                    if ( bar.getClose() > longTradeBasis) {
                        return new TradeIntent("Take Profit", "long", "close", 1, trade, false, 0);
                    }
                }
//                else if((openingCdf < 0.5 && bar.getSignalcdf() > 0.9) || (trade.getOpenBar().getVolcdf() < 0.5 && bar.getVolcdf() >0.9) ){
//                    if (bar.getClose() < longTradeBasis) {
//                        return new TradeIntent("Stop Loss", "long", "close", 1, trade, false, 0);
//                    }
//                }
//                if(openingCdf < 0.5 && bar.getSignalcdf()>0.75  && bar.getSignalroccdf() > 0.5 && bar.getClose()<longTradeBasis){
//                    return new TradeIntent("Stop Loss", "long", "close", 1, trade, false, 0);
//                }
//                if(openingCdf < 0.5) {
//                    if (bar.getSignalcdf() < openingCdf/2) {
//                        return new TradeIntent("Take Profit", "long", "close", 1, trade,false,0);
//                    }
//                }
//                else{
//                    if ((1-bar.getSignalcdf()) > (1-openingCdf)/2) {
//                        return new TradeIntent("Take Profit", "long", "close", 1, trade,false,0);
//                    }
//                }

//                if (bar.getClose() > profitTarget) {
//
//                    return new TradeIntent("Take Profit", "long", "close", 1, trade,false,0);
//                }
//                if(bar.getClose() < trade.getTradeBasis()*(1-stopTarget)){
//                    return new TradeIntent("Take Profit", "long", "close", 1, trade,false,0);
//                }
//                if(bar.getBaseLongVolatility() - trade.getOpeningLongVolatilitySignalLong() > 6){
//                    return new TradeIntent("Take Profit", "long", "close", 1, trade,false,0);
//
//                }
            }
        }
        return null;

    }
    public TradeIntent shortExitConditions(Bar bar, Bar priorBar, double percentageOfVolatility, double stopLossPercentage,
                                           ConfigurationTest configurationTest, TradeLog tradeLog, int shortConsecutiveTrendBreaks, List<Bar> bars, int z){

        boolean earningsFound = false;
        //LocalDate convertedDate = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long timestamp = bar.getDate().getTime();
        //LocalDate priorConvertedDate = priorBar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long priortimestamp = priorBar.getDate().getTime();
        if(priortimestamp + (3600000*24*15) < (timestamp)){
            return new TradeIntent("Trend Break", "long", "close", 1, null,true,0);
        }
        int bound = 3;
        if(z + 3 > bars.size()){
            bound = bars.size()-z-1;
        }
        for(int i = 1; i <=bound; i++){
            if(bars.get(z+i-1).isEarningsDate()){
                earningsFound = true;
                break;
            }
        }

        boolean crossBoolean;

        // if (!configurationTest.isSignalRocFlips()) {
        crossBoolean = (priorBar.getSignalRocLong() > 0 && bar.getSignalRocLong() < 0) ||
                // } else {
                (priorBar.getSignalRocLong() < 0 && bar.getSignalRocLong() > 0);
        //  }
        crossBoolean = false;
        if(crossBoolean || earningsFound){
            return new TradeIntent("Trend Break", "short", "close",1, null,false,0);
        }else{
            for(Trade trade : tradeLog.getActiveTradeList()){
                double openingCdf = trade.getOpenBar().getSignalcdf();
//                if(openingCdf < 0.5) {
//                    if (bar.getSignalcdf() < openingCdf/2) {
//                        return new TradeIntent("Take Profit", "long", "close", 1, trade, false, 0);
//                    }
//                }else{
//                    if (1-bar.getSignalcdf() > ((1-openingCdf)*2)) {
//                        return new TradeIntent("Take Profit", "long", "close", 1, trade, false, 0);
//                    }
//                }

                double target =((double)configurationTest.getShortExitTarget());
                double stopTarget = 0.08;
                if(bar.getBaseVolatility() > configurationTest.getShortExitVolatility1()){
                    target = target * 0.25;
                    stopTarget = stopTarget*1.25;
                }else if(bar.getBaseVolatility() > configurationTest.getShortExitVolatility2()){
                    target = target * 0.5;
                    stopTarget = stopTarget*1.5;
                }else if(bar.getBaseVolatility() > configurationTest.getShortExitVolatility3()){
                    target = target * 0.75;
                    stopTarget = stopTarget*1.75;
                }else{
                    target = target * 1;
                    stopTarget = stopTarget*2;
                }
                if((openingCdf < 0.5 && bar.getSignalcdf() < 0.3) || (trade.getOpenBar().getVolumecdf() < 0.5 && bar.getVolumecdf() < 0.3)) {
                    if (bar.getClose() < trade.getTradeBasis()) {
                        return new TradeIntent("Take Profit", "long", "close", 1, trade, false, 0);
                    }
                }else if((openingCdf > 0.5 && bar.getSignalcdf() > 0.70) || (trade.getOpenBar().getVolumecdf() > 0.5 && bar.getVolumecdf() > 0.7)){
                    if (bar.getSignalcdf() > 0.75 && bar.getClose() < trade.getTradeBasis()) {
                        return new TradeIntent("Take Profit", "long", "close", 1, trade, false, 0);
                    }
                }

            }
        }

        return null;
    }

    public void calculateDaysVolTriggerSlope(List<Bar> barList, int daysSlope, int secondSlopeDays, int volatilitySlopeDays, boolean isLong, ConfigurationTest configurationTest){

        for(int i = 0; i < barList.size(); i++) {
            int modDaySlope = daysSlope;
            int modSecondSlope = secondSlopeDays;
            if(barList.get(i).getMarketCap() < 2000000000){
                modDaySlope = modDaySlope;
            }else if(barList.get(i).getMarketCap() < 50000000000L){
                modDaySlope = modDaySlope;
            }else if(barList.get(i).getMarketCap() > 50000000000L){
                modDaySlope = (int) (modDaySlope*(configurationTest.getMovingTrendLength()[5]*0.01));
                modSecondSlope = (int) (modSecondSlope*(configurationTest.getMovingTrendLength()[5]*0.01));
            }
            if(i < barList.size() - modDaySlope){
                List<Long> dateLongs = new ArrayList<>();
                List<Double> oneMonthDoubles = new ArrayList<>();
                List<Double> treasuryDoubles = new ArrayList<>();
                List<Double> oilDoubles = new ArrayList<>();
                List<Double> commodityDoubles = new ArrayList<>();
                List<Double> closeDoubles = new ArrayList<>();
                for(int x = 1; x <= modDaySlope; x++){
                    oneMonthDoubles.add(barList.get(i + x).getDollarCorrelationFactor());
                    treasuryDoubles.add(barList.get(i + x).getTreasuryCorrelationFactor());
                    oilDoubles.add(barList.get(i + x).getOilCorrelationFactor());
                    commodityDoubles.add(barList.get(i + x).getCommodityIndexValue());
                    dateLongs.add(barList.get(i + x).getDate().getTime());
                    closeDoubles.add(barList.get(i + x).getClose());

                }
                SimpleRegression dollarRegression = new SimpleRegression();
                SimpleRegression treasuryRegression = new SimpleRegression();
                SimpleRegression oilRegression = new SimpleRegression();
                SimpleRegression comoddityRegression = new SimpleRegression();
                SimpleRegression closeRegression = new SimpleRegression();

                for(int z = 0; z <dateLongs.size(); z++){
                    dollarRegression.addData(dateLongs.get(z),oneMonthDoubles.get(z));
                    treasuryRegression.addData(dateLongs.get(z),treasuryDoubles.get(z));
                    oilRegression.addData(dateLongs.get(z),oilDoubles.get(z));
                    comoddityRegression.addData(dateLongs.get(z),commodityDoubles.get(z));
                    closeRegression.addData(dateLongs.get(z),closeDoubles.get(z));

                }

                barList.get(i).setPriceSlope(closeRegression.getSlope());
                barList.get(i).setTreasuryYieldSlope(treasuryRegression.getSlope());
                barList.get(i).setOilSlope(oilRegression.getSlope());
                barList.get(i).setCommoditySlope(comoddityRegression.getSlope());
                barList.get(i).setDollarSlope(dollarRegression.getSlope());
                if(isLong) {
                    barList.get(i).setSignalSlopeLong((float) (dollarRegression.getSlope() + treasuryRegression.getSlope()));
                }

            }
        }
        try{
        for(int i = 0; i < barList.size(); i++) {
            int modDaySlope = daysSlope;
            int modSecondSlope = secondSlopeDays;
            if (barList.get(i).getMarketCap() < 2000000000) {
                modDaySlope = modDaySlope;
            } else if (barList.get(i).getMarketCap() < 50000000000L) {
                modDaySlope = modDaySlope;
            } else if (barList.get(i).getMarketCap() > 50000000000L) {
                modDaySlope = modDaySlope * 2;
                modSecondSlope = (int) (modSecondSlope * (configurationTest.getMovingTrendLength()[5] * 0.01));
            }
            if (i < barList.size() - modSecondSlope) {
                SimpleRegression priceSlopeRegression = new SimpleRegression();
                SimpleRegression simpleRegression = new SimpleRegression();
                SimpleRegression treasuryRocRegression = new SimpleRegression();
                SimpleRegression dollarRocRegression = new SimpleRegression();
                SimpleRegression oilRocRegression = new SimpleRegression();
                SimpleRegression commodityRocRegression = new SimpleRegression();
                for (int x = 0; x <= modSecondSlope - 1; x++) {
                    if (isLong) {
                        simpleRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getSignalSlopeLong());
                        treasuryRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getTreasuryYieldSlope());
                        dollarRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getDollarSlope());
                        oilRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getOilSlope());
                        commodityRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getCommoditySlope());
                    } else {
                        //simpleRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getSignalSlopeShort());
                        treasuryRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getTreasuryYieldSlope());
                        dollarRocRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getDollarSlope());
                    }
                    if (isLong) {
                        priceSlopeRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getPriceSlope());
                    } else {
                        priceSlopeRegression.addData((modSecondSlope - x), barList.get(i + (modSecondSlope - x)).getPriceSlope());
                    }

                }
                barList.get(i).setTreasuryYieldSlopeRoc(treasuryRocRegression.getSlope());
                barList.get(i).setDollarSlopeRoc(dollarRocRegression.getSlope());
                barList.get(i).setCommodityRateOfChange(commodityRocRegression.getSlope());
                barList.get(i).setOilSlopeRoc(oilRocRegression.getSlope());
                if (isLong) {
                    barList.get(i).setPriceSlopeRoc(priceSlopeRegression.getSlope());
                    barList.get(i).setSignalRocLong((float) simpleRegression.getSlope());
                }
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }

        for(int i = 0; i < barList.size(); i++) {

            int modVolSlopeDays = volatilitySlopeDays;
            if(barList.get(i).getMarketCap() < 2000000000){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() < 50000000000L){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() > 50000000000L){
                modVolSlopeDays = (int) (modVolSlopeDays*(configurationTest.getMovingTrendLength()[5]*0.01));

            }
            if (i < barList.size() - modVolSlopeDays) {
                SimpleRegression volatilityRegression = new SimpleRegression();
                for (int x = 0; x <= modVolSlopeDays - 1; x++) {
                    if(barList.get(i + (modVolSlopeDays - x)).getBaseVolatility() != 0){
                        volatilityRegression.addData((modVolSlopeDays - x), barList.get(i + (modVolSlopeDays - x)).getBaseVolatility());
                    }
                }
                if(volatilityRegression.getN() == modVolSlopeDays) {
                    if (isLong) {
                        barList.get(i).setVolatilitySlopeLong(volatilityRegression.getSlope());
                    }
                }
            }
        }
        //ALTERNATIVELY TRYING DIFFERENT VARIABLE THAN VOLAILITITYSLOPEDAYS
        for(int i = 0; i < barList.size(); i++) {
            int modVolSlopeDays = volatilitySlopeDays;
            if(barList.get(i).getMarketCap() < 2000000000){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() < 50000000000L){
                modVolSlopeDays = modVolSlopeDays;
            }else if(barList.get(i).getMarketCap() > 50000000000L){
                modVolSlopeDays = (int) (modVolSlopeDays*(configurationTest.getMovingTrendLength()[5]*0.01));

            }
            if (i < barList.size() - modVolSlopeDays) {
                SimpleRegression volatilityRoCRegression = new SimpleRegression();
                for (int x = 0; x <= modVolSlopeDays - 1; x++) {
                    if(barList.get(i + (modVolSlopeDays - x)).getVolatilitySlopeLong() != 0) {
                        if (isLong) {
                            volatilityRoCRegression.addData((modVolSlopeDays - x), barList.get(i + (modVolSlopeDays - x)).getVolatilitySlopeLong());
                        }
                    }
                }
                if(isLong) {
                    Double test = volatilityRoCRegression.getSlope();
                    barList.get(i).setVolatilitySlopeRoCLong(test);
                }
            }
        }
    }

    public double[] convertListToArray(List<Double> originalList){
        double[] newArray = new double[originalList.size()];
        int size = originalList.size();
        for(int i = 0; i < size; i++){
            newArray[i] = originalList.get(i);
        }
        return newArray;
    }

}
