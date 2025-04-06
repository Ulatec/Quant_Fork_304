package BackTest;

import java.util.Date;
import java.util.Objects;

public class CacheKey {

    private Date date;

    private String ticker;
    private int length;

    private int offset;

    public CacheKey(Date date, int length, int offset, String ticker) {
        this.date = date;
        this.length = length;
        this.offset = offset;
        this.ticker = ticker;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return length == cacheKey.length && offset == cacheKey.offset && Objects.equals(date, cacheKey.date) && Objects.equals(ticker, cacheKey.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, length, offset);
    }
}
