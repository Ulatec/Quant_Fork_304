package Model;

import java.util.List;

public class TickerQuadTemplate {

    String ticker;

    List<Integer> longQuads;

    List<Integer> shortQuads;


    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public List<Integer> getLongQuads() {
        return longQuads;
    }

    public void setLongQuads(List<Integer> longQuads) {
        this.longQuads = longQuads;
    }

    public List<Integer> getShortQuads() {
        return shortQuads;
    }

    public void setShortQuads(List<Integer> shortQuads) {
        this.shortQuads = shortQuads;
    }

    public TickerQuadTemplate(String ticker, List<Integer> longQuads, List<Integer> shortQuads) {
        this.ticker = ticker;
        this.longQuads = longQuads;
        this.shortQuads = shortQuads;
    }
}
