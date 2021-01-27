package tracesgps;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Classe qui dérive "Location" et qui modélise une balise qui est composée
 * de la Latitude, de la Longitude, d'un temps et de commentaires sous la forme
 * d'une chaîne de caractères.
 * @author Gledis Shkurti, William Li.
 */
class Balise extends Location {

    // ATTRIBUTS

    private final String comments;

    // CONSTRUCTEURS

    Balise(Location l) {
        super(l);
        this.comments = "";
    }

    Balise(double lat, double lon, long time, String comments) {
        super("");
        this.setLatitude(lat);
        this.setLongitude(lon);
        this.setTime(time);
        this.comments = comments;
    }

    // REQUETES

    /**
     * Renvoie les commentaires d'une balise.
     */
    String getComments() {
        return this.comments;
    }

    // OUTILS

    /**
     * Renvoie le temps d'une balise sous la forme d'une chaîne de caractères.
     */
    String getStringFormatTime() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                Locale.getDefault()).format(new Date(this.getTime()));
    }

    /**
     * Renvoie vrai si la prochaine balise du parcours est valide, faux sinon.
     */
    boolean isValidate(Location b) {
        return this.distanceTo(b) <= Settings.getRadiusAccuracy();
    }
}
