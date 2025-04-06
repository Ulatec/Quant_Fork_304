package Util;

import Model.Bar;
import Model.ConfigurationTest;
import Model.Ticker;
import Model.TickerQuadTemplate;

import java.util.*;

public class ListSplitter {

    public static ArrayList<List<Bar>> splitBar(List<Bar> fulllist, int numOfOutputs) {
        // get size of the list
        int size = fulllist.size();
        ArrayList<List<Bar>> lists = new ArrayList<>();
        int lastDivider = 0;
        for(int i = 0;i < numOfOutputs; i++){
            int newDivider = (int) Math.round(size*((i+1)/(double)numOfOutputs));
            lists.add(new ArrayList<>(fulllist.subList(lastDivider, newDivider)));
            lastDivider = newDivider;
        }
        // return an List array to accommodate both lists
        return lists;
    }


    public static ArrayList<List<ConfigurationTest>> splitConfigs(List<ConfigurationTest> fulllist, int numOfOutputs) {
        // get size of the list
        int size = fulllist.size();
        ArrayList<List<ConfigurationTest>> lists = new ArrayList<>();
        int lastDivider = 0;
        for(int i = 0;i < numOfOutputs; i++){
            int newDivider = (int) Math.round(size*((i+1)/(double)numOfOutputs));
            lists.add(new ArrayList<>(fulllist.subList(lastDivider, newDivider)));
            lastDivider = newDivider;
        }
        // return an List array to accommodate both lists
        return lists;
    }
    public static ArrayList<List<Ticker>> splitTickers(List<Ticker> fulllist, int numOfOutputs) {
        // get size of the list
        int size = fulllist.size();
        ArrayList<List<Ticker>> lists = new ArrayList<>();
        int lastDivider = 0;
        for(int i = 0;i < numOfOutputs; i++){
            int newDivider = (int) Math.round(size*((i+1)/(double)numOfOutputs));
            lists.add(new ArrayList<>(fulllist.subList(lastDivider, newDivider)));
            lastDivider = newDivider;
        }
        // return an List array to accommodate both lists
        return lists;
    }
//    public static ArrayList<List<Map.Entry<String,List<Bar>>>> splitLists(HashMap<String,List<Bar>> fulllist, int numOfOutputs) {
//        // get size of the list
//        List<Map.Entry<String,List<Bar>>> asList = fulllist.entrySet().stream().toList();
//        int size = asList.size();
//        ArrayList<List<Map.Entry<String,List<Bar>>>> lists = new ArrayList<>();
//        int lastDivider = 0;
//        for(int i = 0;i < numOfOutputs; i++){
//            int newDivider = (int) Math.round(size*((i+1)/(double)numOfOutputs));
//            lists.add(new ArrayList<>(asList.subList(lastDivider, newDivider)));
//            lastDivider = newDivider;
//        }
//        // return an List array to accommodate both lists
//        return lists;
//    }
}
