package lu.crghost.myex.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.models.Geotrack;

/**
 * Created by CR on 24/12/2014.
 */
public class DaoGeotrack implements DbDao<Geotrack> {

    private SQLiteDatabase db;

    public DaoGeotrack(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public long save(Geotrack type) {
        return db.insert(Geotrack.TABLE_NAME, null, type.getContentValues(true));
    }

    @Override
    public void update(Geotrack type) {
        db.update(Geotrack.TABLE_NAME, type.getContentValues(false), BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
    }

    @Override
    public void delete(Geotrack type) {
        if (type.getId() > 0) {
            db.delete(Geotrack.TABLE_NAME,BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
        }
    }

    @Override
    public Geotrack get(long id) {
        Geotrack type = null;
        Cursor c = db.query(Geotrack.TABLE_NAME,
                Geotrack.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        if (c.moveToFirst()) {
            type = new Geotrack(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    @Override
    public List<Geotrack> getAll(String selection, String[] selectionArgs) {
        return getAll(selection,selectionArgs,null);
    }

    @Override
    public List<Geotrack> getAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order = Geotrack.SORT_ORDER;
        List<Geotrack> types = new ArrayList<Geotrack>();
        Cursor c = db.query(Geotrack.TABLE_NAME,
                Geotrack.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        if (c.moveToFirst()) {
            do {
                Geotrack type = new Geotrack(c);
                if (type!=null) types.add(type);
            } while (c.moveToNext());

        }
        if (!c.isClosed()) c.close();
        return types;
    }
}
