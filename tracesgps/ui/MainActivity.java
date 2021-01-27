package tracesgps.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import tracesgps.R;
import tracesgps.StdFileList;
import tracesgps.ui.download.DownloadActivity;
import tracesgps.ui.help.HelpActivity;
import tracesgps.ui.lost.LostActivity;
import tracesgps.ui.map.MapActivity;
import tracesgps.ui.settings.SettingsActivity;
import tracesgps.ui.statistics.StatisticsActivity;
import tracesgps.ui.track.TrackActivity;

/**
 * Activité principale de l'application.
 * @author Jennifer Viney, Ning Shi, Gledis Shkurti.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    // CONSTANTES

    private static final String TAG = "MainActivity";
    public static final String IS_NEW = "isNew";
    public static final String CLASS_NAME = "className";
    public static final String PATH_AUTO = "PathAutoActivity";
    public static final String CONNECTION = "ConnectionActivity";

    // ATTRIBUTS

    public static Boolean isConnectionMode;

    public static String Id;

    private EditText input;

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private Button[] buttons = new Button[5];
    private int[] buttonsId = {R.id.trackButton, R.id.pathAutoButton,
            R.id.connectionButton, R.id.statisticsButton,
            R.id.settingsButton};

    // COMMANDES

    /**
     * La fonction qui lance l'activité principale de l'application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer);

        isConnectionMode = false;

        //MENU
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        // BUTTONS
        buttonInitialise(buttons);

        // CHECK PERMISSION
        if (!checkPermissions()) {
            requestPermissions();
        }
        StdFileList.refresh();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    // MENU --------------------------------------------------------------------

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fonction qui démarre l'activité (la page) qui est choisie par
     * l'utilisateur.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.download:
                intent = new Intent(this, DownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.connection:
                isConnectionMode = true;
                idDialogBox();
                break;
            case R.id.help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.statistics:
                intent = new Intent(this,
                        StatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.lost:
                intent = new Intent(this, LostActivity.class);
                startActivity(intent);
                break;
            case R.id.map:
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.parcours_auto:
                intent = new Intent(this, PathActivity.class);
                intent.putExtra(CLASS_NAME, PATH_AUTO);
                intent.putExtra(IS_NEW, true);
                startActivity(intent);
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.suivi_trace:
                if (StdFileList.getPausedFile() != null) {
                    dialogBox();
                } else {
                    intent = new Intent(this, TrackActivity.class);
                    intent.putExtra(IS_NEW, true);
                    startActivity(intent);
                }
                break;
            default:
                return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fonction qui décide ce qu'il va se passer quand l'utilisateur veut venir
     * en arrière.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.trackButton:
                if (StdFileList.getPausedFile() != null) {
                    dialogBox();
                } else {
                    intent = new Intent(this, TrackActivity.class);
                    intent.putExtra(IS_NEW, true);
                    startActivity(intent);
                }
                break;
            case R.id.pathAutoButton:
                intent = new Intent(this, PathActivity.class);
                intent.putExtra(CLASS_NAME, PATH_AUTO);
                intent.putExtra(IS_NEW, true);
                startActivity(intent);
                break;
            case R.id.connectionButton:
                isConnectionMode = true;
                idDialogBox();
                break;
            case R.id.statisticsButton:
                intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.settingsButton:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    // OUTILS

    // BUTTONS -----------------------------------------------------------------

    /**
     * Initialise les boutons de la view.
     */
    private void buttonInitialise(Button[] buttons) {
        for(int i = 0; i < buttons.length; i++){
            buttons[i] = findViewById(buttonsId[i]);
            buttons[i].setOnClickListener(this);
        }
    }

    // PERMISSION --------------------------------------------------------------

    /**
     * Vérifie l'obtention des permissions demandées.
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
     * Demande des permissions.
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

    // DIALOGBOX ---------------------------------------------------------------

    /**
     * Création de la fenêtre de dialogue pour sélectionner un nouveau ou
     * continuer un enregistrement.
     */
    private void dialogBox() {
        new AlertDialog.Builder(this)
               .setTitle(getResources().getString(R.string.dialog_newContinue))
               .setMessage(getResources().getString(R.string.dialog_question))
               .setPositiveButton(
                       getResources().getString(R.string.dialog_continue),
                       new ContinueRecording())
               .setNegativeButton(
                       getResources().getString(R.string.dialog_new),
                       new NewRecording())
               .setNeutralButton(
                       getResources().getString(R.string.dialog_cancel),
                       new CancelDialog())
               .show();
    }

    /**
     * Création de la fenêtre de dialogue pour confirmer le choix de commencer
     * un nouvel enregistrement alors qu'il y en a un en pause.
     * S'il y a confirmation, l'enregistrement en pause est supprimé.
     */
    private void confirmDialogBox() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confdialog_title)
                .setMessage(R.string.confdialog_message)
                .setPositiveButton(R.string.confdialog_ok, new DeleteFile())
                .setNegativeButton(R.string.confdialog_cancel, new CancelDialog())
                .show();
    }

    /**
     * Création de la fenêtre de dialogue demandant le numéro du concurrent.
     */
    private void idDialogBox() {
        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.iddialog_title)
                .setMessage(R.string.iddialog_Message)
                .setView(input)
                .setPositiveButton(R.string.iddialog_ok, new PositiveButton())
                .setNegativeButton(R.string.iddialog_cancel, new CancelDialog())
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new OKDialog(dialog));
    }

    // LISTENERS ---------------------------------------------------------------

    /**
     * Classe qui permet de démarrer le mode suivi de traces pour continuer
     * un enregistrement quand cette option est séletionnée.
     */
    private class ContinueRecording implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(
                    getApplicationContext(), TrackActivity.class);
            intent.putExtra(IS_NEW, false);
            startActivity(intent);
        }
    }

    /**
     * Classe qui permet de démarrer le mode suivi de traces pour un nouvel
     * enregistrement quand cette option est sélectionnée.
     */
    private class NewRecording implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            confirmDialogBox();
        }
    }

    /**
     * Classe qui permet de démarrer le mode suivi de trace pour un nouvel
     * enregistrement avec suppression de l'enregistrement qui est en pause.
     */
    private class DeleteFile implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (StdFileList.getPausedFile().delete()) {
                Log.i(TAG, "Track file deleted : "
                        + StdFileList.getPausedFile().delete());
            } else {
                Log.i(TAG, "Track file not deleted: "
                        + StdFileList.getPausedFile().delete());
            }
            Intent intent = new Intent(
                    getApplicationContext(), TrackActivity.class);
            intent.putExtra(IS_NEW, true);
            startActivity(intent);
        }
    }

    /**
     * Classe qui permet de vérifier le contenu saisi et d'agir en conséquence.
     */
    private class OKDialog implements View.OnClickListener {

        private AlertDialog dialog;

        OKDialog(AlertDialog d) {
            dialog = d;
        }

        @Override
        public void onClick(View v) {
            boolean error;
            if (input.getText().toString().equals("")
                    || Integer.parseInt(input.getText().toString()) <= 0) {
                error = true;
            } else {
                error = false;
                Id = input.getText().toString();
                Intent intent = new Intent(MainActivity.this,
                        PathActivity.class);
                intent.putExtra(CLASS_NAME, CONNECTION);
                intent.putExtra(IS_NEW, true);
                startActivity(intent);
            }
            if (!error) {
                dialog.dismiss();
            }
        }
    }

    /**
     * Classe qui permet de fermer la fenêtre de dialogue.
     */
    private class CancelDialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    /**
     * Classe qui permet d'enregistrer un écouteur sur le bouton ("ok").
     */
    private class PositiveButton implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    }
}
