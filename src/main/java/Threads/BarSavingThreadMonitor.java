package Threads;

import Model.ConfigurationTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BarSavingThreadMonitor {

    int numberOfThreads;
    int finishedThreads;
    List<ConfigurationTest> configurationTests ;
    int originalListSize;
    HashMap<Integer, List<ConfigurationTest>> barMap = new HashMap<>();

    boolean safeToKill = false;
    public BarSavingThreadMonitor(int numberOfThreads, int originalListSize){
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

    public synchronized void threadFinished(){
        finishedThreads++;
    }

    public synchronized boolean getBackTestResults(){
        if(finishedThreads == numberOfThreads){
            return true;
        }else{
            return false;
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
