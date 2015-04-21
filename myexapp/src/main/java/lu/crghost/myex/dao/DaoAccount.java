package lu.crghost.myex.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.models.Account;

/**
 * Created by CR on 24/12/2014.
 */
public class DaoAccount implements DbDao<Account> {

    private SQLiteDatabase db;

    public DaoAccount(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public long save(Account type) {
        return db.insert(Account.TABLE_NAME, null, type.getContentValues(false));
    }

    @Override
    public void update(Account type) {
        db.update(Account.TABLE_NAME, type.getContentValues(false), BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
    }

    @Override
    public void delete(Account type) {
        if (type.getId() > 0) {
            db.delete(Account.TABLE_NAME,BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
        }
    }

    @Override
    public Account get(long id) {
        Account type = null;
        Cursor c = db.query(Account.TABLE_NAME,
                Account.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        if (c.moveToFirst()) {
            type = new Account(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    @Override
    public List<Account> getAll(String selection, String[] selectionArgs) {
        return getAll(selection, selectionArgs, null);
    }

    @Override
    public List<Account> getAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order=Account.SORT_ORDER;
        List<Account> types = new ArrayList<Account>();
        Cursor c = db.query(Account.TABLE_NAME,
                Account.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        if (c.moveToFirst()) {
            do {
                Account type = new Account(c);
                if (type!=null) types.add(type);
            } while (c.moveToNext());

        }
        if (!c.isClosed()) c.close();
        return types;
    }

    /**
     * Returns the account balance
     * @param account_id
     * @return
     */
    public double getAccountBalance(String account_id) {
        double d = 0;
        String sql = "select sum(initbalance + amount) as currentbalance from ( "+
                "	  select  "+
                "	    0 as initbalance, sum(amount) as amount "+
                "	  from transactions a "+
                "	  where a.account_id=? "+
                "	  union all   "+
                "	  select   "+
                "	    initbalance, 0 as amount "+
                "	  from accounts a   "+
                "	  where a._id=? "+
                " ) x";
        Cursor csr = db.rawQuery(sql, new String[]{account_id,account_id});
        if (csr.moveToNext()) {
            d = csr.getDouble(csr.getColumnIndex("currentbalance"));
        }
        csr.close();

        return d;
    }

    /**
     * Returns the balance at the time of the last credit
     * @param account_id
     * @return
     */
    public double getAccountMaxBalance(String account_id) {
        double d = 0;
        String sql = "select sum(initbalance + amount) as currentbalance from ( "+
                "	  select  "+
                "	    0 as initbalance, sum(amount) as amount "+
                "	  from transactions a "+
                "	  where a.account_id=? "+
                "	    and a._id <= (select max(_id) as lastplus from transactions where account_id=? and amount>0) "+
                "	  union all   "+
                "	  select   "+
                "	    initbalance, 0 as amount "+
                "	  from accounts a   "+
                "	  where a._id=? "+
                " ) x";

        Cursor csr = db.rawQuery(sql, new String[]{account_id,account_id,account_id});
        if (csr.moveToNext()) {
            d = csr.getDouble(csr.getColumnIndex("currentbalance"));
        }
        csr.close();

        return d;
    }
}
