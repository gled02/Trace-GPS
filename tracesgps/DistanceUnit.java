package tracesgps;

import androidx.annotation.NonNull;

/**
 * Type énuméré pour représenter les unités de distance.
 * @author Gledis Shkurti, William Li.
 */
public enum DistanceUnit {
    METERS (1, "m"),
    KILOMETERS (1000 * DistanceUnit.METERS.toDouble(), "km");

    // ATTRIBUTS

    private double value;
    private String s;

    // CONSTRUCTEURS

    DistanceUnit(double value, String s) {
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

    public static DistanceUnit findValue(String s) {
        if (s.equals("km")) {
            return KILOMETERS;
        } else {
            return METERS;
        }
    }
}
