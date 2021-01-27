package tracesgps;

import android.location.Location;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Classe qui permet de gérer la position.
 * @author William Li.
 */
public class LocationObserver {

    // ATTRIBUTS

    private static Location lastLocation;
    private static PropertyChangeSupport support;

    // CONSTRUCTEURS

    public LocationObserver() {
        if (support == null) {
            support = new PropertyChangeSupport(this);
        }
    }

    // REQUETES

    /**
     * Récupérer la dernière position.
     */
    public Location getLastLocation() {
        return lastLocation;
    }

    // COMMANDES

    /**
     * Fonction qui rafraîchit la dernière position et notifie les écouteurs
     * pour le changement.
     */
    void setLastLocation(Location lastLocation) {
        LocationObserver.lastLocation = lastLocation;
        support.firePropertyChange("lastLocation", null,
                lastLocation);
    }

    /**
     * Fonction qui ajoute "listener" à la liste des écouteurs.
     */
    void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
