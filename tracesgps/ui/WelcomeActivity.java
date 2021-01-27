package tracesgps.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

import tracesgps.MyDBHandler;
import tracesgps.MyLocationService;
import tracesgps.Settings;
import tracesgps.R;

/**
 * Classe qui permet l'affichage d'une page lors du lancement de l'application.
 * @author Ning Shi, Gledis Shkurti.
 */
public class WelcomeActivity extends AppCompatActivity {

    // CONSTANTES

    private final String TAG = "WelcomeActivity";

    // ATTRIBUTS

    private static MyDBHandler db;

    // COMMANDES

    /**
     * La fonction qui lance l'activité.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Welcome Activity");
        setContentView(R.layout.layout_welcome);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.logo_transparent);

        if (!checkPermissions()) {
            requestPermissions();
            db = new MyDBHandler(this);
            db.addSettings();
            db.readSettings();
        } else {
            Log.i(TAG, "startService");
            startService(new Intent(this,
                    MyLocationService.class));
            new Handler().postDelayed(new myRunnable(),3000);
            db = new MyDBHandler(this);
            db.readSettings();
        }
        setAppLocale(Settings.getLanguage());
    }

    /**
     * Fonction qui commence le service quand les permissions sont accordées.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        int PERMISSION_ID = 44;
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "In onRequestPermissionResult");
                Log.i(TAG, "StartService");
                startService(new Intent(this,
                        MyLocationService.class));
                startActivity(new Intent(WelcomeActivity.this,
                        MainActivity.class));
            }
        }
    }

    /**
     * Renvoie la base de données nommée settingsDB.db
     */
    public static MyDBHandler getHandler() {
        return db;
    }

    // OUTILS

    /**
     * Vérifier l'obtention des permissions du portable.
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
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS)
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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.SEND_SMS
                },
                PERMISSION_ID);
    }

    /**
     * Permet d'exécuter MainActivity.
     */
    private class myRunnable implements Runnable {
        public void run() {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Fixe la langue de l'application.
     */
    private void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }
}
