package BackTest;

public class BasicBucketReadout {
    public int successfulTrades;

    public int failedTrades;

    public double moneyChange;

    public double percentage;

    public void calculatePercentange(){
        percentage = (double)successfulTrades/(failedTrades + successfulTrades);
    }

    public int getSuccessfulTrades() {
        return successfulTrades;
    }

    public void setSuccessfulTrades(int successfulTrades) {
        this.successfulTrades = successfulTrades;
        calculatePercentange();
    }

    public int getFailedTrades() {
        return failedTrades;
    }

    public void setFailedTrades(int failedTrades) {
        this.failedTrades = failedTrades;
        calculatePercentange();
    }

    public double getMoneyChange() {
        return moneyChange;
    }

    public void setMoneyChange(double moneyChange) {
        this.moneyChange = moneyChange;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "BasicBucketReadout{" +
                "successfulTrades=" + successfulTrades +
                ", failedTrades=" + failedTrades +
                ", moneyChange=" + moneyChange +
                ", percentage=" + percentage +
                '}';
    }
}
