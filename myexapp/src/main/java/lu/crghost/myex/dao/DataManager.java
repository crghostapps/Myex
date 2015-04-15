package lu.crghost.myex.dao;

import android.content.Context;
import lu.crghost.myex.models.*;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

import lu.crghost.myex.conf.MyExProperties;

/**
 * Created by CR on 24/12/2014.
 */
public class DataManager {

    private static final String TAG = "DataManager";

    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;
    private String dbpassword;

    private DaoAccount daoAccount;
    private DaoCostcenter daoCostcenter;
    private DaoDebtor daoDebtor;
    private DaoGeotrack daoGeotrack;
    private DaoMeasure daoMeasure;
    private DaoTransaction daoTransaction;


    public DataManager(Context context, String dbpassword) {
        this.context = context;
        this.dbpassword = dbpassword;
        SQLiteOpenHelper openHelper = new DbOpenHelper(this.context);
        db = openHelper.getWritableDatabase(this.dbpassword);

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
            db = SQLiteDatabase.openDatabase(MyExProperties.DATABASE_PATH,this.dbpassword, null, SQLiteDatabase.OPEN_READWRITE);
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
        return daoAccount.getAll(selection, selectionArgs);
    }

    public long insertAccount(Account type) {
        return daoAccount.save(type);
    }

    public void updateAccount(Account type) {
        daoAccount.update(type);
    }

    public double getAccountBalance(Account type) {
        return daoAccount.getAccountBalance(type.getIdAsString());
    }

    public double getAccountMaxBalance(Account type) {
        return daoAccount.getAccountMaxBalance(type.getIdAsString());
    }

    /***********************************************************************************************
     * Debtor
     ***********************************************************************************************/
    public Debtor getDebtortById(long id) {
        return daoDebtor.get(id);
    }

    public List<Debtor> getDebtors(String selection, String[] selectionArgs) {
        return daoDebtor.getAll(selection, selectionArgs);
    }

    public long insertDebtor(Debtor type) {
        return daoDebtor.save(type);
    }

    public void updateDebtor(Debtor type) {
        daoDebtor.update(type);
    }

    public void deleteDebtor(Debtor type) { daoDebtor.delete(type);}

    /***********************************************************************************************
     * Costcenter
     ***********************************************************************************************/
    public Costcenter getCostcenterById(long id) {
        return daoCostcenter.get(id);
    }

    public List<Costcenter> getCostcenters(String selection, String[] selectionArgs) {
        return daoCostcenter.getAll(selection, selectionArgs);
    }

    public long insertCostcenter(Costcenter type) {
        return daoCostcenter.save(type);
    }

    public void updateCostcenter(Costcenter type) {
        daoCostcenter.update(type);
    }

    /***********************************************************************************************
     * Geotrack
     ***********************************************************************************************/
    public Geotrack getGeotrackById(long id) {
        return daoGeotrack.get(id);
    }

    public List<Geotrack> getGeotracks(String selection, String[] selectionArgs) {
        return daoGeotrack.getAll(selection, selectionArgs);
    }

    public long insertGeotrack(Geotrack type) {
        return daoGeotrack.save(type);
    }

    public void updateGeotrack(Geotrack type) {
        daoGeotrack.update(type);
    }

    /***********************************************************************************************
     * Measure
     ***********************************************************************************************/
    public Measure getMeasureById(long id) {
        return daoMeasure.get(id);
    }

    public List<Measure> getMeasures(String selection, String[] selectionArgs) {
        return daoMeasure.getAll(selection, selectionArgs);
    }

    public long insertMeasure(Measure type) {
        return daoMeasure.save(type);
    }

    public void updateMeasure(Measure type) {
        daoMeasure.update(type);
    }

    /***********************************************************************************************
     * Transaction
     ***********************************************************************************************/
    public Transaction getTransactionById(long id) {
        return daoTransaction.get(id);
    }

    public List<Transaction> getTransactions(String selection, String[] selectionArgs) {
        return daoTransaction.getAll(selection, selectionArgs);
    }

    public long insertTransaction(Transaction type) {
        return daoTransaction.save(type);
    }

    public void updateTransaction(Transaction type) {
        daoTransaction.update(type);
    }




}
