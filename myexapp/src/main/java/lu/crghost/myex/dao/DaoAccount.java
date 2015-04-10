package lu.crghost.myex.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        return db.insert(Account.TABLE_NAME, null, type.getContentValues(true));
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
}
