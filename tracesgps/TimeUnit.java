package tracesgps;

import androidx.annotation.NonNull;

/**
 * Type énuméré pour représenter les unités de temps.
 * @author Gledis Shkurti, William Li.
 */
public enum TimeUnit {
    MILLISECONDS (1, "ms"),
    SECONDS (1000 * TimeUnit.MILLISECONDS.toDouble(), "s"),
    MINUTES (60 * TimeUnit.SECONDS.toDouble(), "min"),
    HOURS (60 * TimeUnit.MINUTES.toDouble(), "h"),
    DAYS (24 * TimeUnit.HOURS.toDouble(), "days"),
    WEEKS (7 * TimeUnit.DAYS.toDouble(), "weeks"),
    YEARS (365.25 * TimeUnit.DAYS.toDouble(), "years"),
    MONTHS (TimeUnit.YEARS.toDouble() / 12, "months");

    // ATTRIBUTS

    private double value;
    private String s;

    // CONSTRUCTEURS

    TimeUnit(double value, String s) {
        this.value = value;
        this.s = s;
    }

    // REQUETES

    public double toDouble() {
        return value;
    }

    @NonNull
    public String toString() {
        return s;
    }

    public static TimeUnit findValue(String s) {
        switch (s) {
            case "ms" :
                return MILLISECONDS;
            case "s" :
                return SECONDS;
            case "min" :
                return  MINUTES;
            case "h" :
                return HOURS;
            case "days" :
                return DAYS;
            case "weeks" :
                return WEEKS;
            case "years" :
                return YEARS;
            case "months" :
                return MONTHS;
        }
        return null;
    }
}
