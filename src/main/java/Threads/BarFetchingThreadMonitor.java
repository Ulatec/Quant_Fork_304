package Threads;

import Model.Bar;
import Model.ConfigurationTest;
import Model.Ticker;

import java.util.*;

public class BarFetchingThreadMonitor {
    int numberOfThreads;
    int finishedThreads;
    List<ConfigurationTest> configurationTests ;
    int originalListSize;
    HashMap<Ticker, List<Bar>> barMap = new HashMap<>();

    boolean safeToKill = false;
    public BarFetchingThreadMonitor(int numberOfThreads, int originalListSize){
        this.numberOfThreads = numberOfThreads;
        this.originalListSize = originalListSize;
        configurationTests = new ArrayList<>();
        finishedThreads = 0;
    }
    public void run(){
        while(!safeToKill) {
            //DO NOTHING
        }

        System.out.println("ThreadMonitor finished.");
    }

    public synchronized void threadFinished(int threadNum, HashMap<Ticker,List<Bar>> barMap){
        this.configurationTests.addAll(configurationTests);
        this.barMap.putAll(barMap);
        finishedThreads++;
        System.out.println(finishedThreads + " finished.");
       // System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            System.out.println();
        }
    }

    public synchronized HashMap<Ticker,List<Bar>> getBarResults(){

        if(barMap.entrySet().size()<originalListSize){
            return null;
        }else{
            safeToKill = true;
            return barMap;
        }
    }

    public synchronized Map.Entry<Ticker,List<Bar>> getNextIfAvailable(){
        if(!barMap.isEmpty()){
            Iterator<Map.Entry<Ticker,List<Bar>>> entryIterator = barMap.entrySet().iterator();
            Map.Entry<Ticker,List<Bar>> entry= entryIterator.next();
            entryIterator.remove();
            return entry;
        }
        return null;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getFinishedThreads() {
        return finishedThreads;
    }

    public void setFinishedThreads(int finishedThreads) {
        this.finishedThreads = finishedThreads;
    }


}
