package lu.crghost.myex.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.models.Measure;

/**
 * Created by CR on 24/12/2014.
 */
public class DaoMeasure implements DbDao<Measure> {

    private SQLiteDatabase db;

    public DaoMeasure(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public long save(Measure type) {
        return db.insert(Measure.TABLE_NAME, null, type.getContentValues(true));
    }

    @Override
    public void update(Measure type) {
        db.update(Measure.TABLE_NAME, type.getContentValues(false), BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
    }

    @Override
    public void delete(Measure type) {
        if (type.getId() > 0) {
            db.delete(Measure.TABLE_NAME,BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
        }
    }

    @Override
    public Measure get(long id) {
        Measure type = null;
        Cursor c = db.query(Measure.TABLE_NAME,
                Measure.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        if (c.moveToFirst()) {
            type = new Measure(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    @Override
    public List<Measure> getAll(String selection, String[] selectionArgs) {
        return getAll(selection,selectionArgs,null);
    }

    @Override
    public List<Measure> getAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order = Measure.SORT_ORDER;
        List<Measure> types = new ArrayList<Measure>();
        Cursor c = db.query(Measure.TABLE_NAME,
                Measure.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        if (c.moveToFirst()) {
            do {
                Measure type = new Measure(c);
                if (type!=null) types.add(type);
            } while (c.moveToNext());

        }
        if (!c.isClosed()) c.close();
        return types;
    }
}
