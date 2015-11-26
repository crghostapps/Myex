package lu.crghost.myex;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import lu.crghost.myex.dao.DataManager;
import lu.crghost.cralib3.security.StringEncoder;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * Created by CR on 13/04/2015.
 */
public class MyExApp extends Application {

    private static final String TAG = "MyExApp";
    public static final String PREFS_FILENAME = "myexprefs";
    public static final String APP_PACKAGE_NAME = "lu.crghost.myex";
    private ConnectivityManager cMgr;
    private DataManager dataManager;
    private SharedPreferences prefs;
    private static Context myContext;

    public static final String DATABASE_NAME = "pl.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_PATH = Environment.getDataDirectory() + "/data/" + APP_PACKAGE_NAME + "/databases/" + DATABASE_NAME;
    public static final String DOCUMENT_PATH =  Environment.getExternalStorageDirectory()+"/Documents/myex/";
    public static final String IMAGE_PATH    = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/myex";


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
        if (dataManager==null) reloadDataManager();
        return this.dataManager;
    }

    public String getCurrencySymbol() {
        return prefs.getString("currency","");
    }
    public long getCurrencySymbolId() {
        return prefs.getLong("currency_id",1);
    }


    /**
     * Only called once, the first who initiate MyExApp (f.ex. TransactionsProvider)
     */
    @Override
    public void onCreate() {
        super.onCreate();
        cMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        prefs = getSharedPreferences(PREFS_FILENAME,Context.MODE_MULTI_PROCESS);
        reloadDataManager();
        myContext = this;
        // Localisation
        localisationRunning = false;
    }

    /**
     * Open database
     */
    public void reloadDataManager() {
        if (dataManager != null) {
            dataManager.closeDb();
        }
        boolean firstlogin = prefs.getBoolean("firstlogin", true);
        String  dbpassword = prefs.getString("dbpassword","?");
        if (dbpassword.equals("?")) {
            dataManager = null;
            Log.w(TAG,"First login, db not initialized");
        } else {
            dbpassword = StringEncoder.decode(this, dbpassword);
            SQLiteDatabase.loadLibs(getApplicationContext());   // Must call this function before calling any SQLCipher functions
            dataManager = new DataManager(this, dbpassword);    // <-- database can be blocked here
            Log.i(TAG,"Database connected");
        }

    }

    public LatLng getLastKnownLatLng() {
        LatLng latLng = new LatLng(0,0);
        Location location = getLastKnownLocation();
        if (location!=null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return latLng;
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

    /**
     * Get actuel version name
     * @param c
     * @return
     */
    public static String getVersionName(Context c) {
        String versionName = "Unknown version";
        try {
            PackageInfo pinfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionName;
    }





    /***********************************************************************************************
     * Initital Data
     ***********************************************************************************************/
    public void initData() {
        SQLiteDatabase db = dataManager.getDb();

        // Measures
        db.execSQL("insert into measures (_id,name,nameshort,mtype) values(1,'"
                + getResources().getString(R.string.data_measures1_name) + "','"
                + getResources().getString(R.string.data_measures1_short) + "',0);");
        db.execSQL("insert into measures (_id,name,nameshort,mtype) values(2,'"
                + getResources().getString(R.string.data_measures2_name) + "','"
                + getResources().getString(R.string.data_measures2_short) + "',2);");
        db.execSQL("insert into measures (_id,name,nameshort,mtype) values(3,'"
                + getResources().getString(R.string.data_measures3_name) + "','"
                + getResources().getString(R.string.data_measures3_short) + "',2);");
        db.execSQL("insert into measures (_id,name,nameshort,mtype) values(4,'"
                + getResources().getString(R.string.data_measures4_name) + "','"
                + getResources().getString(R.string.data_measures4_short) + "',2);");

        // Accounts
        db.execSQL("insert into accounts (_id,acname,acnumber,actype,initbalance,limitamount,measure_id, costcenter_id) values(1,'"
                + getResources().getString(R.string.data_account1) + "',null,0,0,0,1,3);");
        db.execSQL("insert into accounts (_id,acname,acnumber,actype,initbalance,limitamount,measure_id, costcenter_id) values(2,'"
                + getResources().getString(R.string.data_account2) + "',null,1,0,0,1,0);");

        // Costcenters
        // income
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id, isdefaultcct, ccttype, hassons) values(1,'"
                + getResources().getString(R.string.data_costcenter_income) + "',0,30,null,0,0,1);");
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id, isdefaultcct, ccttype, hassons) values(3,'"
                + getResources().getString(R.string.data_costcenter_income_misc) + "',1,40,1,1,0,0);");

        // expense
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id, isdefaultcct, ccttype, hassons) values(2,'"
                + getResources().getString(R.string.data_costcenter_expence) + "',0,10,null,0,1,1);");
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id,isdefaultcct,ccttype, hassons) values(4,'"
                + getResources().getString(R.string.data_costcenter_expence_misc) + "',1,20,2,1,1,0);");
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
