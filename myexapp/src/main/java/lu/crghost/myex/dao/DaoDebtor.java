package lu.crghost.myex.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.models.Debtor;

/**
 * Created by CR on 24/12/2014.
 */
public class DaoDebtor implements DbDao<Debtor> {

    private SQLiteDatabase db;

    public DaoDebtor(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public long save(Debtor type) {
        return db.insert(Debtor.TABLE_NAME, null, type.getContentValues(false));
    }

    @Override
    public void update(Debtor type) {
        db.update(Debtor.TABLE_NAME, type.getContentValues(false), BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
    }

    @Override
    public void delete(Debtor type) {
        if (type.getId() > 0) {
            db.delete(Debtor.TABLE_NAME,BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
        }
    }

    @Override
    public Debtor get(long id) {
        Debtor type = null;
        Cursor c = db.query(Debtor.TABLE_NAME,
                Debtor.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        if (c.moveToFirst()) {
            type = new Debtor(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    @Override
    public List<Debtor> getAll(String selection, String[] selectionArgs) {
        return getAll(selection,selectionArgs,null);
    }

    @Override
    public List<Debtor> getAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order = Debtor.SORT_ORDER;
        List<Debtor> types = new ArrayList<Debtor>();
        Cursor c = db.query(Debtor.TABLE_NAME,
                Debtor.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        if (c.moveToFirst()) {
            do {
                Debtor type = new Debtor(c);
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
        if (order==null) order = Debtor.SORT_ORDER;
        Cursor c = db.query(Debtor.TABLE_NAME,
                Debtor.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        return c;
    }


}
