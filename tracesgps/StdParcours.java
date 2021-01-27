package tracesgps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Gledis Shkurti, William Li.
 */
public class StdParcours extends ArrayList<Balise> implements Parcours {

    // CONSTRUCTEURS

    StdParcours() {
        super();
    }

    private StdParcours(List<Balise> list) {
        super(list);
    }

    // REQUETES

    @Override
    public Parcours goToStartDirectly() {
        if (this.size() == 0) {
            return null;
        }
        Parcours newParcours = new StdParcours();
        newParcours.add(this.get(0));
        return newParcours;
    }

    @Override
    public Parcours goToStartReversePath() {
        Parcours newParcours = (Parcours) this.clone();
        Collections.reverse(newParcours);
        return new StdParcours(newParcours);
    }

    @Override
    public Balise getLastBalise() {
        if (this.size() == 0) {
            return null;
        }
        return this.get(this.size() - 1);
    }

    @Override
    public float getTravelDistance() {
        if (this.size() <= 1) {
            return 0;
        }
        return getTravelDistance(this.size());
    }

    @Override
    public double getAverageSpeed() {
        return getAverageSpeed(this.size());
    }

    @Override
    public double getAverageSpeed(int nbBalises) {
        if (nbBalises < 0) {
            throw new AssertionError("Le nb de balise d'un " +
                    "parcours ne peut pas être négatif");
        }
        if (nbBalises <= 1 || this.size() < nbBalises) {
            return 0;
        }
        return this.getTravelDistance(nbBalises) / this.getTravelTime(nbBalises);
    }

    @Override
    public double getMaxSpeed() {
        double max = 0;
        int i = 0;
        for (int j = Settings.getNbBalisesForSpeed(); j < this.size(); ++j) {
            double speed = distance(i, j) / time(i, j);
            if (speed > max) {
                max = speed;
            }
            ++i;
        }
        return max;
    }

    // OUTILS

    /**
     * Renvoie la distance d'un parcours composé de nbBalises.
     */
    private float getTravelDistance(int nbBalises) {
        if (nbBalises < 0) {
            throw new AssertionError("Le nb de balise d'un " +
                    "parcours ne peut pas négatif");
        }
        if (nbBalises <= 1 || this.size() < nbBalises) {
            return 0;
        }
        float distance = 0;
        Balise last = this.getLastBalise();
        for (int i = 1; i < nbBalises; i++) {
            Balise actual = this.get(this.size() - 1 - i);
            distance += actual.distanceTo(last);
            last = actual;
        }
        return distance;
    }

    /**
     * Renvoie le temps d'un parcours composé de nbBalises.
     */
    private long getTravelTime(int nbBalises) {
        if (nbBalises < 0) {
            throw new AssertionError("Le nb de balise d'un " +
                    "parcours ne peut pas négatif");
        }
        if (nbBalises <= 1 || this.size() < nbBalises) {
            return 0;
        }
        return this.getLastBalise().getTime()
                - this.get(this.size() - nbBalises).getTime();
    }

    /**
     * Renvoie la distance entre la balise de position i et la balise de
     * position j.
     */
    private double distance(int i, int j) {
        double distance = 0;
        Balise last = this.get(j);
        for (int k = i + 1; k <= j; k++) {
            Balise actual = this.get(k);
            distance += actual.distanceTo(last);
            last = actual;
        }
        return distance;
    }

    /**
     *  Renvoie le temps entre la balise de position i et la balise de
     *  position j.
     */
    private double time(int i, int j) {
        return this.get(j).getTime() - this.get(i).getTime();
    }
}