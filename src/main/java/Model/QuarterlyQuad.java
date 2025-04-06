package Model;

import java.util.Date;
import java.util.Objects;

public class QuarterlyQuad {

    public int quadNumber;

    public Date beginDate;

    public Date endDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuarterlyQuad that = (QuarterlyQuad) o;
        return quadNumber == that.quadNumber && Objects.equals(beginDate, that.beginDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quadNumber, beginDate, endDate);
    }
}
