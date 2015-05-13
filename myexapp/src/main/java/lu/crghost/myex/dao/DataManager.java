package lu.crghost.myex.dao;

import android.content.Context;
import lu.crghost.myex.R;
import lu.crghost.myex.models.*;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.conf.MyExProperties;

/**
 * Central data access
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


    /**
     * Get position of an id in a list
     * @param list
     * @param id
     * @return
     */
    public int getPositionInList(List<BaseModel> list, long id) {
        int cpos = 0;
        int position = 0;
        for (BaseModel m : list) {
            if (m.getId()==id) {
                position = cpos;
                break;
            }
            cpos++;
        }
        return position;
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

    public void deleteAccount(Account type) { daoAccount.delete(type); }

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

    public List<Costcenter> getCostcentersForSpinner() {
        List<Costcenter> costcenters = new ArrayList<Costcenter>();
        Costcenter root = new Costcenter();
        root.setId(0L);
        root.setName(context.getResources().getString(R.string.data_costcenter_root));
        costcenters.add(root);
        for (Costcenter c : daoCostcenter.getAll(null,null)) {
            costcenters.add(c);
        }
        return costcenters;
    }

    public void resortCostcenters() {
        daoCostcenter.resort();
    }

    public long insertCostcenter(Costcenter type) {
        return daoCostcenter.save(type);
    }

    public void updateCostcenter(Costcenter type) {
        daoCostcenter.update(type);
    }

    public void deleteCostcenter(Costcenter type) { daoCostcenter.delete(type); }

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

    public List<Measure> getMeasuresForSpinner(boolean addempty, boolean iscurrency) {
        List<Measure> mlist = new ArrayList<Measure>();
        if (addempty) {
            Measure emptymeasure = new Measure();
            emptymeasure.setId(0L);
            emptymeasure.setName(context.getResources().getString(R.string.measures_none));
            mlist.add(emptymeasure);
        }
        String selection = "iscurrency=?";
        String args[] = new String[] {"0"};
        if (iscurrency) args[0] = "1";
        for (Measure m : daoMeasure.getAll(selection,args)) {
            mlist.add(m);
        }
        return mlist;
    }

    public String getCurrencySymbol(long id) {
        String s = "";
        Measure measure = daoMeasure.get(id);
        if (measure != null) s = measure.getNameshort();
        return s;
    }

    public long insertMeasure(Measure type) {
        return daoMeasure.save(type);
    }

    public void updateMeasure(Measure type) {
        daoMeasure.update(type);
    }

    public void deleteMeasure(Measure type) { daoMeasure.delete(type); }

    public int getMeasurePosition(List<Measure> list, long id) {
        int cpos = 0;
        int position = 0;
        for (Measure m : list) {
            if (m.getId()==id) {
                position = cpos;
                break;
            }
            cpos++;
        }
        return position;
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

    public void deleteTransaction(Transaction type) { daoTransaction.delete(type);}

    public Cursor getTransactionsCursor(String selection, String[] selectionArgs, String order) {
        return daoTransaction.getCursorAll(selection,selectionArgs,order);
    }

    public Cursor getTransactionCursorById(long id) {
        return daoTransaction.getCursorById(id);
    }



}
