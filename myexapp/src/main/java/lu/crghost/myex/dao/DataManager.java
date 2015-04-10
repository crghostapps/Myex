package lu.crghost.myex.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

import lu.crghost.myex.conf.MyExProperties;
import lu.crghost.myex.models.Account;

/**
 * Created by CR on 24/12/2014.
 */
public class DataManager {

    private static final String TAG = "DataManager";

    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;

    private DaoAccount daoAccount;
    private DaoCostcenter daoCostcenter;
    private DaoDebtor daoDebtor;
    private DaoGeotrack daoGeotrack;
    private DaoMeasure daoMeasure;
    private DaoTransaction daoTransaction;


    public DataManager(Context context) {
        this.context = context;
        SQLiteOpenHelper openHelper = new DbOpenHelper(this.context);
        db = openHelper.getWritableDatabase();

        daoAccount = new DaoAccount(db);
        daoCostcenter = new DaoCostcenter(db);
        daoDebtor = new DaoDebtor(db);
        daoGeotrack = new DaoGeotrack(db);
        daoMeasure = new DaoMeasure(db);
        daoTransaction = new DaoTransaction(db);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private void openDb() {
        if (!db.isOpen()) {
            db = SQLiteDatabase.openDatabase(MyExProperties.DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            // since we pass db into DAO, have to recreate DAO if db is re-opened
            daoAccount = new DaoAccount(db);
            daoCostcenter = new DaoCostcenter(db);
            daoDebtor = new DaoDebtor(db);
            daoGeotrack = new DaoGeotrack(db);
            daoMeasure = new DaoMeasure(db);
            daoTransaction = new DaoTransaction(db);
        }
    }

    private void closeDb() {
        if (db.isOpen()) {
            db.close();
        }
    }

    private void resetDb() {
        Log.i(TAG, "Resetting database connection (close and re-open).");
        closeDb();
        SystemClock.sleep(500);
        openDb();
    }

    /***********************************************************************************************
     * Account
     ***********************************************************************************************/

    public Account getAccountById(long id) {
        return daoAccount.get(id);
    }

    public List<Account> getAccounts(String selection, String[] selectionArgs) {
        return daoAccount.getAll(selection,selectionArgs);
    }

    public long insertAccount(Account type) {
        return daoAccount.save(type);
    }

    public void updateAccount(Account type) {
        daoAccount.update(type);
    }

}
