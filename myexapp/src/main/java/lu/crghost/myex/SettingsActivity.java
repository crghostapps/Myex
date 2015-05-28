package lu.crghost.myex;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

/**
 * Created by CR on 14/04/2015.
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager pmanager = getPreferenceManager();
        pmanager.setSharedPreferencesName(MyExApp.PREFS_FILENAME);
        pmanager.setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);
        addPreferencesFromResource(R.xml.preferences);

        Log.i(TAG,"Default locale: " + Locale.getDefault());
        Log.i(TAG, "Available locales:");
        Locale[] locales = Locale.getAvailableLocales();
        for (int i=0; i < locales.length; i++) {
            Log.i(TAG,locales[i].getLanguage() + " " + locales[i].getCountry());
        }

    }

}
