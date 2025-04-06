package Model;

import BackTest.Trade;

public class TradeIntent {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\033[31;1;4m";

    public static final String ANSI_RESET = "\u001B[0m";
    String tradeComment;

    String side;

    String openCLose;
    int sizeFactor;
    Trade targetTrade;
    boolean escapeFlag;
    int crossType;

    public TradeIntent(String tradeComment, String side, String openCLose, int sizeFactor, Trade trade,boolean escapeFlag, int crossType) {
        this.side = side;
        if(side.equals("long")){
            this.tradeComment = ANSI_GREEN + tradeComment + ANSI_RESET;
        }else if(side.equals("short")){
            this.tradeComment = ANSI_RED + tradeComment + ANSI_RESET;
        }else{
            this.tradeComment = tradeComment;
        }
        this.crossType = crossType;
        this.openCLose = openCLose;
        this.sizeFactor = sizeFactor;
        this.targetTrade = trade;
        this.escapeFlag = escapeFlag;
    }

    public String getTradeComment() {
        return tradeComment;
    }

    public void setTradeComment(String tradeComment) {
        this.tradeComment = tradeComment;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOpenCLose() {
        return openCLose;
    }

    public void setOpenCLose(String openCLose) {
        this.openCLose = openCLose;
    }

    public int getSizeFactor() {
        return sizeFactor;
    }

    public void setSizeFactor(int sizeFactor) {
        this.sizeFactor = sizeFactor;
    }

    public Trade getTargetTrade() {
        return targetTrade;
    }

    public void setTargetTrade(Trade targetTrade) {
        this.targetTrade = targetTrade;
    }

    public boolean isEscapeFlag() {
        return escapeFlag;
    }

    public void setEscapeFlag(boolean escapeFlag) {
        this.escapeFlag = escapeFlag;
    }

    public int getCrossType() {
        return crossType;
    }

    public void setCrossType(int crossType) {
        this.crossType = crossType;
    }
}
