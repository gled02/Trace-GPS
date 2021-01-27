package tracesgps.ui.statistics;

import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tracesgps.R;
import tracesgps.SaveToFile;
import tracesgps.StdFileList;
import tracesgps.StdSaveToFile;

/**
 * Classe qui permet l'affichage de statistiques pour les fichiers de suivi de
 * traces enregistrés.
 * @author Jennifer Viney.
 */
public class StatisticsActivity extends AppCompatActivity {

    // ATTRIBUTS

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Statistiques".
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_statistics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.statistics_activity);
        toolbar.setLogo(R.drawable.ic_statistics);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        prepareListData();
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(
                this, listDataHeader, listDataChild);
        ExpandableListView expListView = findViewById(R.id.list_stats);
        expListView.setAdapter(listAdapter);
    }

    // OUTILS

    /**
     * Ajoute les informations nécéssaires pour l'affichage des statistiques
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        StdFileList.refresh();
        for (File f : StdFileList.getFileList()) {
            String tmp = f.getName();
            String fileName = tmp.substring(0, tmp.lastIndexOf('.'));
            listDataHeader.add(fileName);
            SaveToFile s = new StdSaveToFile(f);
            List<String> stats = new ArrayList<>();
            stats.add(getResources().getString(R.string.stat_moy)
                    + s.readFilePath().getAverageSpeed());
            stats.add(getResources().getString(R.string.stat_max)
                    + s.readFilePath().getMaxSpeed());
            listDataChild.put(fileName, stats);
        }
    }
}
