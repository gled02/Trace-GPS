package tracesgps.ui.help;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import tracesgps.Settings;
import tracesgps.R;

/**
 * Classe qui permet d'afficher le manuel d'utilisation dans la page "Aide".
 * @author Gledis Shkurti.
 */
public class HelpActivity extends AppCompatActivity {

    // ATTRIBUTS

    private StringBuilder text = new StringBuilder();

    // COMMANDES

    /**
     * La fonction qui lance l'activité "Aide" (HelpActivity).
     * Choisi dans le dossier raw le manuel qui correspond à la langue de
     * l'application et l'affiche dans la page.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.help_activity);
        toolbar.setLogo(R.drawable.ic_help);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        BufferedReader file = null;
        try {
            int id = 0;
            String t = Settings.getLanguage();
            if (t.equals(Locale.FRENCH.toString())) {
                id = R.raw.manuel;
            } else if (t.equals(Locale.ENGLISH.toString())) {
                id = R.raw.manuelen;
            } else if (t.equals(Locale.ITALIAN.toString())) {
                id = R.raw.manuelit;
            } else if (t.equals(Locale.CHINESE.toString())) {
                id = R.raw.manuelch;
            }

            file = new BufferedReader(
                    new InputStreamReader(
                            getResources().openRawResource(id)));
            String line;
            while ((line = file.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TextView output = findViewById(R.id.large);
            output.setText(text);
        }
    }
}