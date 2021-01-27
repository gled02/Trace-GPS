package tracesgps.ui.map;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import tracesgps.R;
import tracesgps.StdMapModel;

/**
 * Classe qui permet l'affichage de la carte.
 * @author Ning Shi.
 */
public class MapActivity extends StdMapModel {

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Carte".
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_map);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.map_activity);
        toolbar.setLogo(R.drawable.ic_maps);
        setSupportActionBar(toolbar);
    }
}