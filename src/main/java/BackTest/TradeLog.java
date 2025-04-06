package BackTest;

import Model.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TradeLog {
    List<Trade> activeTradeList;
    List<Trade> closedTradeList;

    int activeTrades;
    double closedTradesPercentDelta;
    double shortBasis;
    double longBasis;
    boolean shortActive;
    boolean longActive;
    int tradeCounter;
    public TradeLog() {
        activeTrades = 0;
        tradeCounter = 0;
        activeTradeList = new ArrayList<>();
        closedTradeList = new ArrayList<>();

    }

    public void pushNewActiveTrade(Trade trade){
        trade.setTradeNumber(tradeCounter);
        tradeCounter++;
        if(activeTradeList == null){
            activeTradeList = new ArrayList<>();
        }
        activeTradeList.add(trade);
        activeTrades++;
        if(trade.isLong()){
            longActive = true;
        }else{
            shortActive = true;
        }
        calculateTradeBasisForSide(trade.isLong());
    }

    public void calculateTradeBasisForSide(boolean isLong){
        int count = 0;
        double basis = 0;
        for(Trade trade : activeTradeList){
            if(trade.isLong() == isLong){
                basis += trade.getTradeBasis();
                count++;
            }
        }
        if(isLong){
            longBasis = basis/count;
        }else{
            shortBasis = basis/count;
        }
        checkForZeroTrades();
    }

    private void checkForZeroTrades() {
        boolean notFound = true;
        for(Trade trade : activeTradeList){
            if (trade.isLong) {
                notFound = false;
                break;
            }
        }
        if(notFound){
            longBasis = 0;
            longActive = false;
        }
        notFound = true;
        for(Trade trade : activeTradeList){
            if (!trade.isLong) {
                notFound = false;
                break;
            }
        }
        if(notFound){
            shortBasis = 0;
            shortActive = false;
        }
    }
    public void closeAllOnSide(boolean isLong, double closingPrice, Date date, Bar bar, boolean isEscapeFlag, int closingCause){
        Iterator<Trade> iterator = activeTradeList.iterator();
        while (iterator.hasNext()) {
            Trade trade = iterator.next();
        //for(Trade trade : activeTradeList){
            if (trade.isLong() == isLong) {
                if(isEscapeFlag){
                    trade.setClosingCause(1);
                }
                trade.setClosingPrice(closingPrice);
                trade.setCloseDate(date, bar, closingCause);
                trade.setOpen(false);
                closedTradeList.add(trade);
                activeTrades--;
                calculatePercentDelta();
                iterator.remove();
            }
        }
        calculateTradeBasisForSide(isLong);
    }
    public void closeFirstIn(boolean isLong, double closingPrice, Date date, Bar bar, int closingCause) {
        Iterator<Trade> iterator = activeTradeList.iterator();
        while (iterator.hasNext()) {
            Trade tempTrade = iterator.next();
            if (tempTrade.isLong() == isLong) {
                tempTrade.setClosingPrice(closingPrice);
                tempTrade.setCloseDate(date, bar, closingCause);
                tempTrade.setOpen(false);
                closedTradeList.add(tempTrade);
                activeTrades--;
                calculatePercentDelta();
                iterator.remove();
                break;
            }
        }
        calculateTradeBasisForSide(isLong);
    }
    public void closeSpecificTrade(Trade trade, double closingPrice,  Date date, Bar bar, int closingCause) {
        Iterator<Trade> iterator = activeTradeList.iterator();
        while (iterator.hasNext()) {
            Trade tempTrade = iterator.next();
            if (tempTrade.getTradeNumber() == trade.getTradeNumber() && trade.isLong() == tempTrade.isLong()) {
                if(tempTrade.isHalf()){
                    tempTrade.setClosingPrice((tempTrade.getClosingPrice() + closingPrice)/2);
                }else {
                    tempTrade.setClosingPrice(closingPrice);
                }
                tempTrade.setOpen(false);
                tempTrade.setCloseDate(date, bar, closingCause);
                closedTradeList.add(tempTrade);
                activeTrades--;
                calculatePercentDelta();
                iterator.remove();
                break;
            }
        }
        calculateTradeBasisForSide(trade.isLong());
    }

    public void trimPosition(Trade trade, double closingPrice,  Date date, Bar bar) {
        Iterator<Trade> iterator = activeTradeList.iterator();
        while (iterator.hasNext()) {
            Trade tempTrade = iterator.next();
            if (tempTrade.getTradeNumber() == trade.getTradeNumber() && trade.isLong() == tempTrade.isLong()) {
                tempTrade.setClosingPrice(closingPrice);
                tempTrade.setOpen(true);
                tempTrade.setPositionSize(tempTrade.getPositionSize()/2);
                tempTrade.setHalf(true);
                tempTrade.setTrimBar(bar);
                tempTrade.setTrimDate(date);
                //closedTradeList.add(tempTrade);
               // closedTrades++;
               // activeTrades--;
               // calculatePercentDelta();
               // iterator.remove();
                break;
            }
        }
        calculateTradeBasisForSide(trade.isLong());
    }

    private void calculatePercentDelta() {
        double deltaSum = 0.0;
        for(Trade trade : closedTradeList){
            if(trade.isLong){
                deltaSum = deltaSum + ((trade.getClosingPrice() - trade.getTradeBasis())/trade.getTradeBasis());
            }else{
                deltaSum = deltaSum + ((trade.getTradeBasis() - trade.getClosingPrice())/trade.getTradeBasis());
            }
        }
        closedTradesPercentDelta = deltaSum/closedTradeList.size();
    }
    public boolean hasPositionForTicker(String ticker){
        for(Trade trade: activeTradeList){
            if(trade.getTicker().equals(ticker)){
                return true;
            }
        }
        return false;
    }
    public int countForTicker(String ticker){
        int count = 0;
        for(Trade trade: activeTradeList){
            if(trade.getTicker().equals(ticker)){
                count++;
            }
        }
        return count;
    }

    public List<Trade> getActiveTradeList() {
        return activeTradeList;
    }

    public void setActiveTradeList(List<Trade> activeTradeList) {
        this.activeTradeList = activeTradeList;
    }

    public List<Trade> getClosedTradeList() {
        return closedTradeList;
    }

    public void setClosedTradeList(List<Trade> closedTradeList) {
        this.closedTradeList = closedTradeList;
    }

    public int getActiveTrades() {
        return activeTrades;
    }

    public void setActiveTrades(int activeTrades) {
        this.activeTrades = activeTrades;
    }


    public double getClosedTradesPercentDelta() {
        return closedTradesPercentDelta;
    }

    public void setClosedTradesPercentDelta(double closedTradesPercentDelta) {
        this.closedTradesPercentDelta = closedTradesPercentDelta;
    }

    public double getShortBasis() {
        return shortBasis;
    }

    public void setShortBasis(double shortBasis) {
        this.shortBasis = shortBasis;
    }

    public double getLongBasis() {
        return longBasis;
    }

    public void setLongBasis(double longBasis) {
        this.longBasis = longBasis;
    }

    public boolean isShortActive() {
        return shortActive;
    }

    public void setShortActive(boolean shortActive) {
        this.shortActive = shortActive;
    }

    public boolean isLongActive() {
        return longActive;
    }

    public void setLongActive(boolean longActive) {
        this.longActive = longActive;
    }

}
