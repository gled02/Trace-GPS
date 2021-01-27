package tracesgps.ui.track;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import tracesgps.Parcours;
import tracesgps.R;
import tracesgps.StdMapModel;

import static tracesgps.MyApplication.getAppContext;

/**
 * Classe qui permet l'affichage de la carte pour le mode suivi de trace.
 * @author Ning Shi, Jennifer Viney, Gledis Shkurti.
 */
public class TrackActivity extends StdMapModel {

    // ATTRIBUTS

    private TextView text;
    private TextView information;
    private BottomSheetBehavior bottomSheetBehavior;

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Suivi de traces".
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_map);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.track_activity);
        toolbar.setLogo(R.drawable.ic_trace);
        setSupportActionBar(toolbar);

        text = findViewById(R.id.file);

        //INFORMATION
        information = findViewById(R.id.information1);
        information.setText(model.getInformation());

        model.addPropertyChangeListener(new RefreshInfo());

        initialiseBottomSheet();
        initialiseButtons();
        if (model.getLastLocation() == null) {
            findViewById(R.id.file_button).setVisibility(View.INVISIBLE);
            Toast.makeText(getAppContext(),
                    getResources().getString(R.string.notGPS),
                    Toast.LENGTH_LONG).show();
        }
    }

    // TOOLBAR -----------------------------------------------------------------

    /**
     * Ajoute des éléments à la barre d’action, si elle existe.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menubar_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (model.getLastLocation() == null) {
                    dialogBoxExit();
                } else {
                    dialogBox();
                }
                break;
            case R.id.reverse:
                if (model.getLastLocation() != null) {
                    dialogBoxReverse();
                } else {
                    findViewById(R.id.reverse).setActivated(false);
                }
                break;
        }
        return true;
    }

    // BACK PRESSED ------------------------------------------------------------

    /**
     * Fonction qui décide ce qu'il va se passer quand l'utilisateur veut venir
     * en arrière.
     */
    @Override
    public void onBackPressed() {
        if (model.getLastLocation() == null) {
            dialogBoxExit();
        } else {
            dialogBox();
        }
    }

    // INITIALISE --------------------------------------------------------------

    /**
     * Initialise le BottomSheet.
     */
    private void initialiseBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(
                new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setPeekHeight(0);
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    /**
     * Intialise les boutons de la view.
     */
    private void initialiseButtons() {
        ImageButton fileButton = findViewById(R.id.file_button);
        Button loadButton = findViewById(R.id.load_button);
        fileButton.setVisibility(View.VISIBLE);
        fileButton.setOnClickListener(new ShowBottomSheet());
        loadButton.setOnClickListener(new Refresh());
    }

    // DIALOGBOX ---------------------------------------------------------------

    /**
     * Création de la fenêtre de dialogue pour choisir comment finir
     * l'enregistrement.
     */
    private void dialogBox() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_pause_button,
                        new PauseRecording())
                .setNegativeButton(R.string.dialog_stop_button,
                        new StopRecording())
                .setNeutralButton(R.string.dialog_cancel_button,
                        new CancelDialog())
                .show();
    }

    /**
     * Création de la fenêtre de dialogue pour choisir la façon d'afficher le
     * parcours inverse.
     */
    private void dialogBoxReverse() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.rvdialog_title)
                .setMessage(R.string.rvdialog_message)
                .setPositiveButton(R.string.rvdialog_birdEyeView_button,
                        new ReversePath(
                               model.getRecordingPath().goToStartDirectly()))
                .setNegativeButton(R.string.rvdialog_reverse_button,
                        new ReversePath(
                               model.getRecordingPath().goToStartReversePath()))
                .setNeutralButton(R.string.rvdialog_cancel_button,
                        new CancelDialog())
                .show();
    }

    /**
     * Création de la fenêtre de dialogue si la dernière position vaut null.
     */
    private void dialogBoxExit() {
        new AlertDialog.Builder(TrackActivity.this)
                .setTitle(R.string.exitdialog_title)
                .setMessage(R.string.exitdialog_message)
                .setPositiveButton(R.string.exitdialog_yes, new Exit())
                .setNegativeButton(R.string.exitdialog_no, new CancelDialog())
                .show();
    }

    // LISTENER ----------------------------------------------------------------

    /**
     * Classe qui permet de développer le BottomSheet.
     */
    private class ShowBottomSheet implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            bottomSheetBehavior.setState(
                    BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * Classe qui permet de rafraîchir l'affichage du contenu de
     * l'enregistrement.
     */
    private class Refresh implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            text.setText(model.getSaveToFile().readFile());
        }
    }

    /**
     * Classe qui permet de mettre en pause l'enregistrement et de revenir à
     * l'activité précédente.
     */
    private class PauseRecording implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                model.pause();
                TrackActivity.super.onBackPressed();
        }
    }

    /**
     * Classe qui permet d'arrêter l'enregistrement et de revenir à
     * l'activité précédente.
     */
    private class StopRecording implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            model.stop();
            TrackActivity.super.onBackPressed();
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
     * Classe qui permet de revenir au menu principal.
     */
    private class Exit implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            TrackActivity.super.onBackPressed();
        }
    }

    /**
     * Classe qui permet d'afficher le parcours p sur la carte.
     */
    private class ReversePath implements DialogInterface.OnClickListener {

        private Parcours p;

        ReversePath(Parcours p) {
            this.p = p;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            drawPath(p);
            model.setFollowingPath(p);
        }
    }

    /**
     * Cette classe permet de mettre à jour les informations qui sont affichées.
     */
    private class RefreshInfo implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            information.setText(model.getInformation());
        }
    }
}
