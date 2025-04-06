package Threads;

import BackTest.Trade;
import BackTest.TradeLog;
import BackTest.eExponentialRegression;
import Model.Bar;
import Model.ConfigurationTest;
import Model.IndividualStockTest;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import Model.Ticker;

public class ReplayThread extends Thread{

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_GREEN_NEW = "\u001B[32;1;4m";
    public static final String ANSI_GREEN_OUT = "\u001B[32;9;4m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RED_NEW = "\u001B[31;1;4m";
    public static final String ANSI_RED_OUT = "\u001B[31;9;4m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_ORANGE = "\033[38;5;214m";
    List<Bar> referenceBars;
    List<ConfigurationTest> configurationTests;
    ReplayThreadMonitor replayThreadMonitor;
    LocalDate endDate;
    LocalDate startDate;
    LocalDate interestBegin = LocalDate.of(2014,12,31);
    LocalDate interestEnd = LocalDate.of(2016,12,31);
    HashMap<Date, Double > dollarHashMap = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    HashMap<Ticker, List<Bar>> barMap = new HashMap<Ticker, List<Bar>>();
    int complete = 0;
    int threadNum;
    boolean print = true;

    public void setBarMap(HashMap<Ticker, List<Bar>> barMap) {
        this.barMap = barMap;
    }

    public boolean isPrint() {
        return print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public ReplayThread(LocalDate endDate, LocalDate startDate, List<Bar> referenceBars, ReplayThreadMonitor replayThreadMonitor, List<ConfigurationTest> configurationTests, int threadNum) {
        this.referenceBars = referenceBars;
        this.endDate = endDate;
        this.startDate = startDate;
        this.replayThreadMonitor = replayThreadMonitor;
        this.configurationTests = configurationTests;
        this.threadNum = threadNum;
    }
    @Override
    public void run() {
        long endTimeStamp = Date.from(endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
        for (ConfigurationTest configurationTest : configurationTests) {
            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                Iterator<Trade> tradeIterator = individualStockTest.getTradeLog().getClosedTradeList().iterator();
                while(tradeIterator.hasNext()) {
                    Trade trade = tradeIterator.next();
                    if (trade.getCloseBar() != null) {
                        if ((trade.getCloseBar().getDate().getTime() - trade.getOpenBar().getDate().getTime() ) / (1000 * 60 * 60 * 24) > 30) {
                            tradeIterator.remove();
                        }
                    }
                }
                tradeIterator = individualStockTest.getTradeLog().getActiveTradeList().iterator();
                while(tradeIterator.hasNext()) {
                    Trade trade = tradeIterator.next();
                    //if (trade.getCloseBar() != null) {
                    if ((endTimeStamp - trade.getOpenBar().getDate().getTime()) / (1000 * 60 * 60 * 24) > 30) {
                        tradeIterator.remove();
                    }
                    //  }
                }
            }

        }


        for (ConfigurationTest configurationTest : configurationTests) {
            //List<Double> dollarList = new ArrayList<>();
            DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
            int successfulTrades = 0;
            int failedTrades = 0;
            double dollars = 0;
            double deltaSum = 0;
            double totalSuccess = 0;
            double totalAccuracy = 0;
            double longDollars = 0;
            double shortDollars = 0;
            double daysHeld = 0;
            double staticDollars = 0;
            double successfulTrades1 = 0.0;
            double successfulTrades2 = 0.0;
            double successfulTrades3 = 0.0;
            double failedTrades1 = 0.0;
            double failedTrades2 = 0.0;
            double failedTrades3 = 0.0;
            double m2mDollars =0.0;
            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                longDollars += individualStockTest.getLongDollars();
                shortDollars += individualStockTest.getShortDollars();
                successfulTrades = successfulTrades + individualStockTest.getSuccessfulTrades();
                failedTrades = failedTrades + individualStockTest.getFailedTrades();
                dollars = dollars + individualStockTest.getDollars();
                descriptiveStatistics.addValue(individualStockTest.getDollars());
                staticDollars += individualStockTest.getStaticDollars();
                //dollarList.add(individualStockTest.getDollars());
                totalSuccess += individualStockTest.getSuccessRate();
                totalAccuracy += individualStockTest.getTrendAccuracy();
                successfulTrades1 += individualStockTest.getSuccessfulTrades1();
                successfulTrades2 += individualStockTest.getSuccessfulTrades2();
                successfulTrades3 += individualStockTest.getSuccessfulTrades3();
                failedTrades1 += individualStockTest.getFailedTrades1();
                failedTrades2 += individualStockTest.getFailedTrades2();
                failedTrades3 += individualStockTest.getFailedTrades3();
                for (Trade trade : individualStockTest.getTradeLog().getClosedTradeList()) {
                    if (trade.isLong()) {
                        deltaSum = deltaSum + Math.abs(((trade.getClosingPrice() - trade.getTradeBasis()) / trade.getTradeBasis()));
                    } else {
                        deltaSum = deltaSum + Math.abs(((trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis()));
                    }
                    daysHeld = daysHeld + trade.getDaysHeld();

                }

            }

            configurationTest.setAverageDollars(descriptiveStatistics.getPercentile(50));
            configurationTest.setTrendAccuracy(totalAccuracy / configurationTest.getStockTestList().size());
            configurationTest.setAverageDelta(deltaSum / (successfulTrades + failedTrades));
            configurationTest.setSuccessfulTrades(successfulTrades);
            configurationTest.setFailedTrades(failedTrades);
            configurationTest.setDollars(dollars);
            configurationTest.setSuccessRate((double) successfulTrades / (successfulTrades + failedTrades));
            configurationTest.setEqualWeightedSuccess((double) totalSuccess / configurationTest.getStockTestList().size());
            configurationTest.setLongDollars(longDollars*100);
            configurationTest.setShortDollars(shortDollars*100);
            configurationTest.setAverageTradingDaysHeld(daysHeld / (successfulTrades + failedTrades));
            configurationTest.setStaticDollars(staticDollars);
            configurationTest.setSuccessRate1(successfulTrades1/(successfulTrades1 + failedTrades1));
            configurationTest.setSuccessRate2(successfulTrades2/(successfulTrades2 + failedTrades2));
            configurationTest.setSuccessRate3(successfulTrades3/(successfulTrades3 + failedTrades3));
            configurationTest.setSuccessfulTrades1(successfulTrades1);
            configurationTest.setSuccessfulTrades2(successfulTrades2);
            configurationTest.setSuccessfulTrades3(successfulTrades3);
        }

        for (ConfigurationTest configurationTest : configurationTests) {
            int badCount = 0;
            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                if (individualStockTest.getDollars() < 10000) {
                    badCount++;
                    if (individualStockTest.getDollars() - 10000 < configurationTest.getDollarsBelowThreshold()) {
                        configurationTest.setDollarsBelowThreshold(individualStockTest.getDollars() - 10000);
                    }
                }
            }
            configurationTest.setPercentPositiveTests(((double) configurationTest.getStockTestList().size() - badCount) / configurationTest.getStockTestList().size());
        }

        for (ConfigurationTest configurationTest : configurationTests) {
            double shortAttempts = 0.0;
            double shortSuccess = 0.0;
            double shortPossibleProfitable = 0.0;
            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                shortPossibleProfitable = shortPossibleProfitable + individualStockTest.getShortPossibleProfitable();
                for (Trade trade : individualStockTest.getTradeLog().getClosedTradeList()) {
                    if (!trade.isLong()) {
                        shortAttempts++;
                        if (trade.getClosingPrice() < trade.getTradeBasis()) {
                            shortSuccess++;
                        }
                    }

                }
            }
            configurationTest.setShortPossibleProfitable((shortPossibleProfitable) / (shortAttempts));
            configurationTest.setShortPct(shortSuccess / shortAttempts);
        }
//
//        for (ConfigurationTest configurationTest : configurationTests) {
//            double longAttempts = 0.0;
//            double longSuccess = 0.0;
//            double longPossibleProfitable = 0.0;
//            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
//                longPossibleProfitable = longPossibleProfitable + individualStockTest.getLongPossibleProfitable();
//                for (Trade trade : individualStockTest.getTradeLog().getClosedTradeList()) {
//                    if (trade.isLong()) {
//                        longAttempts++;
//                        if (trade.getClosingPrice() < trade.getTradeBasis()) {
//                            longSuccess++;
//                        }
//                    }
//
//                }
//            }
//            configurationTest.setLongPossibleProfitable((longPossibleProfitable) / (longAttempts));
//        }
        for (ConfigurationTest configurationTest : configurationTests) {
            System.out.println(configurationTest.getSuccessRate() + "\t" + configurationTest.getTrendAccuracy() + "\t" + configurationTest.getDollars() + "\t" +
                    configurationTest.getAverageDollars() + "\t" + configurationTest.getPercentPositiveTests() +
                    "\t" + configurationTest.getVolumeWeighting() + "\t" + configurationTest.getIvWeighting() + "\t" + configurationTest.getRealizedVolWeighting()
                    + "\t" + configurationTest.getDollarWeighting() + "\t" + configurationTest.getTreasuryWeighting() + "\t" + configurationTest.getFedTotalAssetsWeighting() +
                    "\t" + configurationTest.getRevenueRocWeighting() + "\t" + configurationTest.getCommodityWeighting() + "\t" + configurationTest.getDiscountWeighting() +
                    "\t" + configurationTest.getMovingTrendLength()[0] + "\t" + configurationTest.getCorrelationDays()
            );
        }
        //System.out.println(individualStockTests);

        for (ConfigurationTest configurationTest : configurationTests) {
            // if(configurationTest.getLongDollars() + configurationTest.getShortDollars() > 0) {
            List<Trade> allClosedTrades = new ArrayList<>();
            List<Trade> allOpenTrades = new ArrayList<>();
            //Assemble all trades
            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                TradeLog tradeLog = individualStockTest.getTradeLog();
                allClosedTrades.addAll(tradeLog.getClosedTradeList());
            }
            for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                TradeLog tradeLog = individualStockTest.getTradeLog();
                allOpenTrades.addAll(tradeLog.getActiveTradeList());
            }
            List<Long> dateLongs = new ArrayList<>();
            List<Double> dollarValues = new ArrayList<>();
            List<Double> shortDollarValues = new ArrayList<>();
            DecimalFormat decimalFormat = new DecimalFormat("#.####");
            double dollars = 10000;
            double shortDollars = 10000;
            double interestDollars = 10000;
            double netLongDollars = 0;
            double netShortDollars = 0;
            List<Trade> allTrades = new ArrayList<>();
            allTrades.addAll(allClosedTrades);
            allTrades.addAll(allOpenTrades);
            List<Double> capitalInUse = new ArrayList<>();
            int shortSuccess = 0;
            int longSuccess = 0;
            int totalShort = 0;
            int totalLong = 0;
            int specificLongSuccess = 0;
            int specificShortSuccess = 0;
            int specificLongTotal = 0;
            int specificShortTotal = 0;
            double[] totals = new double[43];
            double[] success = new double[43];
            Bar prevBar = null;
            for (Bar bar : referenceBars) {
                long barTime = bar.getDate().getTime();
                double netExposure = 0;

                StringBuilder holdingsString = new StringBuilder();
                double totalCapitalInUse = 0.0;
                double totalTradesOn = 0;
                List<Trade> tradesToCalculate = new ArrayList<>();
                for (Trade trade : allTrades) {
                    long tradeTime = trade.getOpenDate().getTime();
                    //long openTradeTime = trade.getOpenBar().getDate().getTime();

                    long closeTradeTime = 0;
                    long trimTradeTime = 0;
                    if (trade.getCloseBar() != null) {
                        closeTradeTime = trade.getCloseBar().getDate().getTime();
                    }
                    if (trade.getTrimBar() != null) {
                        trimTradeTime = trade.getTrimBar().getDate().getTime();
                    }
                    boolean isOpenDate = tradeTime == barTime;
                    boolean isCloseDate = closeTradeTime == barTime;
                    boolean isTrimDate = trimTradeTime == barTime;
                    boolean toBeAdded = false;
                    if(trade.getTradeBasis() != 0 && trade.getPercentage() < 10) {
                        if (trade.getCloseDate() != null) {
                            if ((isOpenDate
                                    || barTime > tradeTime) && (isCloseDate || bar.getDate().before(trade.getCloseDate()) &&
                                    (isTrimDate || bar.getDate().before(trade.getCloseDate())
                                    ))) {
                                toBeAdded = true;
                                tradesToCalculate.add(trade);
                            }
                        } else {
                            if (isOpenDate
                                    || barTime > tradeTime) {
                                toBeAdded = true;
                                tradesToCalculate.add(trade);
                            }
                        }
                    }
                }
                double preExistingCapitalUse = 0;
                //find pre-existing trades
                for (Trade trade : tradesToCalculate) {
                    if (trade.getOpenDate().before(bar.getDate())) {
                        preExistingCapitalUse = preExistingCapitalUse + (trade.getPositionSize());
                    }
                }
                //    double remainingCapitalToUse = 1 - preExistingCapitalUse;


//                        if(raiseSize){
//                            for(Trade trade:tradesToCalculate){
//                                trade.setPositionSize(trade.getPositionSize()*1.1);
//                            }
//                        }
//                        sum = 0;
//                        for (Trade trade : tradesToCalculate) {
//                            if(trade.getCloseBar() !=null) {
//                                if (bar.getDate().before(trade.getCloseBar().getDate())) {
//                                    sum = sum + trade.getPositionSize();
//                                }
//                            }
//                            if(trade.isLong()){
//                                netExposure = netExposure + trade.getPositionSize();
//                            }else{
//                                netExposure = netExposure - trade.getPositionSize();
//                            }
//                        }
                double sum = 0;
                for (Trade trade : tradesToCalculate) {
                    if(trade.getCloseBar() !=null ) {
                        if (bar.getDate().before(trade.getCloseBar().getDate()) || bar.getDate().equals(trade.getCloseBar().getDate())) {
                            sum = sum + trade.getPositionSize();
                        }
                    }else if(trade.getClosingPrice() == 0.0){
                        sum = sum + trade.getPositionSize();
                    }
                    if(trade.isLong()){
                        netExposure = netExposure + trade.getPositionSize();
                    }else{
                        netExposure = netExposure - trade.getPositionSize();
                    }
                }

                boolean raiseSize = sum < 0.75;
                boolean shrinkSize = sum > 4;
                if(raiseSize){
                    for (Trade trade : tradesToCalculate) {
                        long openTradeTime = trade.getOpenBar().getDate().getTime();
                        if(barTime == openTradeTime){
                            //                                   System.out.println("before: " + trade.getPositionSize());
                            trade.setHalf(true);
                            trade.setPositionSize(trade.getPositionSize()*1.2);
//                                     System.out.println("after: " + trade.getPositionSize());
                        }
                    }
                }
                if(shrinkSize){
                    for (Trade trade : tradesToCalculate) {
                        long openTradeTime = trade.getOpenBar().getDate().getTime();
                        if(barTime == openTradeTime){
                            //                                   System.out.println("before: " + trade.getPositionSize());
                            trade.setHalf(true);
                            if(trade.isLong()) {
                                trade.setPositionSize(trade.getPositionSize()*0.9);
                            }else{
                                trade.setPositionSize(trade.getPositionSize()*0.9);
                            }
//                                     System.out.println("after: " + trade.getPositionSize());
                        }
                    }
                }
                sum = 0;
                for (Trade trade : tradesToCalculate) {
                    if(trade.getCloseBar() !=null ) {
                        if (bar.getDate().before(trade.getCloseBar().getDate())) {
                            sum = sum + trade.getPositionSize();
                        }
                    }else if(trade.getClosingPrice() == 0.0){
                        sum = sum + trade.getPositionSize();
                    }
                    if(trade.isLong()){
                        netExposure = netExposure + trade.getPositionSize();
                    }else{
                        netExposure = netExposure - trade.getPositionSize();
                    }
                }
                totalCapitalInUse = (sum);

                tradesToCalculate.sort(Comparator.comparing(t -> t.getTicker().getTicker()));

                for (Trade trade : tradesToCalculate) {

                    if (trade.getPositionSize() != 0) {
                        double size = trade.getPositionSize();
//                                if(raiseSize){
//                                    size = size*1.1;
//                                }
                        long openTradeTime = trade.getOpenBar().getDate().getTime();
                        long closeTradeTime = 0;
                        if (trade.getCloseBar() != null) {
                            closeTradeTime = trade.getCloseBar().getDate().getTime();
                        }
                        String ticker = trade.getTicker().getTicker();
//                                if(trade.isHalf()){
//                                    //int trimDateHashCode = trade.getTrimBar().hashCode();
//                                    if(trade.getTrimDate().after(bar.getDate()) || trade.getTrimDate().equals(bar.getDate())){
//                                        ticker = ticker + "[h]";
//                                    }
//                                }
                        holdingsString.append(",");
                        if (trade.isLong()) {
                            if (barTime == openTradeTime) {
                                holdingsString.append(ANSI_GREEN_NEW).append(ticker).append("(").append(decimalFormat.format(size)).append(")").append(ANSI_RESET);
                            } else if (barTime == closeTradeTime) {
                                holdingsString.append(ANSI_GREEN_OUT).append(ticker).append("(").append(decimalFormat.format(size)).append("/").append(decimalFormat.format(trade.getPercentage())).append(ANSI_RESET);

                            } else {
                                holdingsString.append(ANSI_GREEN).append(ticker).append("(").append(decimalFormat.format(size)).append(")").append(ANSI_RESET);
                            }
                        } else {
                            if (barTime == openTradeTime) {
                                holdingsString.append(ANSI_RED_NEW).append(trade.getTicker().getTicker()).append("(").append(decimalFormat.format(size)).append(")").append("/").append(ANSI_RESET);
                            } else if (barTime == closeTradeTime) {
                                holdingsString.append(ANSI_RED_OUT).append(trade.getTicker().getTicker()).append("(").append(decimalFormat.format(size)).append("/").append(decimalFormat.format(trade.getPercentage())).append(ANSI_RESET);
                            } else {
                                holdingsString.append(ANSI_RED).append(trade.getTicker().getTicker()).append("(").append(decimalFormat.format(size)).append(")").append(ANSI_RESET);
                            }
                        }
                        if(barTime != closeTradeTime) {
                            try {
                                List<Bar> barList = barMap.get(trade.getTicker());
                                Bar matchingBar = null;
                                Bar previousBar = null;
                                // for (Bar bar1 : barList) {
                                for (int i = 0; i < barList.size(); i++) {
                                    if (barList.get(i).getDate().getTime() == barTime) {
                                        previousBar = barList.get(i -1);
                                        matchingBar = barList.get(i);
                                    }
                                }

                                //  }
                                double dollarChange;
                                double shortChange;
                                if(matchingBar != null) {
                                    if (trade.isLong()) {

                                        double testDollars = dollarHashMap.get(prevBar.getDate());
                                        dollarChange = ((testDollars * size) * (((matchingBar.getClose() - previousBar.getClose()) / previousBar.getClose())));
                                        dollars += dollarChange;
                                    }else{
                                        double testDollars = dollarHashMap.get(prevBar.getDate());
                                        dollarChange = ((testDollars * size) * (((previousBar.getClose() - matchingBar.getClose()) / previousBar.getClose())));
                                        dollars += dollarChange;
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
//                                    Bar matchingBar = null;
//                                    for (Bar bar1 : barList) {
//                                        if (bar1.getDate().getTime() == barTime) {
//                                            matchingBar = bar1;
//                                        }
//                                    }
//                                    double dollarChange;
//                                    double shortChange;
//                                    if(matchingBar != null) {
//                                        if (trade.isLong()) {
//
//                                            double testDollars = dollarHashMap.get(trade.getOpenBar().getDate());
//                                            dollarChange = ((testDollars * size) * (((matchingBar.getClose() - trade.getTradeBasis()) / trade.getTradeBasis())));
//                                            dollars += dollarChange;
//                                        } else {
//                                            double testDollars = dollarHashMap.get(trade.getOpenBar().getDate());
//                                            dollarChange = ((testDollars * size) * (((trade.getTradeBasis() - matchingBar.getClose()) / trade.getTradeBasis())));
//                                            //  shortChange = ((shortDollars * size ) * (((trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis())));
//                                            dollars += dollarChange;
//                                            // shortDollars +=shortChange;
//                                            // netShortDollars = netShortDollars + dollarChange;
////                                        if(converted.isAfter(interestBegin) && converted.isBefore(interestEnd)){
////                                            interestDollars += ((interestDollars * tradeSize * trade.getTradeConfidence()) * (((trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis())));
////                                        }
//                                        }
//                                    }
                        }else if (barTime == closeTradeTime) {

                            //double tradeSize = trade.getPositionSize();
                            double dollarChange;
                            double shortChange;
                            //LocalDate converted = bar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (trade.isLong()) {
                                double testDollars = dollarHashMap.get(trade.getOpenBar().getDate());
                                dollarChange = ((testDollars * size ) * (((trade.getClosingPrice() - trade.getTradeBasis()) / trade.getTradeBasis())));
                                dollars += dollarChange;
                                netLongDollars = netLongDollars + dollarChange;
//                                        if(converted.isAfter(interestBegin) && converted.isBefore(interestEnd)){
//                                            interestDollars += ((interestDollars * tradeSize * trade.getTradeConfidence()) * (((trade.getClosingPrice() - trade.getTradeBasis()) / trade.getTradeBasis())));
//                                        }
                            } else {
                                double testDollars = dollarHashMap.get(trade.getOpenBar().getDate());
                                dollarChange = ((testDollars * size) * (((trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis())));
                                shortChange = ((shortDollars * size ) * (((trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis())));
                                dollars += dollarChange;
                                shortDollars +=shortChange;
                                netShortDollars = netShortDollars + dollarChange;
//                                        if(converted.isAfter(interestBegin) && converted.isBefore(interestEnd)){
//                                            interestDollars += ((interestDollars * tradeSize * trade.getTradeConfidence()) * (((trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis())));
//                                        }
                            }

                            if (trade.isLong()) {
                                if (trade.getTradeBasis() < trade.getClosingPrice()) {
                                    longSuccess++;
                                    if(trade.getTradeCategory() != 0){
                                        success[trade.getTradeCategory()] = success[trade.getTradeCategory()]+1;
                                    }
                                    if(trade.isHalf()){
                                        specificLongSuccess++;
                                    }
                                }
                                if(trade.isHalf()){
                                    specificLongTotal++;
                                }
                                totalLong++;
                                if(trade.getTradeCategory() != 0){
                                    totals[trade.getTradeCategory()] = totals[trade.getTradeCategory()]+1;
                                }
                            } else {
                                if (trade.getTradeBasis() > trade.getClosingPrice()) {
                                    shortSuccess++;
                                    if(trade.getTradeCategory() != 0){
                                        success[trade.getTradeCategory()] = success[trade.getTradeCategory()]+1;
                                    }
                                    if(trade.isHalf()){
                                        specificShortSuccess++;
                                    }
                                }
                                if(trade.isHalf()){
                                    specificShortTotal++;
                                }

                                totalShort++;
                                if(trade.getTradeCategory() != 0){
                                    totals[trade.getTradeCategory()] = totals[trade.getTradeCategory()]+1;
                                }
                            }
                        }
                    }

                }

                //System.out.println(i + "/" + configurationTests.size());
                dateLongs.add(bar.getDate().getTime());
                dollarValues.add(dollars);
                dollarHashMap.put(bar.getDate(),dollars);
                shortDollarValues.add(shortDollars);
                if (configurationTests.size() == 1 && print) {

                    System.out.println(sdf.format(bar.getDate()) + "\t" +
                            dollars + "\t" + netLongDollars + "\t" + netShortDollars + "\t" +
                            totalCapitalInUse + "\t" + netExposure + "\t" + totalTradesOn + "\t" + holdingsString
                    );


                }
                capitalInUse.add(totalCapitalInUse);

                prevBar = bar;

            }

            double total = 0;
            for (Double d : capitalInUse) {
                total += d;
            }
            configurationTest.setReplayDollars(dollars);
            configurationTest.setAverageReturnPerTradingDayHeld(((configurationTest.getStaticDollars())) /
                    (configurationTest.getAverageTradingDaysHeld() * (configurationTest.getSuccessfulTrades() + configurationTest.getFailedTrades())));
            configurationTest.setAverageCapitalInUse(total / (referenceBars.size() - 80));

            if (configurationTests.size() == 1 && print) {
                System.out.println("OpenDate\tCloseDate\tTicker\tSignalValueLong\tSignalRocLong\tsignalPercentile\tVolatilitySlopeLong\tSignalValueShort\tSignalRocShort\tVolatilitySlopeShort\tPriceSlope\tPriceSlopeRoc\tVolatilityRocShort\t15dPerf\t30dPerf\tChange");
                for (Trade trade : allTrades) {
                    if(trade.getPositionSize() != 0) {
//                        double change = 0;
//                        if (trade.isLong()) {
//                            change = (trade.getClosingPrice() - trade.getTradeBasis()) / trade.getTradeBasis();
//                        } else {
//                            change = (trade.getTradeBasis() - trade.getClosingPrice()) / trade.getTradeBasis();
//                        }
                        //                       if(trade.getCloseDate() != null) {
//                            System.out.println(sdf.format(trade.getOpenDate()) + "\t" +
//                                    sdf.format(trade.getCloseDate()) + "\t"
//                                    + trade.isLong() + "\t"
//                                    + trade.getTicker().getTicker() + "\t" +
//                                    +trade.getOpeningSignalValueLong() + "\t" +
//                                    trade.getOpeningSignalRoCValueLong() + "\t" +
//                                    trade.getOpeningSignalPercentile() + "\t" +
//                                    trade.getOpeningVolatilitySignalLong() + "\t" +
//                                    + trade.getOpeningSignalValueShort() + "\t" +
//                                    trade.getOpeningSignalRoCValueShort() + "\t" +
//                                    trade.getOpeningVolatilitySignalShort() + "\t" +
//                                    trade.getOpeningPriceSlope() + "\t" +
//                                    trade.getOpeningPriceSlopeRoC() + "\t" +
//                                    trade.getOpeningVolatilitySlopeRoC() + "\t" +
//                                    trade.getThirtyDayForwardPerformance() + "\t" +
//                                    trade.getFifteenDayForwardPerformance() + "\t" +
//                                    +change);
//                        }
                    }
                }
            }
            SimpleRegression simpleRegression = new SimpleRegression();
            SimpleRegression shortRegression = new SimpleRegression();
            for (int i = 0; i < dateLongs.size(); i++) {
                simpleRegression.addData(dateLongs.get(i), dollarValues.get(i));
                shortRegression.addData(dateLongs.get(i), shortDollarValues.get(i));
            }
            double x = simpleRegression.getRSquare();
            //configurationTest.setReturnRsquared(x);
            List<Double> converted = new ArrayList<>();
            for(Long l : dateLongs){
                converted.add(l.doubleValue());
            }
            eExponentialRegression reg = new eExponentialRegression((ArrayList<Double>) converted, (ArrayList<Double>) dollarValues);
            eExponentialRegression shortReg = new eExponentialRegression((ArrayList<Double>) converted, (ArrayList<Double>) shortDollarValues);
            double r2 = reg.getR2();
            double r2x = shortReg.getR2();
            configurationTest.setReturnRsquared(r2);
            configurationTest.setShortRsquared(r2x);
            configurationTest.setExpectedDollars(configurationTest.getAverageReturnPerTradingDayHeld()*configurationTest.getReturnRsquared());
            double[] categorySuccesses = new double[43];
            double[] categoryCounts = new double[43];
            if( print){
//                for(int i = 0; i < totals.length; i++){
//                    System.out.println("category " + i + ": " + success[i] + "/" + totals[i] + " == " + ((double)success[i]/totals[i]));
//                    categorySuccesses[i] = success[i]/totals[i];
//                    categoryCounts[i] =totals[i];
//                }
                configurationTest.setCategorySuccesses(categorySuccesses);
                configurationTest.setCategoryCounts(categoryCounts);

                System.out.println("SpecificShortPct: "+ ((double)specificShortSuccess/specificShortTotal) + "("+ specificShortTotal+")" + " short %: " + ((double) shortSuccess / totalShort) +
                        "SpecificLongPct: "+ ((double)specificLongSuccess/specificLongTotal) + "("+ specificLongTotal+")" +" long %: " + ((double) longSuccess / totalLong) +
                        "counts: L(" + totalLong + ") S(" + totalShort + ") [" + complete + "/" + configurationTests.size() + "]" );
            }
            //      }
            complete++;
        }

        replayThreadMonitor.threadFinished(threadNum, configurationTests);
    }
}
