package tracesgps.ui.lost;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import tracesgps.LocationObserver;
import tracesgps.R;
import tracesgps.Settings;

import static tracesgps.MyApplication.getAppContext;

/**
 * Classe qui permet à l'utilisateur d’envoyer un SMS avec les coordonnées GPS
 * actuelles quand il est perdu.
 * @author Gledis Shkurti.
 */
public class LostActivity extends AppCompatActivity {

    // ATTRIBUTS

    private LocationObserver location;

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Perdu" (LostActivity).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lost);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lost_activity);
        toolbar.setLogo(R.drawable.ic_lost);
        setSupportActionBar(toolbar);
        location = new LocationObserver();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sendSMS();
    }

    // OUTILS

    /**
     * Fonction qui permet d'envoyer un SMS avec les coordonnées GPS actuelles.
     */
    private void sendSMS() {
        if (location.getLastLocation() != null) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setDataAndType(Uri.parse("smsto:"), "");
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "");
            smsIntent.putExtra("sms_body",
                    getResources().getString(R.string.sms) + "\n"
                            + getResources().getString(R.string.sms2) + "\n"
                            + "Lat : " + Location.convert(
                            Settings.applyAccuracy(
                                    location.getLastLocation().getLatitude()),
                            Settings.getDegreFormat()) + "\n"
                            + "Lon : " + Location.convert(
                            Settings.applyAccuracy(
                                    location.getLastLocation().getLongitude()),
                            Settings.getDegreFormat()));
            try {
                startActivity(smsIntent);
                finish();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(LostActivity.this,
                        getResources().getString(R.string.sms_failed),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getAppContext(),
                    getResources().getString(R.string.notGPS),
                    Toast.LENGTH_LONG).show();
        }
    }
}
