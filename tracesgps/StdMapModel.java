package tracesgps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static tracesgps.StdTwoPaths.FOLLOWING_PATH;
import static tracesgps.ui.MainActivity.IS_NEW;

/**
 * Classe de model pour afficher et gérer l'affichage du carte
 * @author Ning Shi, William Li, Gledis Shkurti, Jennifer Viney.
 */
public abstract class StdMapModel extends AppCompatActivity {

    // CONSTANTES

    private static final String TAG = "StdMapModel";

    // ATTRIBUTS

    protected TwoPaths model;
    private MapView map;
    private Polyline line = new Polyline();
    private List<Marker> markers = new ArrayList<>();
    private LocationObserver locationObserver;

    // COMMANDES

    /**
     * La fonction qui lance l'activité.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "After onCreate()");
        locationObserver = new LocationObserver();
        // CHECK PERMISSIONs
        if (!checkPermissions()) {
            Log.i(TAG, "No checkPermission");
            requestPermissions();
        }

        //DISPLAY MAP
        Configuration.getInstance().setUserAgentValue(
                BuildConfig.APPLICATION_ID);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,
                PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map_view);
        if (map == null) {
            Log.e(TAG, "map is null");
        }
        IMapController mapController = map.getController();
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(3.0);
        map.setHorizontalMapRepetitionEnabled(true);
        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(
                MapView.getTileSystem().getMaxLatitude(),
                MapView.getTileSystem().getMinLatitude(), 0);
        map.getController().setZoom(20.0);

        // COMPASS
        CompassOverlay mCompassOverlay = new CompassOverlay(this,
                new InternalCompassOrientationProvider(
                        getApplicationContext()), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);

        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //LOCATION
        GpsMyLocationProvider t = new GpsMyLocationProvider(
                getApplicationContext());
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(t, map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        if (locationObserver.getLastLocation() != null) {
            IGeoPoint startPoint = new GeoPoint(
                    locationObserver.getLastLocation());
            mapController.setCenter(startPoint);
        }

        //MODEL
        if (getIntent().getBooleanExtra(IS_NEW, false)) {
            model = new StdTwoPaths(new StdSaveToFile(), locationObserver);
        } else {
            File pausedFile = StdFileList.getPausedFile();
            if (pausedFile != null) {
                model = new StdTwoPaths(new StdSaveToFile(pausedFile),
                        locationObserver);
                model.resume();
            }
        }
        if (model != null) {
            model.addPropertyChangeListener(new trackPosition());
            model.addPropertyChangeListener(new followPath());
        }

        //CENTER BUTTON
        ImageButton centerButton = findViewById(R.id.center_button);
        centerButton.setOnClickListener(new zoom());
    }

    @Override
    public void onResume(){
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        map.onPause();
    }

    // OUTILS

    //DISPLAY PARCOURS ---------------------------------------------------------

    /**
     * Supprime un marquer après valider un point
     */
    private void removeCheckpoint() {
        if (!line.getPoints().isEmpty()) {
            List<GeoPoint> tmp = line.getPoints();
            tmp.remove(line.getPoints().size() - 1);
            line.setPoints(tmp);
        }
        if (!markers.isEmpty()) {
            Marker m = markers.get(markers.size() - 1);
            map.getOverlayManager().remove(m);
            markers.remove(m);
        }
        if (!markers.isEmpty()) {
            markers.get(markers.size() - 1).setIcon(getResources().getDrawable(
                    R.drawable.next_point_marker));
        }
        map.invalidate();
    }

    /**
     * Dessine le parcours sur la carte.
     */
    protected void drawPath(Parcours parcours) {
        if (parcours != null) {
            map.getOverlays().remove(line);
            map.getOverlays().removeAll(markers);
            markers.clear();
            List<GeoPoint> l = new ArrayList<>();
            for (int i = parcours.size() - 1; i >= 0; --i) {
                GeoPoint p = new GeoPoint(parcours.get(i));
                Marker marker = new Marker(map);
                if (i == 0) {
                    marker.setIcon(getResources().getDrawable(
                            R.drawable.next_point_marker));
                } else {
                    marker.setIcon(getResources().getDrawable(
                            R.drawable.marker));
                }
                marker.setPosition(p);
                markers.add(marker);
                l.add(p);
            }
            line.setPoints(l);
            map.getOverlayManager().add(line);
            map.getOverlays().addAll(markers);
            map.invalidate();
        }
    }

    // PERMISSION --------------------------------------------------------------

    /**
     * Vérifie l'obtention des permissions de portable
     */
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Demande des permissions du portable.
     */
    private void requestPermissions() {
        int PERMISSION_ID = 44;
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSION_ID);
    }

    // LISTENERS ---------------------------------------------------------------

    /**
     * Cette classe permet de centrer la position de l'appareil sur l'écran.
     */
    private class trackPosition implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            map.getController().setCenter(new GeoPoint(
                    locationObserver.getLastLocation()));
            model.removePropertyChangeListener(this);
        }
    }

    /**
     * Cette classe permet de notifier l'utilisateur que le balise a été validé.
     */
    private class followPath implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (model.isResumed()) {
                if (evt.getPropertyName().equals(FOLLOWING_PATH)) {
                    removeCheckpoint();
                    Toast.makeText(getApplicationContext(),
                            R.string.validated, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Cette classe permet de centré la position de l'appareil sur l'écran avec
     * zoom.
     */
    private class zoom implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (locationObserver.getLastLocation() != null) {
                map.getController().setCenter(new GeoPoint(
                        locationObserver.getLastLocation()));
                map.getController().setZoom(20.0);
            }
        }
    }
}
