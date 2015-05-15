package lu.crghost.myex.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.models.Transaction;

/**
 * Created by CR on 24/12/2014.
 */
public class DaoTransaction implements DbDao<Transaction> {

    private SQLiteDatabase db;

    public DaoTransaction(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public long save(Transaction type) {
        return db.insert(Transaction.TABLE_NAME, null, type.getContentValues(false));
    }

    @Override
    public void update(Transaction type) {
        db.update(Transaction.TABLE_NAME, type.getContentValues(false), BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
    }

    @Override
    public void delete(Transaction type) {
        if (type.getId() > 0) {
            db.delete(Transaction.TABLE_NAME,BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
        }
    }

    @Override
    public Transaction get(long id) {
        Transaction type = null;
        Cursor c = db.query(Transaction.TABLE_NAME,
                Transaction.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        if (c.moveToFirst()) {
            type = new Transaction(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    @Override
    public List<Transaction> getAll(String selection, String[] selectionArgs) {
        return getAll(selection,selectionArgs,null);
    }

    @Override
    public List<Transaction> getAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order = Transaction.SORT_ORDER;
        List<Transaction> types = new ArrayList<Transaction>();
        Cursor c = db.query(Transaction.TABLE_NAME,
                Transaction.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        if (c.moveToFirst()) {
            do {
                Transaction type = new Transaction(c);
                if (type!=null) types.add(type);
            } while (c.moveToNext());

        }
        if (!c.isClosed()) c.close();
        return types;
    }

    /**
     * Returns a cursor for selection
     * @param selection
     * @param selectionArgs
     * @param order
     * @return
     */
    public Cursor getCursorAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order = Transaction.SORT_ORDER;
        Cursor c = db.query(Transaction.TABLE_NAME,
                Transaction.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        return c;
    }

    /**
     * Returns a cursor for id
     * @param id
     * @return
     */
    public Cursor getCursorById(long id) {
        Cursor c = db.query(Transaction.TABLE_NAME,
                Transaction.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        return c;
    }

    /**
     * Returns a cursor for last description
     * @param selection
     * @param selectionArgs
     * @return
     */
    public Cursor getAllDescriptions(String selection, String[] selectionArgs) {
        String sql = "select description, max(_id) as _id from " + Transaction.TABLE_NAME ;
        if (selection != null) {
            sql += " where " + selection;
        }
        sql += " group by description";
        return db.rawQuery(sql, selectionArgs);
    }

    public android.database.Cursor getNativeCursorById(long id) {
        android.database.Cursor c = db.query(Transaction.TABLE_NAME,
                Transaction.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        return c;
    }
}
