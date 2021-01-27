package tracesgps;

import android.location.Location;

import java.beans.PropertyChangeListener;

/**
 * Classe qui écoute un MyLocationProvider afin de détecter un changement de
 * position GPS.
 * @author William Li.
 */
public interface TwoPaths {

    // TYPE ENUM

    enum State {
        PAUSED,
        RESUMED,
        STOPPED
    }

    // REQUETES

    /**
     * Renvoie le fichier où est contenu le parcours en cours d'enregistrement.
     */
    SaveToFile getSaveToFile();

    /**
     * Renvoie le parcours en cours d'enregistrement.
     */
    Parcours getRecordingPath();

    /**
     * Renvoie le parcours suivi.
     * Vaut null si aucun parcours n'est suivi.
     */
    Parcours getFollowingPath();

    /**
     * Récupère la dernière position.
     */
    Location getLastLocation();

    /**
     * Renvoie les informations vers la prochaine marque.
     */
    String getInformationOfPathAuto();

    /**
     * Renvoie les informations du parcours en cours.
     */
    String getInformation();

    /**
     * Renvoie vrai si l'enregistrement est en cours.
     */
    boolean isResumed();

    // COMMANDES

    /**
     * Change le parcours suivi.
     * <pre>
     *      p != null </pre>
     * <post>
     *      getFollowingPath() == p </post>
     */
    void setFollowingPath(Parcours p);

    /**
     * Mets l'enregistrement du parcours en pause.
     */
    void pause();

    /**
     * Relance l'enregistrement du parcours.
     */
    void resume();

    /**
     * Arrête l'enregistrement du parcours.
     */
    void stop();

    /**
     * Ajoute "listener" à la liste des écouteurs.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Retire "listener" de la liste des écouteurs.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);
}
