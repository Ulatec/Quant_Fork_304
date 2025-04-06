package Threads;

import Model.ConfigurationTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplayThreadMonitor {
    int numberOfThreads;
    int finishedThreads;
    List<ConfigurationTest> configurationTests;
    int originalListSize;
    HashMap<Integer, List<ConfigurationTest>> barMap = new HashMap<>();

    boolean safeToKill = false;
    public ReplayThreadMonitor(int numberOfThreads, int originalListSize){
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

    public synchronized void threadFinished(int threadNum, List<ConfigurationTest> configurationTests){
        this.configurationTests.addAll(configurationTests);
        barMap.put(threadNum, configurationTests);
        finishedThreads++;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            System.out.println();
        }
    }

    public synchronized List<ConfigurationTest> getBackTestResults(){
        if(barMap.entrySet().size()<numberOfThreads){
            return null;
        }else{
            safeToKill = true;
            return configurationTests;
        }
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
