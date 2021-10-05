package CSRfidJavaMultipleReader;

/**
 *
 */
public class RateInfo implements Comparable {
    public String source;
    public String deviceName;
    public int count = 0;
    public int debugcount = 0;

    public int compareTo(Object o) {
        RateInfo that;
        that = (RateInfo)o;
        return this.source.compareTo(that.source);
    }
}
