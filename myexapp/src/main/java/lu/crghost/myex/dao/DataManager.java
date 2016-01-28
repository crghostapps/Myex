package lu.crghost.myex.dao;

import android.content.Context;
import lu.crghost.myex.R;
import lu.crghost.myex.models.*;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    public void closeDb() {
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

    /*******************************************************************************************************************
     * DB-Transactions
     *******************************************************************************************************************/
    public void beginTransaction() {
        db.beginTransaction();
    }
    public void commit() {
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public void rollback() {
        db.endTransaction();
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

    public Map<String,String> getAccountsNamesMap(String selection, String[] selectionArgs) {
        List<Account> accounts = getAccounts(selection, selectionArgs);
        Map<String, String> accountmap = new TreeMap<String, String>();
        for (Account account: accounts) {
            accountmap.put(account.getIdAsString(), account.getAcname());
        }
        return accountmap;
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

    public Cursor getDebtorsCursor(String selection, String[] selectionArgs) {
       return daoDebtor.getCursorAll(selection, selectionArgs, null);
    }

    public BigDecimal getDebtorTotalAmount(long id) {
        return daoDebtor.getTotalAmount(id);
    }

    /***********************************************************************************************
     * Costcenter
     ***********************************************************************************************/
    public Costcenter getCostcenterById(long id) {
        return daoCostcenter.get(id);
    }

    public String getCostenterDescription(long id) {
        StringBuilder sb = new StringBuilder();
        List<String> names = new ArrayList<String>();
        Costcenter child = daoCostcenter.get(id);
        while (child != null && child.getParent_id() > 0) {
            names.add(child.getName());
            child = daoCostcenter.get(child.getParent_id());
        }
        for (int i = names.size()-1; i >= 0; i--) {
            if (sb.length() < 1) sb.append(names.get(i));
            else {
                sb.append("/");
                sb.append(names.get(i));
            }
        }
        return sb.toString();
    }

    public List<Costcenter> getCostcenters(String selection, String[] selectionArgs) {
        return daoCostcenter.getAll(selection, selectionArgs);
    }

    public static final int COSTCENTERTYPE_INCOME = 0;
    public static final int COSTCENTERTYPE_EXPENSE = 1;
    public static final int COSTCENTERTYPE_ALL = 99;
    public List<Costcenter> getCostcentersForSpinner(String rootname, int cctype) {
        List<Costcenter> costcenters = new ArrayList<Costcenter>();
        if (rootname != null) {
            Costcenter root = new Costcenter();
            root.setId(0L);
            root.setName(rootname);
            costcenters.add(root);
        }
        String selection = null;
        String[] selectionArgs = null;
        if (cctype==COSTCENTERTYPE_INCOME || cctype==COSTCENTERTYPE_EXPENSE) {
            selection = "ccttype=?";
            selectionArgs = new String[] { Integer.toString(cctype) };
        }
        for (Costcenter c : daoCostcenter.getAll(selection,selectionArgs)) {
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

    public List<Measure> getMeasuresForSpinner(boolean addempty, String emptyString) {
        List<Measure> mlist = new ArrayList<Measure>();
        if (emptyString==null) emptyString=context.getResources().getString(R.string.measures_none);
        if (addempty) {
            Measure emptymeasure = new Measure();
            emptymeasure.setId(0L);
            emptymeasure.setName(emptyString);
            mlist.add(emptymeasure);
        }
        for (Measure m : daoMeasure.getAll(null,null)) {
            mlist.add(m);
        }
        return mlist;
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

    public List<Transaction> getTransactions(String selection, String[] selectionArgs, String sort) {
        return daoTransaction.getAll(selection, selectionArgs, sort);
    }

    public long insertTransaction(Transaction type) {
        return daoTransaction.save(type);
    }

    public void updateTransaction(Transaction type) {
        daoTransaction.update(type);
    }

    public void deleteTransaction(Transaction type) { daoTransaction.delete(type);}

    public void deleteTransactions(String selection, String[] selectionAgrs) {
        daoTransaction.deleteAll(selection, selectionAgrs);
    }

    public Cursor getTransactionsCursor(String selection, String[] selectionArgs, String order) {
        return daoTransaction.getCursorAll(selection,selectionArgs,order);
    }

    public Cursor getTransactionCursorById(long id) {
        return daoTransaction.getCursorById(id);
    }

    public Cursor getDescriptionCursor(String selection, String[] selectionArgs) {
        return daoTransaction.getAllDescriptions(selection,selectionArgs);
    }

}
