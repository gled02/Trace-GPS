package tracesgps;

import android.app.Application;
import android.content.Context;

/**
 * Classe qui permet la récupération du context de l'application.
 * @author Jennifer Viney.
 */
public class MyApplication extends Application {

    // ATTRIBUTS

    private static Context context;

    // REQUETES

    /**
     * Renvoi le context de l'application.
     */
    public static Context getAppContext() {
        return context;
    }

    // COMMANDES

    /**
     * La fonction qui lance l'activité.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
