package Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Ticker {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    String ticker;

    String name;

    String type;

    String market;

    String cik;
    double thirtyDayDollarVolume;
    boolean isETF;

    public Ticker() {

    }
    public Ticker(String ticker) {
        this.ticker = ticker;
    }
    public Ticker(String ticker, String cik){
        this.ticker = ticker;
        this.cik = cik;
    }
    public Ticker(String ticker, String cik, String type){
        this.ticker = ticker;
        this.cik = cik;
        this.type = type;
    }
    public Ticker(String ticker,boolean isEtf) {
        this.ticker = ticker;
        this.isETF = isEtf;
    }
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public double getThirtyDayDollarVolume() {
        return thirtyDayDollarVolume;
    }

    public void setThirtyDayDollarVolume(double thirtyDayDollarVolume) {
        this.thirtyDayDollarVolume = thirtyDayDollarVolume;
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "id=" + id +
                ", ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", market='" + market + '\'' +
                ", cik='" + cik + '\'' +
                '}';
    }

    public boolean isETF() {
        return isETF;
    }

    public void setETF(boolean ETF) {
        isETF = ETF;
    }
}
