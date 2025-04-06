package Threads;

import Model.Bar;
import Model.CalculationObject;
import Model.Ticker;
import Repository.DatabaseBarRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BarFetchingThread extends Thread{

    public BarFetchingThreadMonitor barFetchingThreadMonitor;
    public DatabaseBarRepository databaseBarRepository;
    public Ticker uniqueTicker;
    public LocalDate priorStartDate;
    public LocalDate priorEndDate;
    public int threadNum;
    public boolean fullDateRange;

    public boolean isFullDateRange() {
        return fullDateRange;
    }

    public void setFullDateRange(boolean fullDateRange) {
        this.fullDateRange = fullDateRange;
    }

    public BarFetchingThread(BarFetchingThreadMonitor barFetchingThreadMonitor, DatabaseBarRepository databaseBarRepository,
                             Ticker uniqueTicker, LocalDate priorStartDate, LocalDate priorEndDate, int threadNum){
        this.barFetchingThreadMonitor = barFetchingThreadMonitor;
        this.databaseBarRepository = databaseBarRepository;
        this.uniqueTicker = uniqueTicker;
        this.priorStartDate = priorStartDate;
        this.priorEndDate = priorEndDate;
        this.threadNum  = threadNum;
    }

    @Override
    public void run() {
        HashMap<Ticker, List<Bar>> barMap = new HashMap<>();
        while(true) {
            try {
                List<Bar> uniqueBarTickers;
                // if(!fullDateRange) {
                uniqueBarTickers = databaseBarRepository.findAllByCikAndTickerAndDateAfterAndDateBefore(uniqueTicker.getCik(), uniqueTicker.getTicker(), Date.from(priorStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Date.from(priorEndDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                // }else{
                //     uniqueBarTickers = databaseBarRepository.findAllByCikAndTicker(uniqueTicker.getCik(),uniqueTicker.getTicker());
                // }
                for (Bar bar : uniqueBarTickers) {
                    CalculationObject calculationObject = new CalculationObject();
                    bar.setCalculationObject(calculationObject);
                }
                //barListSafe.addAll(uniqueBarTickers);

                uniqueBarTickers.sort(Comparator.comparing(Bar::getDate));
                barMap.put(uniqueTicker, uniqueBarTickers);
                break;
                //System.out.println("Building tickers... " + z++ + "/" + tickerList.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        barFetchingThreadMonitor.threadFinished(threadNum,barMap);
    }
}
