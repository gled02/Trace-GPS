package tracesgps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static tracesgps.MyApplication.getAppContext;
import static tracesgps.ui.MainActivity.Id;
import static tracesgps.ui.MainActivity.isConnectionMode;

/**
 * @author William Li, Jennifer Viney, Gledis Shkurti.
 */
public class StdTwoPaths implements TwoPaths {

    // CONSTANTES

    private static final String TAG = "StdTwoPaths";
    public static final String LAST_POINT = "lastPoint";
    static final String FOLLOWING_PATH = "followingPath";
    private static final String RECORDING_PATH = "recordingPath";

    // ATTRIBUTS

    public static boolean positionsSent;
    private ArrayList<Location> sendLater;
    private SaveToFile saver;
    private Parcours recordingPath;
    private Parcours followingPath;
    private State state;
    private PropertyChangeSupport support;
    private LocationObserver locationObserver;

    // CONSTRUCTEURS

    public StdTwoPaths(SaveToFile saver, LocationObserver locationObserver) {
        if (saver == null) {
            throw new AssertionError(
                    "Le saver ne doit pas être null");
        }
        this.saver = saver;
        recordingPath = saver.readFilePath();
        followingPath = null;
        state = State.RESUMED;
        support = new PropertyChangeSupport(this);
        this.locationObserver = locationObserver;
        locationObserver.addPropertyChangeListener(new TrackProperty());
        locationObserver.addPropertyChangeListener(new FollowPathProperty());
        locationObserver.addPropertyChangeListener(new FireProperty());

        sendLater = new ArrayList<>();
        setAppLocale(Settings.getLanguage());
    }

    // REQUETES

    @Override
    public SaveToFile getSaveToFile() {
        return saver;
    }

    @Override
    public Parcours getRecordingPath() {
        return recordingPath;
    }

    @Override
    public Parcours getFollowingPath() {
        return followingPath;
    }

    @Override
    public Location getLastLocation() {
        return locationObserver.getLastLocation();}

    @Override
    public String getInformationOfPathAuto() {
        Location actual = locationObserver.getLastLocation();
        String text = getAppContext().getText(R.string.next) + "\n";
        if (actual == null || recordingPath.size() == 0 ||
                getFollowingPath() == null || getFollowingPath().isEmpty()) {
            text += getAppContext().getString(R.string.direction) + "NaN\n";
            text += getAppContext().getString(R.string.distance) + "0.0"
                    + Settings.getDistanceUnit() + "\n";
            text += getAppContext().getString(R.string.delay) + "00:00:00";
        } else {
            double distance = actual.distanceTo(getFollowingPath().get(0))
                    / Settings.getDistanceUnit().toDouble();
            double azimuth = getAngle(
                    locationObserver.getLastLocation().getLongitude(),
                    locationObserver.getLastLocation().getLatitude(),
                    getFollowingPath().get(0).getLongitude(),
                    getFollowingPath().get(0).getLatitude());
            text += getAppContext().getString(R.string.direction) + Settings.applyAccuracy(azimuth) + "\n";
            text += getAppContext().getString(R.string.distance) + "" + Settings.applyAccuracy(distance)
                    + Settings.getDistanceUnit() + "\n";
            SimpleDateFormat time = new SimpleDateFormat(
                    "HH:mm:ss", Locale.getDefault());
            time.setTimeZone(TimeZone.getTimeZone("UTC"));
            text += getAppContext().getString(R.string.delay) + time.format(new Date(
                    Double.valueOf(distance /
                    getRecordingPath().getAverageSpeed(2))
                    .longValue()));
        }
        setAppLocale(Settings.getLanguage());
        return text;
    }

    @Override
    public String getInformation() {
        Location actual = locationObserver.getLastLocation();
        if (actual != null && recordingPath.size() > 0) {
            String text = getAppContext().getString(R.string.lat)
                    + Location.convert(
                    Settings.applyAccuracy(actual.getLatitude()),
                    Settings.getDegreFormat()) + "\n";
            text += getAppContext().getString(R.string.lon)
                    + Location.convert(
                    Settings.applyAccuracy(actual.getLongitude()),
                    Settings.getDegreFormat()) + "\n";
            double speed = Settings.applyAccuracy(Settings.systemToFormat(
                    recordingPath.getAverageSpeed(
                            Settings.getNbBalisesForSpeed()),
                    Settings.getSpeedUnit()));
            text += getAppContext().getString(R.string.speed) + speed +
                    Settings.getSpeedUnit().toString() + "\n";
            double distance = Settings.applyAccuracy(
                    Settings.systemToFormat(recordingPath.getTravelDistance(),
                            Settings.getDistanceUnit()));
            text += getAppContext().getString(R.string.distance) + distance +
                    Settings.getDistanceUnit().toString() + "\n";
            SimpleDateFormat time = new SimpleDateFormat(
                    "HH:mm:ss", Locale.getDefault());
            time.setTimeZone(TimeZone.getTimeZone("UTC"));
            text += getAppContext().getString(R.string.time) + time.format(new Date(
                    locationObserver.getLastLocation().getTime()
                            - recordingPath.get(0).getTime()));
            if (followingPath != null && followingPath.size() > 1) {
                text += "\n" + getAppContext().getString(R.string.start)
                        + Settings.applyAccuracy(Settings.systemToFormat(
                        locationObserver.getLastLocation().distanceTo(
                                followingPath.getLastBalise()),
                        Settings.getDistanceUnit()))
                        + Settings.getDistanceUnit().toString();
            }
            setAppLocale(Settings.getLanguage());
            return text;
        } else {
            String text = getAppContext().getString(R.string.lat) + "NaN\n";
            text += getAppContext().getString(R.string.lon) + "NaN\n";
            text += getAppContext().getString(R.string.speed) + "0" +
                    Settings.getSpeedUnit().toString() + "\n";
            text += getAppContext().getString(R.string.distance) + "0" +
                    Settings.getDistanceUnit().toString() + "\n";
            text += getAppContext().getString(R.string.time) + "00:00:00";
            setAppLocale(Settings.getLanguage());
            return text;
        }
    }

    @Override
    public boolean isResumed() {
        return state == State.RESUMED;
    }

    // COMMANDES

    @Override
    public void setFollowingPath(Parcours p) {
        followingPath = p;
    }

    @Override
    public void pause() {
        state = State.PAUSED;
        recordingPath.add(new Balise(locationObserver.getLastLocation()));
        saver.writeToFile(recordingPath);
        boolean res = saver.pause();
        if (!res) {
            Log.e(TAG, "Impossible de mettre le fichier en pause");
        }
    }

    @Override
    public void resume() {
        state = State.RESUMED;
        boolean res = saver.resume();
        if (!res) {
            Log.e(TAG, "Impossible de mettre le fichier en resume");
        }
    }

    @Override
    public void stop() {
        state = State.STOPPED;
        recordingPath.add(new Balise(locationObserver.getLastLocation()));
        saver.writeToFile(recordingPath);
        boolean res = saver.finish(recordingPath);
        if (!res) {
            Log.e(TAG, "Impossible de mettre le fichier en finish");
        }
        checkSendLater();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    // OUTILS

    /**
     * Méthode qui calcule l'angle entre deux coordonnées.
     */
    private double getAngle(double lon1, double lat1,
                            double lon2, double lat2) {
        double fLat = Math.PI * (lat1) / 180.0;
        double fLng = Math.PI * (lon1) / 180.0;
        double tLat = Math.PI * (lat2) / 180.0;
        double tLng = Math.PI * (lon2) / 180.0;
        double degree = (Math.atan2(Math.sin(tLng - fLng) * Math.cos(tLat),
                Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat)
                * Math.cos(tLat) * Math.cos(tLng - fLng))) * 180.0 / Math.PI;
        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }

    /**
     * Les informations sur la position de l'appareil sont envoyées vers le
     * serveur s'il y a une connexion internet, et sont stockées dans une liste
     * sinon.
     */
    private void sendPosition(Location location) {
        ConnectivityManager cm =
                (ConnectivityManager)getAppContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =
                Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        sendLater.add(location);
        try {
            if (sendLater.size() > 0 && isConnected) {
                for (Location l : sendLater) {
                    if (new SendPosition().execute(l).get()) {
                        sendLater.remove(l);
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie qu'il ne reste pas de position non envoyée, et notifie
     * l'utilisateur de tout problème.
     */
    private void checkSendLater() {
        positionsSent = true;
        ConnectivityManager cm =
                (ConnectivityManager)getAppContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =
                Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected && sendLater.size() > 0) {
            positionsSent = false;
            Toast.makeText(getAppContext(),
                    getAppContext().getString(R.string.no_internet),
                    Toast.LENGTH_LONG).show();
        } else {

            try {
                if (sendLater.size() > 0) {
                    for (Location l : sendLater) {
                        if (new SendPosition().execute(l).get()) {
                            sendLater.remove(l);
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                positionsSent = false;
                Toast.makeText(getAppContext(),
                        getAppContext().getString(R.string.error_send_position),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction qui fixe la langue de l'application.
     */
    private void setAppLocale(String localeCode) {
        Resources resources = getAppContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    // LISTENER ----------------------------------------------------------------

    /**
     * Classe qui gère l'enregistrement des balises pour le suivi de traces
     * et qui renvoie la position au serveur si l'utilisateur possède internet.
     */
    private class TrackProperty implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (state == State.RESUMED) {
                Balise b = new Balise(locationObserver.getLastLocation());
                if (recordingPath.size() == 0
                      || (b.getTime() - recordingPath.getLastBalise().getTime())
                      >= Settings.getTimeBetweenTwoBalises()) {
                    recordingPath.add(b);
                    saver.writeToFile(recordingPath);
                    if (isConnectionMode) {
                        sendPosition(locationObserver.getLastLocation());
                    }
                    support.firePropertyChange(RECORDING_PATH,
                            null, recordingPath);
                }
            }
        }
    }

    /**
     * Classe qui gère le suivi d'un parcours.
     */
    private class FollowPathProperty implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (state == State.RESUMED && followingPath != null) {
                if (followingPath.size() == 0) {
                    support.firePropertyChange(LAST_POINT,
                            null, null);
                }
                while (followingPath.size() > 0 && followingPath.get(0)
                        .isValidate(locationObserver.getLastLocation())) {
                    followingPath.remove(0);
                    support.firePropertyChange(FOLLOWING_PATH,
                            null, followingPath);
                }
            }
        }
    }

    /**
     * Classe qui notifie quand la propriété a changé de valeur.
     */
    private class FireProperty implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            support.firePropertyChange(evt);
        }
    }

    // SEND POSITION -----------------------------------------------------------

    /**
     * Thread qui envoie les informations sur la position de l'appareil au
     * serveur.
     */
    @SuppressLint("StaticFieldLeak")
    private class SendPosition extends AsyncTask<Location, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Location... locations) {
            for (Location location : locations) {
                SimpleDateFormat time = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String request =
                        "https://tracegps-2020.herokuapp.com/setgps?id="
                        + Id + "&date="
                        + time.format(location.getTime())
                        + "&lat=" + location.getLatitude()
                        + "&lon=" + location.getLongitude();
                try {
                    URL url = new URL(request);
                    HttpURLConnection conn =
                            (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.i(TAG, "position sent");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "SendPosition: " + "error");
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }
}
