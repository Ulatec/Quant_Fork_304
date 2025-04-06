package Fetchers;

import BackTest.*;
import Model.*;
import Repository.DatabaseBarRepository;
import Repository.ImpliedVolailityRepository;
import Repository.RealizedVolatilityRepository;
import Threads.*;
import Util.DateFormatter;
import Util.ListSplitter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class StockRangeTester implements ApplicationRunner {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_GREEN_NEW = "\u001B[32;1;4m";
    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_ORANGE = "\033[38;5;214m";
    Instant now;
    Instant start;
    double delta;
    double rate;
    boolean dollarInput = true;
    boolean multiTicker = false;

    boolean multithreadedNonVerbose = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ImpliedVolailityRepository impliedVolailityRepository;
    @Autowired
    public RealizedVolatilityRepository realizedVolatilityRepository;
    @Autowired
    public DatabaseBarRepository databaseBarRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        boolean[] directions = new boolean[]{false};
        boolean fullDateRange = true;
        LocalDate startDate = LocalDate.of(2020,10,8);
        LocalDate endDate = LocalDate.now();
//
        LocalDate priorEndDate = LocalDate.of(2024,12,20);
        LocalDate fiveDaysBack = priorEndDate.minusDays(5);
        LocalDate priorStartDate = LocalDate.of(2007,9,9);
        LocalDate barRequestStart = LocalDate.of(2007,9,9);
        int threads = 16;
        boolean update = false;
        boolean ohShit = false;
        boolean fullLog = false;
        boolean multiTickerMode = true;
        boolean logBuckets = true;
        double[] dollarsPercentToTest = {0.01};
        int dayVolToTest = 30;
        double[] rangeThresholdToTest = {0.9,};
        boolean[] priceRocFlips = {false};
        boolean[] priceSlopeFlips = {false};
        boolean[] volatilityRocFlips = {false};
        boolean[] volatilitySlopeFlips = {false,};
        boolean[] signalRocFlips = {false};
        boolean[] signalValueFlips = {true};
        boolean[] volumeFlip = {true};
        boolean[] signalRocEnable = {true};
        boolean[] signalValueEnable= {true};
        boolean[] volatilityRocEnable= {false,};
        boolean[] volatilitySlopeEnable= {true};
        boolean[] vixRocEnable= {false};
        boolean[] oilEnable= {false};
        boolean[] treasuryEnable= {true};
        boolean[] commodityEnable= {false};
        double[] percentOfVolProfit = {1};
        double[] ivWeighting = {-6e-11};
        double[] discountWeighting = {-2e-11};
        double[] realizedVolWeighting = {-0.8};
        double[] volumeWeighting = {0.7};
        double[] treasuryWeighting = {11};
        double[] commodityWeighting = {30};
        double[] fedTotalAssetsWeighting = {-1e-9};
        double[] revenueRocWeighting = {3};
        double[] dollarWeighting = {10};
        double[] vixWeighting = {87};
        double[] oilWeighting = {7};
        double[] longOpenSignalValueThreshold = {0.9};
        double[] longOpenSignalRocThreshold = {0.9};
        double[] longOpenVolatilitySlopeThreshold = {0.8};
        double[] longOpenVolatilityRocThreshold = {0.8};
        double[] shortOpenSignalValueThreshold = {0.2};
        double[] shortOpenSignalRocThreshold = {0.2};
        double[] shortOpenVolatilitySlopeThreshold = {0.3};
        double[] shortOpenVolatilityRocThreshold = {0.1};
        double[] shortExitTarget = {0.004};
        double[] shortExitVolatility1 = {25};
        double[] shortExitVolatility2 ={48};
        double[] shortExitVolatility3 = {68};
        double[] longExitTarget = {0.019};
        double[] longExitVolatility1 = {21};
        double[] longExitVolatility2 = {45};
        double[] longExitVolatility3 = {63};
        int[] correlationDays ={9};
        int[] ivTypes = {1};
        int[] ivTrendType = {1};
        double[] stopLoss = {0.12};
        int[] trendLengthToTest1 = {60};
        int[] trendLengthToTest2 = {3};
        int[] trendLengthToTest3 = {13};
        int[] trendLengthToTest4 = {36};
        int[] trendLengthToTest5 = {53};
        int[] trendLengthToTest6 = {300};
        int[] trendLengthToTest7 = {68000};
        int[] trendLengthToTest8 = {500};
        int[] trendLengthToTest = {110};
        int[] tradeLengthToTest = {35};
        int[] trendConfirmationLengthToTest = {2};
        int[] trendBreakConfirmationToTest = {85};
        int[] volLookback = {63};
        boolean useVolatilitySurface = true;


        double initialLongOpenVolatilitySlope = 0;
        double incrementerLongOpenVolatilitySlope = 0.05;
        int longOpenVolatilitySlopeParamCount = 12;
        double[] workingArray = new double[2];
        double[] incrementerArray = new double[longOpenVolatilitySlopeParamCount];
        boolean[] priorFlipArray = new boolean[longOpenVolatilitySlopeParamCount];
       // boolean[] directionArray = new boolean[longOpenVolatilitySlopeParamCount];
        boolean[] reversedFlagArray = new boolean[longOpenVolatilitySlopeParamCount];
        boolean[] priorSuccessArray = new boolean[longOpenVolatilitySlopeParamCount];

        double[] volSlopeValues = new double[6];
        volSlopeValues[0] = -0.12;
        for(int j = 1; j <volSlopeValues.length ; j++){
            volSlopeValues[j] = volSlopeValues[j-1] + 0.04;
        }

        ArrayList<double[]> workingArrays = new ArrayList<>();
        workingArrays.add(new double[]{});
       // for(int j = 0; j < workingArray.length; j++) {
            for (double volSlope1 : volSlopeValues) {
                for (double volSlope2 : volSlopeValues) {
                    for (double volSlope3 : volSlopeValues) {
                        //for(double volSlope4 : volSlopeValues) {
                           // for(double volSlope5 : volSlopeValues) {
          //                        workingArrays.add(new double[]{volSlope1, volSlope2,volSlope3});
                         //   }
                       // }
                    }
                }
            }
       // }
        ArrayList<boolean[]> directionArrays = new ArrayList<>();
        // workingArrays.add(new double[]{});
        // for(int j = 0; j < workingArray.length; j++) {
        for (boolean direction: directions) {
            //for (double volSlope2 : volSlopeValues) {
            directionArrays.add(new boolean[]{direction});
            // }
        }
        //HashMap<String, ConfigurationTest> settingsMap = getAllSettings();

        ArrayList<int[]> trendLengths = new ArrayList<>();
        for (int movingTrendLength : trendLengthToTest1) {
            for (int movingTrendLength2 : trendLengthToTest2) {
                for (int movingTrendLength3 : trendLengthToTest3) {
                    for (int movingTrendLength4 : trendLengthToTest4) {
                        for (int movingTrendLength5 : trendLengthToTest5) {
                            for (int movingTrendLength6 : trendLengthToTest6) {
                                for (int movingTrendLength7 : trendLengthToTest7) {
                                    for (int movingTrendLength8 : trendLengthToTest8) {
                                        trendLengths.add(new int[]{movingTrendLength, movingTrendLength2,
                                                movingTrendLength3, movingTrendLength4, movingTrendLength5,
                                                movingTrendLength6, movingTrendLength7, movingTrendLength8});
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        List<ConfigurationTest> configurationTestList = new ArrayList<>();
        for (int trendLength : trendLengthToTest) {
            for (int tradeLength : tradeLengthToTest) {
                for (int trendConfirm : trendConfirmationLengthToTest) {
                    for (int trendBreak : trendBreakConfirmationToTest) {
                        for (double rangeThreshold : rangeThresholdToTest) {
                            for (int ivType : ivTypes) {
                                for (boolean priceRocFlip : priceRocFlips) {
                                    for (boolean volatilityRocFlip : volatilityRocFlips) {
                                        for (boolean volatilitySlopeFlip : volatilitySlopeFlips) {
                                            for (int IVTrendCalc : ivTrendType) {
                                                for (double stopLoss1 : stopLoss) {
                                                    for (double pctOfVol : percentOfVolProfit) {
                                                        for (boolean signalRocFlip : signalRocFlips) {
                                                            for (double ivWeight : ivWeighting) {
                                                                for (double discount : discountWeighting) {
                                                                    for (double volume : volumeWeighting) {
                                                                        for (double realized : realizedVolWeighting) {
                                                                            for (int[] movingTrendLengths : trendLengths) {
                                                                                for (int vollLookback : volLookback) {
                                                                                    for (double dollarPercent : dollarsPercentToTest) {
                                                                                        for (double treasury : treasuryWeighting) {
                                                                                            for(double commodity : commodityWeighting) {
                                                                                                for(double fedAssets : fedTotalAssetsWeighting) {
                                                                                                    for(double revenue : revenueRocWeighting) {
                                                                                                        for(double dollar : dollarWeighting) {
                                                                                                            for(int correlation : correlationDays) {
                                                                                                                for(double oil : oilWeighting) {
                                                                                                                    for(double vix : vixWeighting) {
                                                                                                                        for(double longOpenSignalValueThresholdValue: longOpenSignalValueThreshold) {
                                                                                                                            for(double longOpenSignalRocThresholdValue : longOpenSignalRocThreshold) {
                                                                                                                                for(double longOpenVolatilitySlopeThresholdValue : longOpenVolatilitySlopeThreshold) {
                                                                                                                                    for(double shortOpenSignalValueThresholdValue : shortOpenSignalValueThreshold) {
                                                                                                                                        for(double shortOpenSignalRocThresholdValue : shortOpenSignalRocThreshold) {
                                                                                                                                            for(double shortOpenVolatilitySlopeThresholdValue : shortOpenVolatilitySlopeThreshold) {
                                                                                                                                                for(double shortExitTargetValue : shortExitTarget) {
                                                                                                                                                    for(double shortExitVolatility1Value : shortExitVolatility1) {
                                                                                                                                                        for(double shortExitVolatility2Value : shortExitVolatility2) {
                                                                                                                                                            for(double shortExitVolatility3Value : shortExitVolatility3) {
                                                                                                                                                                for(double longExitTargetValue : longExitTarget) {
                                                                                                                                                                    for(double longExitVolatility1Value : longExitVolatility1) {
                                                                                                                                                                        for(double longExitVolatility2Value : longExitVolatility2) {
                                                                                                                                                                            for (double longExitVolatility3Value : longExitVolatility3) {
                                                                                                                                                                                for(boolean signalValueFlip : signalValueFlips) {
                                                                                                                                                                                    for (boolean priceSlopeFlip : priceSlopeFlips) {
                                                                                                                                                                                        for(boolean signalRoc : signalRocEnable) {
                                                                                                                                                                                            for (boolean signalValue : signalValueEnable) {
                                                                                                                                                                                                for(boolean volatilityRoc : volatilityRocEnable) {
                                                                                                                                                                                                    for(boolean volatilitySlope : volatilitySlopeEnable) {
                                                                                                                                                                                                        for(boolean vixRoc : vixRocEnable) {
                                                                                                                                                                                                            for(boolean oilE : oilEnable) {
                                                                                                                                                                                                                for(boolean treasuryE : treasuryEnable) {
                                                                                                                                                                                                                    for(boolean commodityE : commodityEnable) {
                                                                                                                                                                                                                        for(double longVolRoc : longOpenVolatilityRocThreshold) {
                                                                                                                                                                                                                            for(double shortVolRoc : shortOpenVolatilityRocThreshold) {
                                                                                                                                                                                                                                for(double[] workingArry : workingArrays) {
                                                                                                                                                                                                                                    for(boolean[] directionArray : directionArrays) {
                                                                                                                                                                                                                                        for(boolean volumeFlips : volumeFlip) {
                                                                                                                                                                                                                                            ConfigurationTest configurationTest = new ConfigurationTest();
                                                                                                                                                                                                                                            configurationTest.setLongOpenSignalValueThreshold(longOpenSignalValueThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setLongOpenSignalRocThreshold(longOpenSignalRocThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setLongOpenVolatilitySlopeThreshold(longOpenVolatilitySlopeThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortOpenSignalValueThreshold(shortOpenSignalValueThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortOpenSignalRocThreshold(shortOpenSignalRocThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortOpenVolatilitySlopeThreshold(shortOpenVolatilitySlopeThresholdValue);
                                                                                                                                                                                                                                            configurationTest.setShortExitTarget(shortExitTargetValue);
                                                                                                                                                                                                                                            configurationTest.setShortExitVolatility1(shortExitVolatility1Value);
                                                                                                                                                                                                                                            configurationTest.setShortExitVolatility2(shortExitVolatility2Value);
                                                                                                                                                                                                                                            configurationTest.setShortExitVolatility3(shortExitVolatility3Value);
                                                                                                                                                                                                                                            configurationTest.setLongExitTarget(longExitTargetValue);
                                                                                                                                                                                                                                            configurationTest.setLongExitVolatility1(longExitVolatility1Value);
                                                                                                                                                                                                                                            configurationTest.setLongExitVolatility2(longExitVolatility2Value);
                                                                                                                                                                                                                                            configurationTest.setLongExitVolatility3(longExitVolatility3Value);
                                                                                                                                                                                                                                            configurationTest.setTradeLength(tradeLength);
                                                                                                                                                                                                                                            configurationTest.setTrendLength(trendLength);
                                                                                                                                                                                                                                            configurationTest.setTrendConfirmationLength(trendConfirm);
                                                                                                                                                                                                                                            configurationTest.setTrendBreakLength(trendBreak);
                                                                                                                                                                                                                                            configurationTest.setRangeThreshold(rangeThreshold);
                                                                                                                                                                                                                                            configurationTest.setPriceSlopeFlip(priceSlopeFlip);
                                                                                                                                                                                                                                            // configurationTest.setDaysVol(daysVol);
                                                                                                                                                                                                                                            configurationTest.setPercentOfVolatility(pctOfVol);
                                                                                                                                                                                                                                            configurationTest.setStopLoss(stopLoss1);
                                                                                                                                                                                                                                            configurationTest.setIvAdjustmentType(ivType);
                                                                                                                                                                                                                                            configurationTest.setPriceRocFlip(priceRocFlip);
                                                                                                                                                                                                                                            configurationTest.setVolatilitySlopeFlips(volatilitySlopeFlip);
                                                                                                                                                                                                                                            configurationTest.setVolatilityRocFlip(volatilityRocFlip);
                                                                                                                                                                                                                                            configurationTest.setIvTrendCalcType(IVTrendCalc);
                                                                                                                                                                                                                                            configurationTest.setSignalRocFlips(signalRocFlip);
                                                                                                                                                                                                                                            configurationTest.setIvWeighting(ivWeight);
                                                                                                                                                                                                                                            configurationTest.setDiscountWeighting(discount);
                                                                                                                                                                                                                                            configurationTest.setVolumeWeighting(volume);
                                                                                                                                                                                                                                            configurationTest.setRealizedVolWeighting(realized);
                                                                                                                                                                                                                                            configurationTest.setMovingTrendLength(movingTrendLengths);
                                                                                                                                                                                                                                            configurationTest.setVolLookback(vollLookback);
                                                                                                                                                                                                                                            configurationTest.setUseVolatilitySurface(useVolatilitySurface);
                                                                                                                                                                                                                                            configurationTest.setDollarPercent(dollarPercent);
                                                                                                                                                                                                                                            configurationTest.setTreasuryWeighting(treasury);
                                                                                                                                                                                                                                            configurationTest.setCommodityWeighting(commodity);
                                                                                                                                                                                                                                            configurationTest.setFedTotalAssetsWeighting(fedAssets);
                                                                                                                                                                                                                                            configurationTest.setRevenueRocWeighting(revenue);
                                                                                                                                                                                                                                            configurationTest.setDollarWeighting(dollar);
                                                                                                                                                                                                                                            configurationTest.setCorrelationDays(correlation);
                                                                                                                                                                                                                                            configurationTest.setOilWeighting(oil);
                                                                                                                                                                                                                                            configurationTest.setVixWeighting(vix);
                                                                                                                                                                                                                                            configurationTest.setSignalValueFlip(signalValueFlip);
                                                                                                                                                                                                                                            configurationTest.setLongOpenVolatilityRocThreshold(longVolRoc);
                                                                                                                                                                                                                                            configurationTest.setShortOpenVolatilityRocThreshold(shortVolRoc);
                                                                                                                                                                                                                                       //     configurationTest.setWorkingArray(workingArry);
                                                                                                                                                                                                                                        //    configurationTest.setPriorSuccessArray(priorSuccessArray);
                                                                                                                                                                                                                                       //     configurationTest.setIncrementerArray(incrementerArray);
                                                                                                                                                                                                                                            configurationTest.setVolumeFlip(volumeFlips);
                                                                                                                                                                                                                                            configurationTestList.add(configurationTest);
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                }
                                                                                                                                                                                                            }
                                                                                                                                                                                                        }
                                                                                                                                                                                                    }
                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Connection.Response response;

        response = Jsoup.connect("https://api-secure.wsj.net/api/michelangelo/timeseries/history?json=%7B%22Step%22%3A%22P1D%22%2C%22" +
                        "TimeFrame%22%3A%22ALL%22%2C%22EntitlementToken%22%3A%22cecc4267a0194af89ca343805a3e57af%22%2C%22" +
                        "IncludeMockTick%22%3Atrue%2C%22FilterNullSlots%22%3Afalse%2C%22FilterClosedPoints%22%3Atrue%2C%22" +
                        "IncludeClosedSlots%22%3Afalse%2C%22IncludeOfficialClose%22%3Atrue%2C%22InjectOpen%22%3Afalse%2C%22" +
                        "ShowPreMarket%22%3Afalse%2C%22ShowAfterHours%22%3Afalse%2C%22UseExtendedTimeFrame%22%3Atrue%2C%22" +
                        "WantPriorClose%22%3Atrue%2C%22IncludeCurrentQuotes%22%3Afalse%2C%22ResetTodaysAfterHoursPercentChange%22%3Afalse%2C%22" +
                        "Series%22%3A%5B%7B%22Key%22%3A%22BOND%2FBX%2FXTUP%2FTMUBMUSD01M%22%2C%22Dialect%22%3A%22Charting%22%2C%22Kind%22%3A%22" +
                        "Ticker%22%2C%22SeriesId%22%3A%22s1%22%2C%22DataTypes%22%3A%5B%22Last%22%5D%7D%5D%7D&ckey=cecc4267a0")
                .header("Dylan2010.EntitlementToken", "cecc4267a0194af89ca343805a3e57af")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        JSONObject treasuryDataJson = new JSONObject(response.body());
        JSONArray dateArray = treasuryDataJson.getJSONObject("TimeInfo").getJSONArray("Ticks");
        JSONArray rateArray = treasuryDataJson.getJSONArray("Series").getJSONObject(0).getJSONArray("DataPoints");
        int backTestThreads = threads;
        if(ohShit){
            update = true;
            priorEndDate = LocalDate.now();
            fiveDaysBack = priorEndDate.minusDays(5);
            priorStartDate = LocalDate.of(2022,12,9);
            barRequestStart = priorEndDate.minusDays(15);
            backTestThreads =1;
            fullDateRange = false;
        }

        ArrayList<Bar> priorNasdaqBars = (ArrayList<Bar>) getBarsBetweenDates(priorStartDate, priorEndDate, "QQQ");
        Collections.reverse(priorNasdaqBars);

        List<ConfigurationTest> savedTest = new ArrayList<>();
        double pct = 1;

        ExecutorService executorService2 = Executors.newScheduledThreadPool(64);
        ExecutorService executorService3 = Executors.newScheduledThreadPool(16);


        List<Ticker> uniqueTickers = assembleQuadTemplates(priorStartDate, priorEndDate);
        if(update) {
            int count = 0;
            BarSavingThreadMonitor barSavingThreadMonitor = new BarSavingThreadMonitor(threads, uniqueTickers.size());
            List<List<Ticker>> listOfTickers = ListSplitter.splitTickers(uniqueTickers, threads);
            for (int z = 0; z < threads; z++) {
                BarSavingThread barSavingThread = new BarSavingThread(listOfTickers.get(z), databaseBarRepository, barRequestStart, priorEndDate, barSavingThreadMonitor);
                executorService2.submit(barSavingThread);
              //  barSavingThread.start();
            }
            while (!barSavingThreadMonitor.getBackTestResults()) {

            }
        }


        int count = 0;

        int z = 0;
//        Date priorDate = Date.from(priorStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//        Date priorDateEnd = Date.from(priorEndDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());


        BarFetchingThreadMonitor barFetchingThreadMonitor = new BarFetchingThreadMonitor(threads, uniqueTickers.size());
        for(Ticker uniqueTicker : uniqueTickers){


            BarFetchingThread barFetchingThread = new BarFetchingThread(barFetchingThreadMonitor,databaseBarRepository,uniqueTicker,priorStartDate,priorEndDate,0);
            barFetchingThread.setFullDateRange(fullDateRange);
            executorService2.submit(barFetchingThread);
        }

        while(barFetchingThreadMonitor.getBarResults() == null){

        }

        final HashMap<Ticker,List<Bar>> barMap = barFetchingThreadMonitor.getBarResults();
        final HashMap<Ticker,List<Bar>> deepCopy = new HashMap<>(barMap);
        final Set<Map.Entry<Ticker,List<Bar>>> entrySet  = barMap.entrySet();
        Iterator<Map.Entry<Ticker, List<Bar>>> iterator = entrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<Ticker,List<Bar>> entry = iterator.next();
           // List<String> CIKsFounds = new ArrayList<>();
            for(Map.Entry<Ticker, List<Bar>> entry1 : entrySet){
                if(entry1.getKey().getTicker().equals(entry.getKey().getTicker()) &&
                !entry1.getKey().getCik().equals(entry.getKey().getCik()) && entry1.getValue().size() == entry.getValue().size()){
                    iterator.remove();
                    break;
                }
            }
        }
        start = Instant.now();
        Iterator<Map.Entry<Ticker, List<Bar>>> iterator2 = entrySet.iterator();
        while(iterator2.hasNext()){
         //   for (Map.Entry<Ticker, List<Bar>> entry : entrySet) {
                Map.Entry<Ticker,List<Bar>> entry = iterator2.next();
                List<Bar> barListSafe = new ArrayList<>();
                int size = entry.getValue().size();
                for (int itemIndex = 0; itemIndex < size; itemIndex++) {
                    Bar item = entry.getValue().get(itemIndex);
                    try {
                        barListSafe.add((Bar) item.clone());
                    } catch (Exception ignored) {

                    }
                }
                System.out.println("Building tickers... " + z++ + "/" + uniqueTickers.size());

                BackTestThreadMonitor backTestThreadMonitor = new BackTestThreadMonitor(backTestThreads, configurationTestList.size());
                List<List<ConfigurationTest>> listOfConfigs = ListSplitter.splitConfigs(configurationTestList, backTestThreads);
                for (int x = 0; x < listOfConfigs.size(); x++) {

                    BackTestThread backTestThread = new BackTestThread(listOfConfigs.get(x), barListSafe, configurationTestList.size() == 1,
                            backTestThreadMonitor, entry.getKey(), x,
                            realizedVolatilityRepository, multithreadedNonVerbose, priorEndDate);
                    //   executorService3.submit(backTestThread);
                    executorService3.execute(backTestThread);
                    // backTestThread.start();
                }

                while (backTestThreadMonitor.getBackTestResults() == null) {

                }
                count++;
                now = Instant.now();
                delta = Duration.between(start, now).toMillis();
                rate = ((float) count / delta) * 1000;
                System.out.println(ANSI_ORANGE + "Tickers per second: " + rate + "\n" + ANSI_RESET);

                System.out.println("Complete: " + count + " / " + entrySet.size());
//                if(count % 500 == 0){
//                    System.gc();
//                }
                iterator2.remove();
            }
            ReplayThreadMonitor replayThreadMonitor = new ReplayThreadMonitor(threads * 4, configurationTestList.size());
            List<List<ConfigurationTest>> listOfConfigs2 = ListSplitter.splitConfigs(configurationTestList, threads * 4);
            List<Bar> referenceBars =
                    databaseBarRepository.findAllByCikAndTickerAndDateAfterAndDateBefore("0000723125", "MU", Date.from(priorStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Date.from(priorEndDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

            for (int x = 0; x < listOfConfigs2.size(); x++) {
                ReplayThread replayThread = new ReplayThread(endDate, startDate, referenceBars, replayThreadMonitor, listOfConfigs2.get(x), x);
                replayThread.setBarMap(deepCopy);
                replayThread.start();
            }
            while (replayThreadMonitor.getBackTestResults() == null) {

            }
            ReplayThreadMonitor replayThreadMonitor2 = new ReplayThreadMonitor(threads * 32, configurationTestList.size());


            List<ConfigurationTest> finishedConfigs = replayThreadMonitor.getBackTestResults();
            if (finishedConfigs.size() == 1) {
                //for (Bar bar : barListSafe) {
                for (ConfigurationTest configurationTest1 : finishedConfigs) {
                    // for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {

                    TradeLog tradeLog = configurationTest1.getStockTestList().get(finishedConfigs.size() - 1).getTradeLog();
                    for (IndividualStockTest individualStockTest : configurationTest1.getStockTestList()) {
                        for (Trade trade : individualStockTest.getTradeLog().getClosedTradeList()) {
                            LocalDate localTradeDate = trade.getCloseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localTradeDate.getYear() == endDate.getYear() &&
                                    localTradeDate.getMonthValue() == endDate.getMonthValue() &&
                                    localTradeDate.getDayOfMonth() == endDate.getDayOfMonth()
                            ) {
                                System.out.println("Close: " + trade.getTicker() + trade.getCloseDate() + "\t"  + "\t" + trade.isLong() + "\t" + trade.getClosingPrice());
                            }
                        }
                    }
                    for (IndividualStockTest individualStockTest : configurationTest1.getStockTestList()) {
                        for (Trade trade : individualStockTest.getTradeLog().getActiveTradeList()) {
                            LocalDate localTradeDate = trade.getOpenDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localTradeDate.getYear() == endDate.getYear() &&
                                    localTradeDate.getMonthValue() == endDate.getMonthValue() &&
                                    localTradeDate.getDayOfMonth() == endDate.getDayOfMonth()
                            ) {
                                System.out.println("Open: " + trade.getTicker() + trade.getOpenDate() + "\t"  + "\t" + trade.isLong() + "\t" + trade.getTradeBasis());
                            }
                        }
                    }
                    System.out.println("Active Trades: ");
                    for (IndividualStockTest individualStockTest : configurationTest1.getStockTestList()) {
                        tradeLog = individualStockTest.getTradeLog();
                        for (Trade trade : tradeLog.getActiveTradeList()) {
                            //LocalDate localTradeDate = trade.getOpenDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            System.out.println("Active: " + trade.getTicker() + trade.getOpenDate() + "\t"  + "\t" + trade.isLong() + "\t" + trade.getTradeBasis());
                            //}
                        }
                    }

                }

            }
            finishedConfigs.sort(Comparator.comparing(ConfigurationTest::getSuccessRate));
            //finishedConfigs.removeIf(configurationTest -> configurationTest.getSuccessfulTrades() > 3000);
            for (ConfigurationTest configurationTest : finishedConfigs) {
                if (configurationTest.getSuccessfulTrades() != 0) {
                       // if(configurationTest.getCategorySuccesses()[27] >= 0.75 && configurationTest.getCategoryCounts()[27] >= 45) {
                        StringBuilder stringBuilder = new StringBuilder();
                        boolean allPositive = true;

                        for (IndividualStockTest individualStockTest : configurationTest.getStockTestList()) {
                            if (individualStockTest.getDollars() < 100) {
                                allPositive = false;
                                break;
                            }
                        }
                        if (allPositive) {
                            savedTest.add(configurationTest);
                            stringBuilder.append(ANSI_ORANGE);
                        } else {
                            stringBuilder.append(ANSI_RED);
                        }
                        stringBuilder.append(configurationTest);


                        stringBuilder.append(ANSI_RESET);
                        // if(configurationTest.getSuccessfulTrades() + configurationTest.getFailedTrades() > 20 && configurationTest.getDollars() > 10000 && configurationTest.getSuccessRate()>0.65 && configurationTest.getAverageReturnPerTradingDayHeld() > 1e-6) {
                        System.out.println(stringBuilder);
                        // }
                 //   }
                }
                //}
            }
            for (ConfigurationTest configurationTest : finishedConfigs) {
                configurationTest.getStockTestList().sort(Comparator.comparing(IndividualStockTest::getDollars));
            }
            finishedConfigs.sort(Comparator.comparing(ConfigurationTest::getReturnRsquared));
            for (ConfigurationTest configurationTest : savedTest) {
                if (configurationTest.getSuccessfulTrades() > 2) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(ANSI_ORANGE);
                    stringBuilder.append(configurationTest);
                    stringBuilder.append(ANSI_RESET);
                    //if(configurationTest.getSuccessfulTrades() + configurationTest.getFailedTrades() > 20 && configurationTest.getDollars() > 10000 && configurationTest.getSuccessRate()>0.65 && configurationTest.getAverageReturnPerTradingDayHeld() > 1e-6) {
                    System.out.println(stringBuilder);
                    //}
                }
            }
        now = Instant.now();
        delta = Duration.between(start, now).toMillis();
        System.out.println(ANSI_ORANGE + "TIME TO COMPLETE:  " + delta + "\n" + ANSI_RESET);
        //System.out.println(barList);
    }





    public ConfigurationTest createNewIncrementalConfig(ConfigurationTest savedConfig, ConfigurationTest oldConfig, int paramIndex, double step, boolean success, boolean priorSuccess,boolean gayBoolean){
        try {
            ConfigurationTest newConfig = (ConfigurationTest) oldConfig.clone();
           // newConfig.setWorkingArray(oldConfig.getWorkingArray().clone());
           // newConfig.setIncrementerArray(oldConfig.getIncrementerArray().clone());
            newConfig.setSuccessfulTrades1(0);
            newConfig.setSuccessfulTrades2(0);
            newConfig.setSuccessfulTrades3(0);
            newConfig.setSuccessRate1(0);
            newConfig.setSuccessRate2(0);
            newConfig.setSuccessRate3(0);
            newConfig.setFailedTrades1(0);
            newConfig.setFailedTrades2(0);
            newConfig.setFailedTrades3(0);
            newConfig.setAverageReturnPerTradingDayHeld(0);
            newConfig.setReturnRsquared(0);
            newConfig.setShortRsquared(0);
            newConfig.setReplayDollars(0);
            newConfig.setTradeLog(null);
            newConfig.setAverageCapitalInUse(0);
            newConfig.setLongDollars(0);
            newConfig.setShortDollars(0);
            newConfig.setDollars(0);
          //  newConfig.getWorkingArray()[paramIndex] = step;
//            double[] workingArray = oldConfig.getWorkingArray();
//            if (!success && !priorSuccess) {
//                step = step / 2;
//                workingArray[paramIndex] = workingArray[paramIndex] + step;
//                newConfig.getPriorSuccessArray()[paramIndex] = true;
//            } else if (!success){
//                workingArray[paramIndex] = savedConfig.getWorkingArray()[paramIndex] - step;
//            }else {
//                workingArray[paramIndex] = workingArray[paramIndex] + step;
//            }
//            newConfig.getDirectionArray()[paramIndex] = gayBoolean;
//
//            //if no successful scenarios, then move step
//
//            double[] incrementarArray = oldConfig.getIncrementerArray();
//            incrementarArray[paramIndex] = step;
//            newConfig.setWorkingArray(workingArray);


            return newConfig;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }





    public List<Ticker> assembleQuadTemplates(LocalDate priorStartDate, LocalDate priorEndDate){
        List<Ticker> newList = new ArrayList<>();
        List<String> uniqueTickers = databaseBarRepository.findAllUniqueTickers();
        //List<Bar> all = databaseBarRepository.all();

        for(int i = 0; i < uniqueTickers.size(); i++) {
            String[] strings = uniqueTickers.get(i).split(",");
            newList.add(new Ticker(strings[0],strings[1]));
        }


        newList.add(new Ticker("QQQ", "0001067839", "ETF"));

        newList.add(new Ticker("MU","0000723125", "STOCK"));
        newList.removeIf(ticker -> ticker.getTicker().equals("BE"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SR"));
        newList.removeIf(ticker -> ticker.getTicker().equals("BBX"));
        newList.removeIf(ticker -> ticker.getTicker().equals("CVA"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SDOW"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SRTY"));
        newList.removeIf(ticker -> ticker.getTicker().equals("QID"));
        newList.removeIf(ticker -> ticker.getTicker().equals("SQQQ"));
        newList.removeIf(ticker -> ticker.getTicker().equals("TECS"));
        newList.removeIf(ticker -> ticker.getTicker().equals("BBVA"));

        return newList;
    }


    public Bar buildBarFromJsonData(Object object){
        JSONObject resultObject = (JSONObject) object;
        Bar bar = new Bar();
        try {
            bar.setClose(resultObject.getDouble("c"));
            bar.setOpen(resultObject.getDouble("o"));
            bar.setHigh(resultObject.getDouble("h"));
            bar.setLow(resultObject.getDouble("l"));
            bar.setVolume(resultObject.getDouble("v"));

            bar.setDate(new Date(resultObject.getLong("t") + (60000 * 1440)));
            bar.setSplitAdjustFactor(1);

        }catch (Exception e){
            e.printStackTrace();
        }
        return bar;
    }




    public List<Bar> getBarsBetweenDates(LocalDate from, LocalDate to, String ticker){
        while(true) {
            try {
                String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" +
                        DateFormatter.formatLocalDate(from) + "/" + DateFormatter.formatLocalDate(to) + "?adjusted=true&sort=asc&limit=5500" +
                        "&apiKey=";
                Connection.Response oilResponse = Jsoup.connect
                                (url)
                        .method(Connection.Method.GET)

                        .ignoreContentType(true)
                        .execute();
                JSONObject JsonObject = new JSONObject(oilResponse.body());
                if(JsonObject.has("results")) {
                    JSONArray resultArray = JsonObject.getJSONArray("results");
                    ArrayList<Bar> bars = new ArrayList<>();
                    for (Object object : resultArray) {
                        bars.add(buildBarFromJsonData(object));
                    }
                    return bars;
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}