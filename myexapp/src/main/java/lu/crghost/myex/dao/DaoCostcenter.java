package lu.crghost.myex.dao;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import lu.crghost.myex.models.Costcenter;
import lu.crghost.myex.models.Costcenter;

/**
 * Created by CR on 24/12/2014.
 */
public class DaoCostcenter implements DbDao<Costcenter> {

    private SQLiteDatabase db;

    private int cc_sort;

    public DaoCostcenter(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public long save(Costcenter type) {
        return db.insert(Costcenter.TABLE_NAME, null, type.getContentValues(false));
    }

    @Override
    public void update(Costcenter type) {
        db.update(Costcenter.TABLE_NAME, type.getContentValues(false), BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
    }

    @Override
    public void delete(Costcenter type) {
        if (type.getId() > 0) {
            db.delete(Costcenter.TABLE_NAME,BaseColumns._ID + "=?",new String[]{ type.getIdAsString() });
        }
    }

    @Override
    public Costcenter get(long id) {
        Costcenter type = null;
        Cursor c = db.query(Costcenter.TABLE_NAME,
                Costcenter.FIELD_NAMES,
                BaseColumns._ID +"=?", new String[]{ String.valueOf(id) },
                null,null,null,"1");
        if (c.moveToFirst()) {
            type = new Costcenter(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    @Override
    public List<Costcenter> getAll(String selection, String[] selectionArgs) {
        return getAll(selection,selectionArgs,null);
    }

    @Override
    public List<Costcenter> getAll(String selection, String[] selectionArgs, String order) {
        if (order==null) order = Costcenter.SORT_ORDER;
        List<Costcenter> types = new ArrayList<Costcenter>();
        Cursor c = db.query(Costcenter.TABLE_NAME,
                Costcenter.FIELD_NAMES,
                selection, selectionArgs,
                null,null,order,null);
        if (c.moveToFirst()) {
            do {
                Costcenter type = new Costcenter(c);
                if (type!=null) types.add(type);
            } while (c.moveToNext());

        }
        if (!c.isClosed()) c.close();
        return types;
    }

    /**
     * Calculate levels and sort of tree
     */
    public void resort() {
        cc_sort  = 0;
        // Roots
        List<Costcenter> roots = getAll("parent_id is null", new String[] {}, "name");
        for (Costcenter root : roots) {
            int clevel = 0;
            root.setClevel(clevel);
            root.setSort(cc_sort++);
            root.setHassons(resortChilds(root.getId(), clevel));
            update(root);
        }
    }

    private boolean resortChilds(long parent_id, int clevel) {
        clevel++;
        List<Costcenter> childs = getAll("parent_id=?", new String[]{Long.toString(parent_id)}, "name");
        for (Costcenter child : childs) {
            child.setClevel(clevel);
            child.setSort(cc_sort++);
            child.setHassons(resortChilds(child.getId(), clevel));
            update(child);
        }
        return true;
    }


}
