package tracesgps.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import tracesgps.FileUtils;
import tracesgps.Parcours;
import tracesgps.R;
import tracesgps.SaveToFile;
import tracesgps.StdMapModel;
import tracesgps.StdSaveToFile;

import static tracesgps.StdTwoPaths.LAST_POINT;
import static tracesgps.StdTwoPaths.positionsSent;
import static tracesgps.ui.MainActivity.CLASS_NAME;
import static tracesgps.ui.MainActivity.CONNECTION;
import static tracesgps.ui.MainActivity.PATH_AUTO;

/**
 * Classe qui permet l'affichage de la carte selon le mode sélectionné
 * (parcours autonome ou parcours connecté).
 * @author Ning Shi, Jennifer Viney.
 */
public class PathActivity extends StdMapModel {

    // CONSTANTES

    private String TAG = "PathActivity";
    private static final int FILE_SELECT_CODE = 0;

    // ATTRIBUTS

    private boolean isFileSelected;
    private boolean isPathFinished;
    private TextView information;
    private TextView info;
    private BottomSheetBehavior bottomSheetBehavior;
    private Bundle extras;

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Parcours Autonome".
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_map);
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();

        // Activity title
        Toolbar toolbar = findViewById(R.id.toolbar);
        String className = extras.getString(CLASS_NAME);
        if (className != null) {
            switch (className) {
                case PATH_AUTO:
                    toolbar.setTitle(R.string.path_auto_activity);
                    toolbar.setLogo(R.drawable.ic_path2_white);
                    break;
                case CONNECTION :
                    toolbar.setTitle(R.string.connection_activity);
                    toolbar.setLogo(R.drawable.ic_path_white);
                    break;
            }
            setSupportActionBar(toolbar);
        } else {
            Log.e(TAG, "null class name");
        }

        isPathFinished = false;

        initialiseBottomSheet();

        //INFORMATION
        information = findViewById(R.id.information1);
        info = findViewById(R.id.information2);
        information.setText(model.getInformationOfPathAuto());
        info.setVisibility(View.VISIBLE);
        info.setText(model.getInformation());

        model.addPropertyChangeListener(new RefreshInfo());
        showFileChooser();
        findViewById(R.id.file_button).setVisibility(View.INVISIBLE);
    }

    /**
     * Quand le client a choisi le fichier, le resultat est renvoyé ici.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            isFileSelected = true;
            Uri uri = data.getData();
            Log.e(TAG, "File Uri: "
                    + Objects.requireNonNull(uri).toString()
                    + "Autority" + uri.getAuthority());
            String path = FileUtils.getFilePathByUri(
                    this.getApplicationContext(), uri);
            Log.e(TAG, "File Path: " + path);
            if (path != null) {
                SaveToFile saver = new StdSaveToFile(new File(path));
                Parcours p = saver.readFilePath();
                model.setFollowingPath(p);
                model.addPropertyChangeListener(
                        new LastPointChangeListener());
                drawPath(p);
            }
        } else {
            isFileSelected = false;
            onBackPressed();
            Toast.makeText(this.getApplicationContext(),
                    getResources().getString(R.string.dialog_no_file),
                    Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Fonction qui décide ce qu'il va se passer quand l'utilisateur veut venir
     * en arrière.
     */
    @Override
    public void onBackPressed() {
        if (isFileSelected) {
            if (isPathFinished) {
                dialogBoxFinished();
            } else {
                dialogBoxExit();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Fonction qui affiche une boîte de dialogue selon si le parcours est fini.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isPathFinished) {
                dialogBoxFinished();
            } else {
                dialogBoxExit();
            }
        }
        return true;
    }

    // OUTILS

    // INITIALISATION ----------------------------------------------------------

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
                    public void onStateChanged(@NonNull View view,
                                               int newState) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            bottomSheetBehavior.setPeekHeight(0);
                        }
                    }
                    @Override
                    public void onSlide(@NonNull View view, float v) { }
                });
    }


    // FILE CHOOSER ------------------------------------------------------------

    /**
     * Démarre le FileChooser pour la sélection d'un parcours.
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"text/csv/*", "application/octetstream/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(
                Intent.createChooser(intent, getResources().getString(R.string.choose_file)),
                FILE_SELECT_CODE);
    }

    // SERVER ------------------------------------------------------------------

    /**
     * Démarre le thread qui envoie le fichier au serveur.
     */
    private boolean sendFileToServer() {
        boolean t = false;
        try {
             t = new SendFile().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (model.getSaveToFile().getFilePath().delete()) {
            Log.i(TAG, "Track file deleted : "
                    + model.getSaveToFile().getFilePath());
        } else {
            Log.i(TAG, "Track file not deleted: "
                    + model.getSaveToFile().getFilePath());
        }
        return t;
    }

    // DIALOG BOX --------------------------------------------------------------

    /**
     * Création de la fenêtre de dialogue pour quitter lorsque le suivi
     * de parcours n'est pas terminé.
     */
    private void dialogBoxExit() {
        new AlertDialog.Builder(PathActivity.this)
                .setTitle(R.string.exitdialog_title)
                .setMessage(R.string.exitdialog_message)
                .setPositiveButton(R.string.exitdialog_yes, new Exit())
                .setNegativeButton(R.string.exitdialog_no, new Cancel())
                .show();
    }

    /**
     * Création de la fenêtre de dialogue à la fin du parcours.
     */
    private void dialogBoxFinished() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.findialog_title)
                .setMessage(R.string.findialog_message)
                .setNeutralButton(R.string.findialog_stats, new Statistics())
                .show();
    }

    /**
     * Création de la fenêtre de dialogue pour afficher les statistiques du
     * parcours.
     */
    private void dialogBoxStatistics() {
        StringBuilder msg = new StringBuilder();
        msg.append(getResources().getString(R.string.stat_moy)).append(model.getSaveToFile()
                .readFilePath().getAverageSpeed()).append("\n");
        msg.append(getResources().getString(R.string.stat_max)).append(model.getSaveToFile()
                .readFilePath().getMaxSpeed()).append("\n");
        if (Objects.requireNonNull(extras.getString(CLASS_NAME))
                    .equals(PATH_AUTO)) {
            new AlertDialog.Builder(PathActivity.this)
                    .setTitle(R.string.statdialog_title)
                    .setMessage(msg)
                    .setPositiveButton(R.string.statdialog_validate, new Validate())
                    .setNegativeButton(R.string.statdialog_no, new Exit())
                    .show();
        } else {
            new AlertDialog.Builder(PathActivity.this)
                    .setTitle(R.string.statdialog_title2)
                    .setMessage(msg)
                    .setNeutralButton(R.string.statdialog_exit, new Exit())
                    .show();
        }
    }

    // LISTENERS ---------------------------------------------------------------

    /**
     * Classe qui permet d'afficher la fenêtre de dialogue quand le parcours est
     * fini
     */
    private class LastPointChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(LAST_POINT)) {
                model.stop();
                isPathFinished = true;
                dialogBoxFinished();
            }
        }
    }

    /**
     * Classe qui permet de valider le parcours pour l'envoyer au serveur.
     */
    private class Validate implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (sendFileToServer()) {
                startActivity(new Intent(PathActivity.this,
                        MainActivity.class));
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_send_file),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Classe qui permet de supprimer le fichier de suivi de traces et de
     * revenir au menu principal.
     */
    private class Exit implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (Objects.requireNonNull(extras.getString(CLASS_NAME))
                    .equals(PATH_AUTO)) {
                if (model.getSaveToFile().getFilePath().delete()) {
                    Log.i(TAG, "Track file deleted : "
                            + model.getSaveToFile().getFilePath());
                } else {
                    Log.i(TAG, "Track file not deleted: "
                            + model.getSaveToFile().getFilePath());
                }
            } else {
                if (positionsSent) {
                    if (model.getSaveToFile().getFilePath().delete()) {
                        Log.i(TAG, "Track file deleted : "
                                + model.getSaveToFile().getFilePath());
                    } else {
                        Log.i(TAG, "Track file not deleted: "
                                + model.getSaveToFile().getFilePath());
                    }
                }
            }
            startActivity(new Intent(PathActivity.this,
                    MainActivity.class));
        }
    }

    /**
     * Classe qui permet de fermer la fenêtre de dialogue.
     */
    private class Cancel implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    /**
     * Classe qui permet de voir les statistiques du parcours.
     */
    private class Statistics implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialogBoxStatistics();
        }
    }

    /**
     * Cette classe permet de mettre à jour les informations qui sont affichées.
     */
    private class RefreshInfo implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            information.setText(model.getInformationOfPathAuto());
            info.setText(model.getInformation());
        }
    }

    // SEND FILE ---------------------------------------------------------------

    /**
     * Classe qui permet d'envoyer un fichier au serveur.
     */
    @SuppressLint("StaticFieldLeak")
    private class SendFile extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            SaveToFile save = model.getSaveToFile();
            String request = "https://tracegps-2020.herokuapp.com/setsuivi";
            try {
                URL url = new URL(request);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/plain");
                OutputStream os = conn.getOutputStream();
                os.write(save.readFile().getBytes());
                os.flush();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i(TAG, "file sent");
                }
            } catch (IOException e) {
                Log.e(TAG, "error");
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
