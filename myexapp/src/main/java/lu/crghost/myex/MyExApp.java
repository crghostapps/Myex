package lu.crghost.myex;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import lu.crghost.myex.dao.DataManager;
import lu.crghost.cralib.security.StringEncoder;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * Created by CR on 13/04/2015.
 */
public class MyExApp extends Application {

    private static final String TAG = "MyExApp";
    public static final String PREFS_FILENAME = "myexprefs";
    private ConnectivityManager cMgr;
    private DataManager dataManager;
    private SharedPreferences prefs;
    private static Context myContext;

    private long costcentersEdit_last_parent_id = 0;
    private boolean transactionEdit_last_sign_negatif = true;

    LocationManager locationManager;
    boolean localisationRunning;

    //
    // getters/setters
    //
    public SharedPreferences getPrefs() {
        return this.prefs;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public String getCurrencySymbol() {
        return prefs.getString("currency","");
    }


    //
    // lifecycle
    //
    @Override
    public void onCreate() {
        super.onCreate();
        cMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        prefs = getSharedPreferences(PREFS_FILENAME,Context.MODE_MULTI_PROCESS);

        boolean firstlogin = prefs.getBoolean("firstlogin",true);
        String  dbpassword = null;
        if (firstlogin) {
            // @TODO: Add ask dbpass dialog here for first start
            dbpassword = "gruntz4711";
        } else {
            //dbpassword = StringEncoder.decode(this,prefs.getString("dxpassword","gruntz4711"));
            dbpassword = "gruntz4711";
        }

        // Must call this function before calling any SQLCipher functions
        SQLiteDatabase.loadLibs(getApplicationContext());
        dataManager = new DataManager(this,dbpassword);
        myContext = this;
        if (firstlogin) {
            Log.i(TAG, "First start --> init basic data");
            initData();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstlogin", false);
            editor.putString("dbpassword", StringEncoder.encode(this, dbpassword));
            editor.apply();
        }

        // Localisation
        localisationRunning = false;
    }

    public Location getLastKnownLocation() {
        Location location = null;
        if (locationManager == null) locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    public void refreshLocation() {
        if (!prefs.getBoolean("localisation",true)) {
            return;
        }
        if (localisationRunning) {
            return;
        }
        if (locationManager == null) locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "------- Current location=" + location);
                locationManager.removeUpdates(this);
                localisationRunning = false;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        }
        localisationRunning = true;
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

    /**
     * Get context from a static function
     * @return
     */
    public static Context getContext() {
        return myContext;
    }

    /**
     * Get public document directory
     * @return
     */
    public static File getPublicDocumentDir() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Documents/myex");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory " + file.getAbsolutePath() + " not created");
                file = null;
            }
        }
        return file;
    }

    /***********************************************************************************************
     * Initital Data
     ***********************************************************************************************/
    private void initData() {
        SQLiteDatabase db = dataManager.getDb();

        // Measures
        db.execSQL("insert into measures (_id,name,nameshort) values(1,'"
                + getResources().getString(R.string.data_measures1_name) + "','"
                + getResources().getString(R.string.data_measures1_short) + "');");
        db.execSQL("insert into measures (_id,name,nameshort) values(2,'"
                + getResources().getString(R.string.data_measures2_name) + "','"
                + getResources().getString(R.string.data_measures2_short) + "');");
        db.execSQL("insert into measures (_id,name,nameshort) values(3,'"
                + getResources().getString(R.string.data_measures3_name) + "','"
                + getResources().getString(R.string.data_measures3_short) + "');");
        db.execSQL("insert into measures (_id,name,nameshort) values(4,'"
                + getResources().getString(R.string.data_measures4_name) + "','"
                + getResources().getString(R.string.data_measures4_short) + "');");

        // Accounts
        db.execSQL("insert into accounts (_id,acname,acnumber,actype,initbalance,limitamount,measure_id) values(1,'"
                + getResources().getString(R.string.data_account1) + "',null,0,0,0,1);");
        db.execSQL("insert into accounts (_id,acname,acnumber,actype,initbalance,limitamount,measure_id) values(2,'"
                + getResources().getString(R.string.data_account2) + "',null,1,0,0,1);");

        // Costcenters
        // income
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id, isdefaultcct, ccttype) values(1,'"
                + getResources().getString(R.string.data_costcenter_income) + "',0,30,0,0,0);");
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id, isdefaultcct, ccttype) values(3,'"
                + getResources().getString(R.string.data_costcenter_income_misc) + "',1,40,1,1,0);");

        // expense
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id, isdefaultcct, ccttype) values(2,'"
                + getResources().getString(R.string.data_costcenter_expence) + "',0,10,0,0,1);");
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id,isdefaultcct,ccttype) values(4,'"
                + getResources().getString(R.string.data_costcenter_expence_misc) + "',1,20,2,1,1);");
    }

    /***********************************************************************************************
    * Global variables
    ***********************************************************************************************/
    public long getCostcentersEdit_last_parent_id() {
        return costcentersEdit_last_parent_id;
    }

    public void setCostcentersEdit_last_parent_id(long costcentersEdit_last_parent_id) {
        this.costcentersEdit_last_parent_id = costcentersEdit_last_parent_id;
    }

    public boolean isTransactionEdit_last_sign_negatif() {
        return transactionEdit_last_sign_negatif;
    }

    public void setTransactionEdit_last_sign_negatif(boolean transactionEdit_last_sign_negatif) {
        this.transactionEdit_last_sign_negatif = transactionEdit_last_sign_negatif;
    }
}
