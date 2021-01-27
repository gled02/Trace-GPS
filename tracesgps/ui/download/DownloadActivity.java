package tracesgps.ui.download;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tracesgps.R;

/**
 * Classe qui permet le téléchargement de parcours.
 * @author Jennifer Viney.
 */
public class DownloadActivity extends AppCompatActivity {

    // ATTRIBUTS

    private long downloadID;
    private Map<String, URL> map = new HashMap<>();
    private ListView listView;
    private TextView textView;

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Téléchargement".
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_download);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.download_activity);
        toolbar.setLogo(R.drawable.ic_file_cloud_download);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button bt = findViewById(R.id.bt_start);
        bt.setOnClickListener(new startConnection());
        textView = findViewById(R.id.explanation);
        textView.setText(
                R.string.explain_button);
        registerReceiver(onDownloadComplete,new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * Supprime le BroadcastReceiver quand l'activité prend fin.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    // BROADCAST ---------------------------------------------------------------

    /**
     * Notifie l'utilisateur du résultat du téléchargment.
     */
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(getApplicationContext(), R.string.download_complete,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.download_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    // DOWNLOAD ----------------------------------------------------------------

    /**
     * Gère le téléchargement d'un parcours.
     */
    private void download(String fileName) {
        File file = new File(getExternalFilesDir("Paths"), fileName);
        DownloadManager.Request request = null;
        try {
            request = new DownloadManager.Request(Uri.parse(
                    Objects.requireNonNull(map.get(fileName))
                            .toURI().toString()))
                    .setTitle(fileName)
                    .setDescription(getResources().getString(R.string.downloading))
                    .setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        DownloadManager downloadManager =
                (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = Objects.requireNonNull(downloadManager).enqueue(request);
    }

    // LISTENER ----------------------------------------------------------------

    /**
     * Écouteur qui démarre le téléchargement lorsqu'un parcours est
     * sélectionné.
     */
    private AdapterView.OnItemClickListener listener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id) {
            String t = (String) listView.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), t,
                    Toast.LENGTH_SHORT).show();
            download(t);
        }
    };

    /**
     * Démarre la connection au serveur.
     */
    private class startConnection implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Objects.requireNonNull(getSupportActionBar())
                    .setTitle(R.string.loading);
            new Connect().execute();
        }
    }

    // SERVEUR -----------------------------------------------------------------

    /**
     * Classe qui permet la connection au serveur pour récupérer les parcours.
     */
    @SuppressLint("StaticFieldLeak")
    private class Connect extends AsyncTask<Void, Void, Map<String, URL>> {

        @Override
        protected Map<String, URL> doInBackground(Void... voids) {
            String u = "https://tracegps-2020.herokuapp.com/getfile?";
            String[] types = {"csv", "xml", "json"};
            try {
                for (String t : types) {
                    boolean correct = true;
                    String tmp = (u + "type=" + t);
                    int i = 1;
                    while (correct) {
                        URL url = new URL(tmp + "&n=" + i);
                        HttpURLConnection conn =
                                (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        InputStream in = conn.getInputStream();
                        InputStreamReader rd = new InputStreamReader(in);
                        BufferedReader bf = new BufferedReader(rd);
                        String line = bf.readLine();
                        if (validFile(line)) {
                            map.put("Parcours" + i + "." + t, url);
                            ++i;
                        } else {
                            correct = false;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, URL> result) {
            Objects.requireNonNull(getSupportActionBar())
                    .setTitle(R.string.download_activity);
            if (result.isEmpty()) {
                result.put(getResources().getString(R.string.no_paths), null);
            }
            textView.setText(R.string.explain_download);
            listView = findViewById(R.id.list_paths);
            ArrayAdapter<Object> aa = new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    result.keySet().toArray());
            listView.setAdapter(aa);
            listView.setOnItemClickListener(listener);
        }

        /**
         * Si le fichier est valide renvoi vrai, sinon renvoi faux.
         */
        private boolean validFile(String s) {
            return !s.contains("Fichier non trouvé");
        }
    }
}
