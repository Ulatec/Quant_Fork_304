package Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity

public class RealizedVolatility {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    public Date date;

    public int daysVol;

    public double realizedVolatility;

    public String ticker;


    public RealizedVolatility() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDaysVol() {
        return daysVol;
    }

    public void setDaysVol(int daysVol) {
        this.daysVol = daysVol;
    }

    public double getRealizedVolatility() {
        return realizedVolatility;
    }

    public void setRealizedVolatility(double realizedVolatility) {
        this.realizedVolatility = realizedVolatility;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
