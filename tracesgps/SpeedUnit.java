package tracesgps;

import androidx.annotation.NonNull;

/**
 * Type énuméré pour représenter les unités de la vitesse.
 * @author Gledis Shkurti, William Li.
 */
public enum SpeedUnit {
    METERS_PER_SECOND (1. / 1000., "m/s"),
    KILOMETERS_PER_HOUR (SpeedUnit.METERS_PER_SECOND.toDouble() * 1000.
            / 60. / 60., "km/h");

    // ATTRIBUTS

    private double value;
    private String s;

    // CONSTRUCTEURS

    SpeedUnit(double value, String s) {
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


    public static SpeedUnit findValue(String s) {
        if (s.equals("km/h")) {
            return KILOMETERS_PER_HOUR;
        } else {
            return METERS_PER_SECOND;
        }
    }
}

