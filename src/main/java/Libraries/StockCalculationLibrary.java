package Libraries;



import BackTest.CacheKey;
import Model.Bar;
import Model.ImpliedVolaility;
import Model.RealizedVolatility;
import Repository.RealizedVolatilityRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class StockCalculationLibrary {

    private HashMap<CacheKey, Double> varianceCache = new HashMap<>();



    public StockCalculationLibrary() {

    }

    public double getLogVarianceReverse(List<Bar> barList, int dayOffset, int length, String ticker) {
        //length = length - 10;
        if (dayOffset - length - 1 > 0) {
            CacheKey cacheKey = new CacheKey(barList.get(dayOffset).getDate(), length, dayOffset, ticker);
            if (varianceCache.containsKey(cacheKey)) {
                return varianceCache.get(cacheKey);
            } else {
                //not in cache
                if (dayOffset - length - 1 > 0) {
                    //LocalDate startDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(length  + 1);
                    //LocalDate endDate = barList.get(dayOffset).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
                    List<Double> returnsBetweenBars = new ArrayList<>();
                    // List<Double> oldBetween = new ArrayList<>();
                    long startTimestamp = barList.get(dayOffset).getDate().getTime() - ((long) (length + 1) * 3600000 * 24);
                    double sum = 0;
//                    double sumSq = 0;
                    int trackingVariable = dayOffset;
                    // Date date = Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    while (true) {
                        if (barList.get(trackingVariable).getDate().getTime() > (startTimestamp) && trackingVariable - 1 > 0) {
                            double a = barList.get(trackingVariable).getClose();

                            double b = barList.get(trackingVariable - 1).getClose();
                            double d = Math.log(a / b);
                            returnsBetweenBars.add(d);
                            sum += d;
                        } else {
                            break;
                        }
                        trackingVariable--;
                    }



//                    DoubleSummaryStatistics doubleSummaryStatistics = returnsBetweenBars.stream().mapToDouble(x -> x).summaryStatistics();
                    double variance = 0;
                    int size = returnsBetweenBars.size();
                    int oldSize = returnsBetweenBars.size();
                    for (int i = 0; i < size; i++) {
                        variance += Math.pow(returnsBetweenBars.get(i) - sum / oldSize, 2);
                    }
                    variance /= oldSize - 1;
                    double old = 100 * (Math.sqrt(variance) * Math.sqrt(252));

                    return old;

                }


            }

        }
        return 0.0;
    }

}