package tracesgps;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.Locale;
import java.util.Objects;

/**
 * Classe quie permet d'implémenter une base de données nommée "settingsDB.db"
 * pour sauvegarder tous les paramètres choisis par l’utilisateur.
 * @author Gledis Shkurti.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    // ATTRIBUTS

    /**
     * Les informations contenues dans la base de données.
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "settingsDB.db";
    private static final String TABLE_NAME = "Settings";
    private static final String KEY_ID = "id";
    private static final String LANGUAGE = "Language";
    private static final String SPEED_UNIT = "SpeedUnit";
    private static final String DISTANCE_UNIT = "DistanceUnit ";
    private static final String TIME_UNIT = "TimeUnit";
    private static final String PATH = "PathText";
    private static final String ACCURACY = "Accuracy";
    private static final String NBBALISES_FORSPEED = "NbBalisesForSpeed";
    private static final String FILE_EXTENTION = "FileExtention";
    private static final String LOCATION_INTERVAL = "LocationInterval";
    private static final String RADIUS_ACCURANCY = "RadiusAccuracy";
    private static final String DEGRE_FORMAT = "DegreFormat";
    private static final String TIMEBETWEEN_TWOBALISES
                                                    = "TimeBetweenTwoBalises";

    // CONSTRUCTEURS

    /**
     * Création de la base de données.
     */
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // COMMANDES

    /**
     * Méthode qui permet la création d'une table dans la base de données.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
        + KEY_ID + " INTEGER PRIMARY KEY,"
        + LANGUAGE + " TEXT," + SPEED_UNIT + " TEXT," + DISTANCE_UNIT + " TEXT,"
        + TIME_UNIT + " TEXT," + PATH + " TEXT," + ACCURACY + " INTEGER,"
        + NBBALISES_FORSPEED + " INTEGER," + FILE_EXTENTION + " TEXT,"
        + LOCATION_INTERVAL + " TEXT," + RADIUS_ACCURANCY + " TEXT,"
        + DEGRE_FORMAT + " INTEGER," + TIMEBETWEEN_TWOBALISES + " TEXT" + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);
    }

    /**
     * Méthode qui autorise les changements dans la base de données.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Méthode qui permet de remplir la table dans la base de données avec des
     * valeurs par défaut. (Insérer des lignes.)
     */
    public void addSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LANGUAGE, Locale.getDefault().getLanguage());
        values.put(SPEED_UNIT, SpeedUnit.KILOMETERS_PER_HOUR.toString());
        values.put(DISTANCE_UNIT, DistanceUnit.KILOMETERS.toString());
        values.put(TIME_UNIT, TimeUnit.HOURS.toString());
        values.put(PATH, Objects.requireNonNull(MyApplication
                .getAppContext().getExternalFilesDir("Tracks")).getPath());
        values.put(ACCURACY, 3);
        values.put(NBBALISES_FORSPEED, 2);
        values.put(FILE_EXTENTION, FileExtension.XML.toString());
        values.put(LOCATION_INTERVAL,
                String.valueOf(800 * TimeUnit.MILLISECONDS.toDouble()));
        values.put(RADIUS_ACCURANCY,
                String.valueOf(10 * DistanceUnit.METERS.toDouble()));
        values.put(DEGRE_FORMAT, Location.FORMAT_DEGREES);
        values.put(TIMEBETWEEN_TWOBALISES,
                String.valueOf(60 * TimeUnit.SECONDS.toDouble()));
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Méthode qui remplit la classe SETTINGS avec les valeurs sauvegardées
     * dans la table de la base de données.
     */
    public void readSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] allColumns = {
                KEY_ID, LANGUAGE, SPEED_UNIT, DISTANCE_UNIT,
                TIME_UNIT, PATH, ACCURACY, NBBALISES_FORSPEED, FILE_EXTENTION,
                LOCATION_INTERVAL, RADIUS_ACCURANCY, DEGRE_FORMAT,
                TIMEBETWEEN_TWOBALISES};
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME, allColumns,
                KEY_ID + " = ?",
                new String[]{String.valueOf(1)},null,null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Settings.setLanguage(cursor.getString(1));
            Settings.setSpeedUnit(SpeedUnit.findValue(
                    cursor.getString(2)));
            Settings.setDistanceUnit(DistanceUnit.findValue(
                    cursor.getString(3)));
            Settings.setTimeUnit(TimeUnit.findValue(
                    cursor.getString(4)));
            Settings.setPath(cursor.getString(5));
            Settings.setAccuracy(cursor.getInt(6));
            Settings.setNbBalisesForSpeed(cursor.getInt(7));
            Settings.setFileExtention(FileExtension.findValue(
                    cursor.getString(8)));
            Settings.setLocationInterval(Double.valueOf(
                    cursor.getString(9)));
            Settings.setRadiusAccuracy(
                    Double.valueOf(cursor.getString(10)));
            Settings.setDegreFormat(cursor.getInt(11));
            Settings.setTimeBetweenTwoBalises(
                    Double.valueOf(cursor.getString(12)));
        }
    }

    /**
     * Méthode qui met à jour la table de la base de données en fonction des
     * valeurs changées.
     */
    public void updateSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LANGUAGE, Settings.getLanguage());
        values.put(SPEED_UNIT, Settings.getSpeedUnit().toString());
        values.put(DISTANCE_UNIT, Settings.getDistanceUnit().toString());
        values.put(TIME_UNIT, Settings.getTimeUnit().toString());
        values.put(PATH, Settings.getPath());
        values.put(ACCURACY, Settings.getAccuracy());
        values.put(NBBALISES_FORSPEED, Settings.getNbBalisesForSpeed());
        values.put(FILE_EXTENTION, Settings.getFileExtention().toString());
        values.put(LOCATION_INTERVAL,
                String.valueOf(Settings.getLocationInterval()));
        values.put(RADIUS_ACCURANCY,
                String.valueOf(Settings.getRadiusAccuracy()));
        values.put(DEGRE_FORMAT, Settings.getDegreFormat());
        values.put(TIMEBETWEEN_TWOBALISES,
                String.valueOf(Settings.getTimeBetweenTwoBalises()));
        db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(1)});
    }
}