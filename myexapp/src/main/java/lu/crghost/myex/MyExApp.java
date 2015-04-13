package lu.crghost.myex;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import lu.crghost.myex.dao.DataManager;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by CR on 13/04/2015.
 */
public class MyExApp extends Application {

    public static final String PREFS_FILENAME = "plprefs";
    private ConnectivityManager cMgr;
    private DataManager dataManager;
    private SharedPreferences prefs;
    private static Context myContext;

    //
    // getters/setters
    //
    public SharedPreferences getPrefs() {
        return this.prefs;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }


    //
    // lifecycle
    //
    @Override
    public void onCreate() {
        super.onCreate();
        cMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        prefs = getSharedPreferences(PREFS_FILENAME,Context.MODE_MULTI_PROCESS);
        // Must call this function before calling any SQLCipher functions
        SQLiteDatabase.loadLibs(getApplicationContext());
        dataManager = new DataManager(this);
        myContext = this;
    }

    public void reloadPreferences() {
        prefs = getSharedPreferences(PREFS_FILENAME,Context.MODE_MULTI_PROCESS);
    }

    @Override
    public void onTerminate() {
        // not guaranteed to be called
        super.onTerminate();
    }

    //
    // util/helpers for app
    //

    /**
     * Check if device is on network
     * @return
     */
    public boolean networkConnectionPresent() {
        NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
        if ((netInfo != null) && (netInfo.getState() != null)) {
            return netInfo.getState().equals(NetworkInfo.State.CONNECTED);
        }
        return false;
    }

    public static Context getContext() {
        return myContext;
    }



}
