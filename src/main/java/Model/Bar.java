package Model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class Bar implements Cloneable{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date date;
    private String ticker;
    private String cik;
    private double low;

    private double high;

    private double close;

    private double open;


    private double volume;

    private double splitAdjustFactor;

    private double dollarCorrelationFactor;

    private double realizedVolCorrelationFactor;
    private double treasuryCorrelationFactor;


    @Transient
    public double commodityRateOfChange;
    @Transient
    public double commodityCorrelationFactor;
    @Transient
    public double commodityIndexValue;

    public Double treasuryRate;
    public Double dollarRate;
    public double baseVolatility;
    public double baseLongVolatility;
    public double oilValue;
    public double oilCorrelationFactor;

    @Transient
    public double oilSlopeRoc;
    public double goldCorrelationFactor;
    public double tenYearCorrelationFactor;

    public double volumeChange;

    public double treasuryYieldSlope;

    @Transient
    public double dollarSlopeRoc;
    public float signalSlopeLong;
    @Transient
    public double commoditySlope;

    public double goldValue;
    public double goldSlope;
    public double goldSlopeRoc;
    public double tenYearRate;
    public double tenYearSlope;
    public double tenYearSlopeRoc;

    public float signalRocLong;

    public double volatilitySlopeLong;

    private boolean earningsDate;

    private double sharesOutstanding;
    private double marketCap;
    private float  signalcdf;
    private float  volumecdf;
    private float signalroccdf;
    private float volroccdf;
    private float volcdf;

    @Transient
    private double vixSlopeRoc;
    @Transient
    public long mostRecentEarnings;
    @Transient
    public boolean yearAgoEarnings;
    @Transient
    public CalculationObject calculationObject;
    @Transient
    public double averageVolume;
    @Column(columnDefinition = "double default 0.0")
    public double oilSlope;
//    @Column(columnDefinition = "double default 0.0")
//    private double vixSlope;
    @Column(columnDefinition = "double default 0.0")
    public double volatilitySlopeRoCLong;
    @Transient
    public double priceSlope;
    @Column(columnDefinition = "double default 0.0")
    public double treasuryYieldSlopeRoc;
    @Transient
    public double commodityCorrelationSlope;
    @Column(columnDefinition = "double default 0.0")
    public double dollarSlope;
    @Transient
    public double priceSlopeRoc;



    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }




    @Override
    public String toString() {
        return "Bar{" +
                "date=" + date +
                ", low=" + low +
                ", high=" + high +
                ", close=" + close +
                ", open=" + open +
                '}';
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getSplitAdjustFactor() {
        return splitAdjustFactor;
    }

    public void setSplitAdjustFactor(double splitAdjustFactor) {
        this.splitAdjustFactor = splitAdjustFactor;
    }




    @Override
    public Object clone() throws CloneNotSupportedException {
        Bar clone = null;
        try  {
            clone = (Bar) super.clone();
            //Copy new date object to cloned method
//            clone.setDob((Date) this.getDob().clone());
        }
        catch (CloneNotSupportedException e)  {
            throw new RuntimeException(e);
        }
        return clone;
    }



    public double getDollarCorrelationFactor() {
        return dollarCorrelationFactor;
    }

    public void setDollarCorrelationFactor(double dollarCorrelationFactor) {
        this.dollarCorrelationFactor = dollarCorrelationFactor;
    }

    public double getRealizedVolCorrelationFactor() {
        return realizedVolCorrelationFactor;
    }

    public void setRealizedVolCorrelationFactor(double realizedVolCorrelationFactor) {
        this.realizedVolCorrelationFactor = realizedVolCorrelationFactor;
    }

    public Double getTreasuryRate() {
        return treasuryRate;
    }

    public void setTreasuryRate(Double treasuryRate) {
        this.treasuryRate = treasuryRate;
    }

    public double getTreasuryCorrelationFactor() {
        return treasuryCorrelationFactor;
    }

    public void setTreasuryCorrelationFactor(double treasuryCorrelationFactor) {
        this.treasuryCorrelationFactor = treasuryCorrelationFactor;
    }

    public double getCommodityRateOfChange() {
        return commodityRateOfChange;
    }

    public void setCommodityRateOfChange(double commodityRateOfChange) {
        this.commodityRateOfChange = commodityRateOfChange;
    }

    public double getCommodityCorrelationFactor() {
        return commodityCorrelationFactor;
    }

    public void setCommodityCorrelationFactor(double commodityCorrelationFactor) {
        this.commodityCorrelationFactor = commodityCorrelationFactor;
    }

    public double getCommodityIndexValue() {
        return commodityIndexValue;
    }

    public void setCommodityIndexValue(double commodityIndexValue) {
        this.commodityIndexValue = commodityIndexValue;
    }




    public double getBaseVolatility() {
        return baseVolatility;
    }

    public void setBaseVolatility(double baseVolatility) {
        this.baseVolatility = baseVolatility;
    }

    public double getBaseLongVolatility() {
        return baseLongVolatility;
    }

    public void setBaseLongVolatility(double nasdaqVol) {
        this.baseLongVolatility = nasdaqVol;
    }

    public double getOilValue() {
        return oilValue;
    }

    public void setOilValue(double oilValue) {
        this.oilValue = oilValue;
    }

    public double getOilCorrelationFactor() {
        return oilCorrelationFactor;
    }

    public void setOilCorrelationFactor(double oilCorrelationFactor) {
        this.oilCorrelationFactor = oilCorrelationFactor;
    }


    public double getGoldCorrelationFactor() {
        return goldCorrelationFactor;
    }

    public void setGoldCorrelationFactor(double goldCorrelationFactor) {
        this.goldCorrelationFactor = goldCorrelationFactor;
    }



    public double getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(double volumeChange) {
        this.volumeChange = volumeChange;
    }

    public double getTreasuryYieldSlope() {
        return treasuryYieldSlope;
    }

    public void setTreasuryYieldSlope(double treasuryYieldSlope) {
        this.treasuryYieldSlope = treasuryYieldSlope;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getDollarRate() {
        return dollarRate;
    }

    public void setDollarRate(Double dollarRate) {
        this.dollarRate = dollarRate;
    }

    public float getSignalSlopeLong() {
        return signalSlopeLong;
    }

    public void setSignalSlopeLong(float signalSlopeLong) {
        this.signalSlopeLong = signalSlopeLong;
    }

    public float getSignalRocLong() {
        return signalRocLong;
    }

    public void setSignalRocLong(float signalRocLong) {
        this.signalRocLong = signalRocLong;
    }

    public double getVolatilitySlopeLong() {
        return volatilitySlopeLong;
    }

    public void setVolatilitySlopeLong(double volatilitySlopeLong) {
        this.volatilitySlopeLong = volatilitySlopeLong;
    }


    public boolean isEarningsDate() {
        return earningsDate;
    }

    public void setEarningsDate(boolean earningsDate) {
        this.earningsDate = earningsDate;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public double getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(double sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }



    public double getOilSlopeRoc() {
        return oilSlopeRoc;
    }

    public void setOilSlopeRoc(double oilSlopeRoc) {
        this.oilSlopeRoc = oilSlopeRoc;
    }

    public double getDollarSlopeRoc() {
        return dollarSlopeRoc;
    }

    public void setDollarSlopeRoc(double dollarSlopeRoc) {
        this.dollarSlopeRoc = dollarSlopeRoc;
    }

    public double getCommoditySlope() {
        return commoditySlope;
    }

    public void setCommoditySlope(double commoditySlope) {
        this.commoditySlope = commoditySlope;
    }

    public double getVixSlopeRoc() {
        return vixSlopeRoc;
    }

    public void setVixSlopeRoc(double vixSlopeRoc) {
        this.vixSlopeRoc = vixSlopeRoc;
    }


    public CalculationObject getCalculationObject() {
        return calculationObject;
    }

    public void setCalculationObject(CalculationObject calculationObject) {
        this.calculationObject = calculationObject;
    }

    public double getOilSlope() {
        return oilSlope;
    }

    public void setOilSlope(double oilSlope) {
        this.oilSlope = oilSlope;
    }

//    public double getVixSlope() {
//        return vixSlope;
//    }
//
//    public void setVixSlope(double vixSlope) {
//        this.vixSlope = vixSlope;
//    }

    public double getVolatilitySlopeRoCLong() {
        return volatilitySlopeRoCLong;
    }

    public void setVolatilitySlopeRoCLong(double volatilitySlopeRoCLong) {
        this.volatilitySlopeRoCLong = volatilitySlopeRoCLong;
    }




    public double getPriceSlope() {
        return priceSlope;
    }

    public void setPriceSlope(double priceSlope) {
        this.priceSlope = priceSlope;
    }

    public double getTreasuryYieldSlopeRoc() {
        return treasuryYieldSlopeRoc;
    }

    public void setTreasuryYieldSlopeRoc(double treasuryYieldSlopeRoc) {
        this.treasuryYieldSlopeRoc = treasuryYieldSlopeRoc;
    }

    public double getCommodityCorrelationSlope() {
        return commodityCorrelationSlope;
    }

    public void setCommodityCorrelationSlope(double commodityCorrelationSlope) {
        this.commodityCorrelationSlope = commodityCorrelationSlope;
    }

    public double getDollarSlope() {
        return dollarSlope;
    }

    public void setDollarSlope(double dollarSlope) {
        this.dollarSlope = dollarSlope;
    }

    public double getPriceSlopeRoc() {
        return priceSlopeRoc;
    }

    public void setPriceSlopeRoc(double priceSlopeRoc) {
        this.priceSlopeRoc = priceSlopeRoc;
    }



    public double getTenYearRate() {
        return tenYearRate;
    }

    public void setTenYearRate(double tenYearRate) {
        this.tenYearRate = tenYearRate;
    }

    public double getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(double goldValue) {
        this.goldValue = goldValue;
    }

    public double getTenYearCorrelationFactor() {
        return tenYearCorrelationFactor;
    }

    public void setTenYearCorrelationFactor(double tenYearCorrelationFactor) {
        this.tenYearCorrelationFactor = tenYearCorrelationFactor;
    }

    public double getGoldSlope() {
        return goldSlope;
    }

    public void setGoldSlope(double goldSlope) {
        this.goldSlope = goldSlope;
    }

    public double getTenYearSlope() {
        return tenYearSlope;
    }

    public void setTenYearSlope(double tenYearSlope) {
        this.tenYearSlope = tenYearSlope;
    }

    public double getGoldSlopeRoc() {
        return goldSlopeRoc;
    }

    public void setGoldSlopeRoc(double goldSlopeRoc) {
        this.goldSlopeRoc = goldSlopeRoc;
    }

    public double getTenYearSlopeRoc() {
        return tenYearSlopeRoc;
    }

    public void setTenYearSlopeRoc(double tenYearSlopeRoc) {
        this.tenYearSlopeRoc = tenYearSlopeRoc;
    }

    public boolean isYearAgoEarnings() {
        return yearAgoEarnings;
    }

    public void setYearAgoEarnings(boolean yearAgoEarnings) {
        this.yearAgoEarnings = yearAgoEarnings;
    }

    public long getMostRecentEarnings() {
        return mostRecentEarnings;
    }

    public void setMostRecentEarnings(long mostRecentEarnings) {
        this.mostRecentEarnings = mostRecentEarnings;
    }

    public double getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(double averageVolume) {
        this.averageVolume = averageVolume;
    }

    public float getSignalcdf() {
        return signalcdf;
    }

    public void setSignalcdf(float signalcdf) {
        this.signalcdf = signalcdf;
    }

    public float getVolumecdf() {
        return volumecdf;
    }

    public void setVolumecdf(float volumecdf) {
        this.volumecdf = volumecdf;
    }

    public float getSignalroccdf() {
        return signalroccdf;
    }

    public void setSignalroccdf(float signalroccdf) {
        this.signalroccdf = signalroccdf;
    }

    public float getVolroccdf() {
        return volroccdf;
    }

    public void setVolroccdf(float volroccdf) {
        this.volroccdf = volroccdf;
    }

    public float getVolcdf() {
        return volcdf;
    }

    public void setVolcdf(float volcdf) {
        this.volcdf = volcdf;
    }


    //    public double getOilSlope() {
//        return oilSlope;
//    }
//
//    public void setOilSlope(double oilSlope) {
//        this.oilSlope = oilSlope;
//    }
//
//    public double getOilSlopeRoc() {
//        return oilSlopeRoc;
//    }
//
//    public void setOilSlopeRoc(double oilSlopeRoc) {
//        this.oilSlopeRoc = oilSlopeRoc;
//    }
}
