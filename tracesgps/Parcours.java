package tracesgps;

import java.util.List;

/**
 * Interface qui modélise un parcours qui est composé d'une liste de balises.
 *  <cons>
 *      $DESC$
 *          Un parcours vide.
 *      $ARGS$ -
 *      $POST$
 *          getParcours() == new ArrayList<>() </cons>
 *  <cons>
 *      $DESC$
 *          Un parcours initialisé.
 *      $ARGS$ list
 *      $POST$
 *          getParcours() == list </cons>
 * @author Gledis Shkurti, William Li.
 */
public interface Parcours extends List<Balise> {

    // REQUETES

    /**
     * Renvoie le parcours à suivre pour revenir au point de départ
     * selon la formule du vol d’oiseau.
     */
    Parcours goToStartDirectly();

    /**
     * Renvoie le parcours à suivre pour revenir au point de départ
     * selon la formule de suivi du chemin inverse.
     */
    Parcours goToStartReversePath();

    /**
     * Renvoie la dernière balise du parcours ou null si le parcours est vide.
     */
    Balise getLastBalise();

    /**
     * Renvoie la distance parcourue.
     */
    float getTravelDistance();

    /**
     * Renvoie la vitesse calculée sur tout le parcours.
     */
    double getAverageSpeed();

    /**
     * Renvoie la vitesse calculée sur les nbBalises dernières balises.
     */
    double getAverageSpeed(int nbBalises);

    /**
     * Renvoie la vitesse maximale effectuée sur ce parcours.
     */
    double getMaxSpeed();
}
