package tracesgps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Classe qui dérive "Service". Elle permet la récupération de la position de
 * l'appareil.
 * @author Jennifer Viney, Ning Shi.
 */

public class MyLocationService extends Service implements LocationListener {

    // CONSTANTES

    private final String TAG = "MyLocationService";
    private static final float LOCATION_DISTANCE = 0f;

    // ATTRIBUTS

    private static LocationManager mLocationManager;
    private LocationObserver locationObserver;
    private PropertyChangeSupport support;

    // COMMANDES

    /**
     * La fonction qui lance l'activité.
     */
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate LocationService");
        support = new PropertyChangeSupport(this);
        locationObserver = new LocationObserver();
        initializeLocationManager();
        try {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (mLocationManager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            (long) Settings.getLocationInterval(),
                            LOCATION_DISTANCE,
                            this
                    );
                }
                if (mLocationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            (long) Settings.getLocationInterval(),
                            LOCATION_DISTANCE,
                            this
                    );
                } else {
                    Log.i(TAG, "No provider disponible");
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "network provider does not exist, "
                    + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy LocationService");
        if (mLocationManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.removeUpdates(this);
            } catch (Exception e) {
                Log.e(TAG, "fail to remove location listener, ignore", e);
            }
        }
        Log.i(TAG, "MyLocationService Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand LocationService");
        try {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (mLocationManager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            (long) Settings.getLocationInterval(),
                            LOCATION_DISTANCE,
                            this
                    );
                    Log.i(TAG, "use network provider");
                }
                if (mLocationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            (long) Settings.getLocationInterval(),
                            LOCATION_DISTANCE,
                            this
                    );
                    Log.i(TAG, "use GPS provider");
                } else {
                    Log.i(TAG, "No provider disponible");
                }
            } else {
                Log.i(TAG, "Permissions denied");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "network provider does not exist, "
                    + e.getMessage());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        String time = new SimpleDateFormat("hh:mm:ss",
                Locale.getDefault()).format(location.getTime());
        support.firePropertyChange("location",
                null, location);
        Log.i(TAG, "onLocationChanged: " + location.getLatitude()
                + " " + location.getLongitude() + " " + time);
        locationObserver.setLastLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: " + provider);
    }

    // OUTILS

    /**
     * Initialisation du location manager.
     */
    private void initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager - LOCATION_INTERVAL: "
                + Settings.getLocationInterval()
                + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager =
                    (LocationManager) getApplicationContext().getSystemService(
                            Context.LOCATION_SERVICE);
        }
    }
}
