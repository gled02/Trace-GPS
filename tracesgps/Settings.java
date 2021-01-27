package tracesgps;

/**
 * Classe abstraite qui permet de définir les paramètres de l'application.
 * @author William Li.
 */
public abstract class Settings {

    // ATTRIBUTS

    private static String language;
    private static SpeedUnit speedUnit;
    private static DistanceUnit distanceUnit;
    private static TimeUnit timeUnit;
    private static String path;
    private static int accuracy;
    private static int nbBalisesForSpeed;
    private static FileExtension fileExtention;
    private static double locationInterval;
    private static double radiusAccuracy;
    private static int degreFormat;
    private static double timeBetweenTwoBalises;

    // REQUETES

    /**
     * Renvoie la langue définie par l'utilisateur.
     */
    public static String getLanguage() {
        return language;
    }

    /**
     * Renvoie l'unité de vitesse définie par l'utilisateur.
     */
    public static SpeedUnit getSpeedUnit() {
        return speedUnit;
    }

    /**
     * Renvoie l'unité de la distance définie par l'utilisateur.
     */
    public static DistanceUnit getDistanceUnit() {
        return distanceUnit;
    }

    /**
     * Renvoie l'unité de temps définie par l'utilisateur.
     */
    public static TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Renvoie l'emplacement des fichiers générés définie par l'utilisateur.
     */
    public static String getPath() {
        return path;
    }

    /**
     * Renvoie la précision affichée des différentes valeurs définie par
     * l'utilisateur.
     */
    public static int getAccuracy() {
        return accuracy;
    }

    /**
     * Renvoie le nombre de balises à considérer pour calculer la vitesse défini
     * par l'utilisateur.
     */
    public static int getNbBalisesForSpeed() {
        return nbBalisesForSpeed;
    }

    /**
     * Renvoie l'extention des fichiers générés définie par l'utilisateur.
     */
    public static FileExtension getFileExtention() {
        return fileExtention;
    }

    /**
     * Renvoie l'intervalle de temps entre 2 demandes de localisation exprimé en
     * millisecondes défini par l'utilisateur.
     */
    public static double getLocationInterval() {
        return locationInterval;
    }

    /**
     * Renvoie la distance à laquelle on considère être arrivé
     * à une balise (en mètre).
     */
    public static double getRadiusAccuracy() {
        return radiusAccuracy;
    }

    /**
     * Renvoie le format d'affichage des degrés dans l'application.
     */
    public static int getDegreFormat() {
        return degreFormat;
    }

    /**
     * Renvoie le temps entre l'enregistrement de 2 balises.
     */
    public static double getTimeBetweenTwoBalises() {
        return timeBetweenTwoBalises;
    }

    // COMMANDES

    /**
     * Change la langue du système.
     * <post>
     *      getLanguage() == s </post>
     */
    public static void setLanguage(String s) {
        language = s;
    }

    /**
     * Change l'unité de vitesse du système.
     * <post>
     *      getSpeedUnit() == unit </post>
     */
    public static void setSpeedUnit(SpeedUnit unit) {
        speedUnit = unit;
    }

    /**
     * Change l'unité de distance du système.
     * <post>
     *       getDistanceUnit() == unit </post>
     */
    public static void setDistanceUnit(DistanceUnit unit) {
        distanceUnit = unit;
    }

    /**
     * Change l'unité de temps du système.
     * <post>
     *      getTimeUnit() == unit </post>
     */
    public static void setTimeUnit(TimeUnit unit) {
        timeUnit = unit;
    }

    /**
     * Change l'emplacement des fichiers du système.
     * <post>
     *      getPath() == s </post>
     */
    public static void setPath(String s) {
        path = s;
    }

    /**
     * Change la précision affichée des différentes valeurs du système.
     * <post>
     *      getAccuracy() == value </post>
     */
    public static void setAccuracy(int accuracy) {
        Settings.accuracy = accuracy;
    }

    /**
     * Change le nombre de balises à considérer pour calculer la vitesse du
     * système.
     * <post>
     *     getNbBalisesForSpeed() == nbBalisesForSpeed </post>
     */
    public static void setNbBalisesForSpeed(int nbBalisesForSpeed) {
        Settings.nbBalisesForSpeed = nbBalisesForSpeed;
    }

    /**
     * Change l'extention des fichiers générés du système.
     * <post>
     *      getFileExtention() == fileExtension </post>
     */
    public static void setFileExtention(FileExtension fileExtension) {
        Settings.fileExtention = fileExtension;
    }

    /**
     * Change l'intervalle de temps entre 2 demandes de localisation exprimé en
     * millisecondes du système.
     * <post>
     *      getLocationInterval() == locationInterval </post>
     */
    public static void setLocationInterval(double locationInterval) {
        Settings.locationInterval = locationInterval;
    }

    /**
     * Change la distance à laquelle on considère être arrivé à
     * une balise (en mètre).
     * <post>
     *      getRadiusAccuracy() == radiusAccuracy </post>
     */
    public static void setRadiusAccuracy(double radiusAccuracy) {
        Settings.radiusAccuracy = radiusAccuracy;
    }

    /**
     * Change le format pour l'affichage des degrés dans l'application.
     * <post>
     *      getDegreFormat() == degreFormat </post>
     */
    public static void setDegreFormat(int degreFormat) {
        Settings.degreFormat = degreFormat;
    }

    /**
     * Change le temps entre l'enregistrement de 2 balises.
     * <post>
     *      setTimeBetweenTwoBalises() == timeBetweenTwoBalises </post>
     */
    public static void setTimeBetweenTwoBalises(double timeBetweenTwoBalises) {
        Settings.timeBetweenTwoBalises = timeBetweenTwoBalises;
    }

    /**
     * Converti la valeur de l'unité time, en unité de l'application unit.
     * (millisecondes)
     */
    public static double formatToSystem(double time, TimeUnit unit) {
        return time * unit.toDouble();
    }

    /**
     * Converti la valeur de l'unité distance, en unité de l'application unit.
     * (mètre)
     */
    public static double formatToSystem(double distance, DistanceUnit unit) {
        return distance * unit.toDouble();
    }

    /**
     * Converti la valeur fournie par l'application vers l'unité distance.
     */
    static double systemToFormat(double distance, DistanceUnit unit) {
        return distance / unit.toDouble();
    }

    /**
     * Converti la valeur fournie par l'application vers l'unité speed.
     */
    static double systemToFormat(double speed, SpeedUnit unit) {
        return speed / unit.toDouble();
    }

    /**
     * Arrondi la valeur prise en argument selon le choix d'utilisateur.
     */
    public static double applyAccuracy(double value) {
        double n = Math.pow(10, accuracy);
        return Math.round(value * n) / n;
    }
}
