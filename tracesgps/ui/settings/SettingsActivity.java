package tracesgps.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

import tracesgps.DistanceUnit;
import tracesgps.FileExtension;
import tracesgps.MyDBHandler;
import tracesgps.Settings;
import tracesgps.R;
import tracesgps.SpeedUnit;
import tracesgps.TimeUnit;
import tracesgps.ui.WelcomeActivity;

/**
 * Classe qui permet d'afficher les paramètres liés à l’enregistrement d’un
 * parcours.
 * @author Gledis Shkurti.
 */
public class SettingsActivity extends AppCompatActivity {

    // ATTRIBUTS

    private static SharedPreferences sharedPreferences;
    private mySharedPreferenceChangeListener listener;
    private static ListPreference language;
    private static ListPreference fileExtention;
    private static ListPreference speed;
    private static ListPreference distance;
    private static ListPreference time;
    private static ListPreference formatMarkers;
    private static EditTextPreference nbBalises;
    private static EditTextPreference path;
    private static EditTextPreference locationIn;
    private static EditTextPreference accurancy;
    private static EditTextPreference accurancy2;
    private static EditTextPreference timeBalises;
    private static String languageString;
    private static String fileExtentionString;
    private static String speedString;
    private static String distanceString;
    private static String timeString;
    private static String formatMarkersString;

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Paramètres" (SettingsActivity).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_activity);
        toolbar.setLogo(R.drawable.ic_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences,
                false);
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        autoFill();
        listener = new mySharedPreferenceChangeListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        languageString = getLanguage();
        fileExtentionString = getFileExtention();
        speedString = getSpeedUnit();
        distanceString = getDistanceUnit();
        timeString = getTimeUnit();
        formatMarkersString = getDegreFormat();
    }

    /**
     * Enregistre les écouteurs de SharedPrefereces quand l'activité sort de la
     * pause.
     */
    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Supprime les écouteurs de SharedPrefereces quand l'activité est en pause.
     */
    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Classe qui remplit la vue de l'activité.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(
                Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            fileExtention = findPreference("fileExtention");
            speed = findPreference("speed");
            distance = findPreference("distance");
            time = findPreference("time");
            formatMarkers = findPreference("formatMarkers");
            language = findPreference("language");
            nbBalises = findPreference("nbBalises");
            path = findPreference("path");
            locationIn = findPreference("locationIn");
            accurancy = findPreference("accurancy");
            accurancy2 = findPreference("accurancy2");
            timeBalises = findPreference("timeBalises");

            nbBalises.setOnBindEditTextListener(
                    new myOnBindEditTextListener());
            locationIn.setOnBindEditTextListener(
                    new myOnBindEditTextListener());
            accurancy.setOnBindEditTextListener(
                    new myOnBindEditTextListener());
            accurancy2.setOnBindEditTextListener(
                    new myOnBindEditTextListener());
            timeBalises.setOnBindEditTextListener(
                    new myOnBindEditTextListener());

            java.util.Objects.requireNonNull(fileExtention)
                    .setValueIndex(Settings.getFileExtention().ordinal());
            java.util.Objects.requireNonNull(speed)
                    .setValueIndex(Settings.getSpeedUnit().ordinal());
            java.util.Objects.requireNonNull(distance)
                    .setValueIndex(Settings.getDistanceUnit().ordinal());
            java.util.Objects.requireNonNull(time)
                    .setValueIndex(Settings.getTimeUnit().ordinal());
            java.util.Objects.requireNonNull(formatMarkers)
                    .setValueIndex(Settings.getDegreFormat());
            java.util.Objects.requireNonNull(language)
                    .setValueIndex(languageToIndex());

            language.setSummary(languageString);
            fileExtention.setSummary(fileExtentionString);
            speed.setSummary(speedString);
            distance.setSummary(distanceString);
            time.setSummary(timeString);
            formatMarkers.setSummary(formatMarkersString);
            nbBalises.setSummary(
                    String.valueOf(Settings.getNbBalisesForSpeed()));
            path.setSummary(Settings.getPath());
            locationIn.setSummary(
                    String.valueOf(Settings.getLocationInterval()));
            accurancy.setSummary(
                    String.valueOf(Settings.getRadiusAccuracy()));
            accurancy2.setSummary(
                    String.valueOf(Settings.getAccuracy()));
            timeBalises.setSummary(
                    String.valueOf(Settings.getTimeBetweenTwoBalises()));
        }
    }

    // OUTILS

    /**
     * Classe qui regarde quels éléments sont selectionnés/entrés et qui en
     * fonction des ces éléments appelle les méthodes de la classe "Settings"
     * pour effectuer les valeurs choisie.
     */
    private class mySharedPreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            String s = "";
            MyDBHandler db = WelcomeActivity.getHandler();
            switch (key) {
                case "language":
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(
                                    R.string.fr_option))) {
                        setAppLocale(Locale.FRENCH.toString());
                        s = getResources().getString(R.string.fr_option);
                    }
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(
                                    R.string.en_option))) {
                        setAppLocale(Locale.ENGLISH.toString());
                        s = getResources().getString(R.string.en_option);
                    }
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(
                                    R.string.it_option))) {
                        setAppLocale(Locale.ITALIAN.toString());
                        s = getResources().getString(R.string.it_option);
                    }
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(
                                    R.string.ch_option))) {
                            setAppLocale(Locale.CHINESE.toString());
                       s = getResources().getString(R.string.ch_option);
                    }
                    languageString = s;
                    language.setSummary(s);
                    db.updateSettings();
                    restartActivity();
                    break;
                case "fileExtention":
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(R.string.json))) {
                        Settings.setFileExtention(FileExtension.JSON);
                        s = getResources().getString(R.string.json);
                    }
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(R.string.xml))) {
                        Settings.setFileExtention(FileExtension.XML);
                        s = getResources().getString(R.string.xml);
                    }
                    if (sharedPreferences.getString(key, "").
                            equals(getResources().getString(R.string.csv))) {
                        Settings.setFileExtention(FileExtension.CSV);
                        s = getResources().getString(R.string.csv);
                    }
                    fileExtentionString = s;
                    fileExtention.setSummary(s);
                    db.updateSettings();
                    break;
                case "speed":
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.ms))) {
                        Settings.setSpeedUnit(SpeedUnit.METERS_PER_SECOND);
                        s = getResources().getString(R.string.ms);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.kh))) {
                        Settings.setSpeedUnit(SpeedUnit.KILOMETERS_PER_HOUR);
                        s = getResources().getString(R.string.kh);
                    }
                    speedString = s;
                    speed.setSummary(s);
                    db.updateSettings();
                    break;
                case "distance":
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.m))) {
                        Settings.setDistanceUnit(DistanceUnit.METERS);
                        s = getResources().getString(R.string.m);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.km))) {
                            Settings.setDistanceUnit(DistanceUnit.KILOMETERS);
                        s = getResources().getString(R.string.km);
                    }
                    distanceString = s;
                    distance.setSummary(s);
                    db.updateSettings();
                    break;
                case "time":
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(
                                    R.string.milisec))) {
                        Settings.setTimeUnit(TimeUnit.MILLISECONDS);
                        s = getResources().getString(R.string.milisec);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.sec))) {
                        Settings.setTimeUnit(TimeUnit.SECONDS);
                        s = getResources().getString(R.string.sec);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.min))) {
                        Settings.setTimeUnit(TimeUnit.MINUTES);
                        s = getResources().getString(R.string.min);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.hour))) {
                        Settings.setTimeUnit(TimeUnit.HOURS);
                        s = getResources().getString(R.string.hour);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.days))) {
                        Settings.setTimeUnit(TimeUnit.DAYS);
                        s = getResources().getString(R.string.days);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.weeks))) {
                        Settings.setTimeUnit(TimeUnit.WEEKS);
                        s = getResources().getString(R.string.weeks);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(
                                    R.string.months))) {
                        Settings.setTimeUnit(TimeUnit.MONTHS);
                        s = getResources().getString(R.string.months);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.years))) {
                        Settings.setTimeUnit(TimeUnit.YEARS);
                        s = getResources().getString(R.string.years);
                    }
                    timeString = s;
                    time.setSummary(s);
                    db.updateSettings();
                    break;
                case "formatMarkers":
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(R.string.deg))) {
                        Settings.setDegreFormat(Location.FORMAT_DEGREES);
                        s = getResources().getString(R.string.deg);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(
                                    R.string.degmin))) {
                        Settings.setDegreFormat(Location.FORMAT_MINUTES);
                        s = getResources().getString(R.string.degmin);
                    }
                    if (sharedPreferences.getString(key, "")
                            .equals(getResources().getString(
                                    R.string.degminsec))) {
                        Settings.setDegreFormat(Location.FORMAT_SECONDS);
                        s = getResources().getString(R.string.degminsec);
                    }
                    formatMarkersString = s;
                    formatMarkers.setSummary(s);
                    db.updateSettings();
                    break;
                case "nbBalises":
                    if (!sharedPreferences.getString(
                            key, "").isEmpty()) {
                        int value = Integer.parseInt(sharedPreferences
                                .getString(key, ""));
                        if (value < 2) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.t_nbBalises),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Settings.setNbBalisesForSpeed(value);
                            nbBalises.setSummary(String.valueOf(value));
                            db.updateSettings();
                        }
                    }
                    break;
                case "path":
                    if (!sharedPreferences.getString(
                            key, "").isEmpty()) {
                        Settings.setPath(sharedPreferences
                                .getString(key, ""));
                        path.setSummary(sharedPreferences
                                .getString(key, ""));
                        db.updateSettings();
                    }
                    break;
                case "locationIn":
                    if (!sharedPreferences.getString(
                            key, "").isEmpty()) {
                        double value =
                                Double.parseDouble(sharedPreferences
                                        .getString(key, ""));
                        if (value <= 0) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.equals0),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Settings.setLocationInterval(
                                    Settings.formatToSystem(value,
                                            TimeUnit.MILLISECONDS));
                            locationIn.setSummary(String.valueOf(value));
                            db.updateSettings();
                        }
                    }
                    break;
                case "accurancy":
                    if (!sharedPreferences.getString(
                            key, "").isEmpty()) {
                        double value =
                                Double.parseDouble(sharedPreferences
                                        .getString(key, ""));
                        if (value < 0) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.equals02),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Settings.setRadiusAccuracy(Settings
                                    .formatToSystem(value,
                                            DistanceUnit.METERS));
                            accurancy.setSummary(String.valueOf(value));
                            db.updateSettings();
                        }
                    }
                    break;
                case "accurancy2":
                    if (!sharedPreferences.getString(
                            key, "").isEmpty()) {
                        int value = Integer.parseInt(sharedPreferences
                                .getString(key, ""));
                        if (value < 0) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.equals02),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Settings.setAccuracy(value);
                            accurancy2.setSummary(String.valueOf(value));
                            db.updateSettings();
                        }
                    }
                    break;
                case "timeBalises":
                    if (!sharedPreferences.getString(
                            key, "").isEmpty()) {
                        double value =
                                Double.parseDouble(sharedPreferences
                                        .getString(key, ""));
                        if (value == 0) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.equals03),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Settings.setTimeBetweenTwoBalises(
                                    Settings.formatToSystem(value,
                                            TimeUnit.SECONDS));
                            timeBalises.setSummary(String.valueOf(value));
                            db.updateSettings();
                        }
                    }
                    break;
                default:
            }
        }
    }

    /**
     * Remplit les champs de texte avec des valeurs par défaut.
     */
    private void autoFill() {
        SharedPreferences.Editor store = sharedPreferences.edit();
        store.putString("nbBalises",
                String.valueOf(Settings.getNbBalisesForSpeed()));
        store.putString("path",
                String.valueOf(Settings.getPath()));
        store.putString("locationIn",
                String.valueOf(Settings.getLocationInterval()));
        store.putString("accurancy",
                String.valueOf(Settings.getRadiusAccuracy()));
        store.putString("accurancy2",
                String.valueOf(Settings.getAccuracy()));
        store.putString("timeBalises",
                String.valueOf(Settings.getTimeBetweenTwoBalises()));
        store.apply();
    }

    /**
     * Fixe la langue de l'application.
     */
    private void setAppLocale(String localeCode) {
        Settings.setLanguage(localeCode);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    /**
     * Recommence l'activité.
     */
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * Renvoie l'indice de la langue dans la liste des langues.
     */
    private static int languageToIndex() {
        if (Settings.getLanguage().equals(Locale.FRENCH.toString())) {
            return 0;
        } else if (Settings.getLanguage().equals(Locale.ENGLISH.toString())) {
            return 1;
        } else if (Settings.getLanguage().equals(Locale.ITALIAN.toString())) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Fixe le type des entrées à NUMBER.
     */
    private static class myOnBindEditTextListener
            implements EditTextPreference.OnBindEditTextListener {
        @Override
        public void onBindEditText(EditText editText) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    /**
     * Renvoie la langue de l'application traduit dans la langue choisi
     * par utilisateur.
     */
    private String getLanguage() {
        switch (Settings.getLanguage()) {
            case "en" :
                return getResources().getString(R.string.en_option);
            case "fr" :
                return getResources().getString(R.string.fr_option);
            case "it" :
                return  getResources().getString(R.string.it_option);
            case "ch" :
                return getResources().getString(R.string.ch_option);
        }
        return null;
    }

    /**
     * Renvoie l'extention des fichiers traduit dans la langue choisi par
     * utilisateur.
     */
    private String getFileExtention() {
        switch (Settings.getFileExtention().toString()) {
            case ".xml" :
                return getResources().getString(R.string.xml);
            case ".csv" :
                return getResources().getString(R.string.csv);
            case ".json" :
                return getResources().getString(R.string.json);
        }
        return null;
    }

    /**
     * Renvoie l'unité de la vitesse traduit dans la langue choisi par
     * utilisateur.
     */
    private String getSpeedUnit() {
        switch (Settings.getSpeedUnit().toString()) {
            case "m/s" :
                return getResources().getString(R.string.ms);
            case "km/h" :
                return getResources().getString(R.string.kh);
        }
        return null;
    }

    /**
     * Renvoie l'unité de la distance traduit dans la langue choisi par
     * utilisateur.
     */
    private String getDistanceUnit() {
        switch (Settings.getDistanceUnit().toString()) {
            case "m" :
                return getResources().getString(R.string.m);
            case "km" :
                return getResources().getString(R.string.km);
        }
        return null;
    }

    /**
     * Renvoie l'unité du temps traduit dans la langue choisi par
     * utilisateur.
     */
    private String getTimeUnit() {
        switch (Settings.getTimeUnit().toString()) {
            case  "ms" :
                return getResources().getString(R.string.milisec);
            case "s" :
                return getResources().getString(R.string.sec);
            case "min" :
                return getResources().getString(R.string.min);
            case "h" :
                return getResources().getString(R.string.hour);
            case "days" :
                return getResources().getString(R.string.days);
            case "weeks" :
                return getResources().getString(R.string.weeks);
            case "years" :
                return getResources().getString(R.string.years);
            case "months" :
                return getResources().getString(R.string.months);
        }
        return null;
    }

    /**
     * Renvoie le format des coordonnées GPS traduit dans la langue choisi par
     * utilisateur.
     */
    private String getDegreFormat() {
        switch (Settings.getDegreFormat()) {
            case 0 :
                return getResources().getString(R.string.deg);
            case 1 :
                return getResources().getString(R.string.degmin);
            case 2 :
                return getResources().getString(R.string.degminsec);
        }
        return null;
    }
}