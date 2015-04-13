package lu.crghost.myex.models;

import android.content.ContentValues;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import lu.crghost.cralib.tools.HashCodeUtil;

import static android.provider.BaseColumns._ID;

/**
 * Created by CR on 23/12/2014.
 */
public class Costcenter extends BaseModel implements BaseModelInterface {

    private static final String TAG="Costcenter";

    public static final String TABLE_NAME = "costcenters";
    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "name",
            "parent_id",
            "clevel",
            "sort",
            "hassons",
            "ccttype",
            "isdefaultcct",
            "measure_id",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = "name";

    private String name;
    private long parent_id;
    private int clevel;
    private int sort;
    private int hassons;
    private int ccttype;
    private int isdefaultcct;
    private long measure_id;

    public static final int TYPE_INCOME  = 1;
    public static final int TYPE_EXPENSE = 2;
    public static final int MAXLEVEL = 4;			// 1->4
    public static final int MAXSTDID = 4;			// Id's 1-4 are fixed

    public Costcenter() {};
    public Costcenter(ContentValues c) { setValues(c); }
    public Costcenter(Cursor c) { setValues(c); }

    /**
     * Create table
     * @param db
     */
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        "  name TEXT NULL ,"+
                        "  parent_id INT NULL ,"+
                        "  clevel INT ,"+
                        "  sort   INT ," +
                        "  hassons INT," +
                        "  ccttype INT," +
                        "  isdefaultcct INT," +
                        "  measure_id INT NULL, " +
                        "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime'))  ,"+
                        "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );");
        Log.i(TAG, TABLE_NAME + " created");

        /* Predefined cost centers
        // income
        db.execSQL("insert into costcenters (_id, name, clevel, sort, hassons, ccttype) values(1,'"
                + MyExApp.getContext().getResources().getString(R.string.data_costcenter_income) + "',0,30,1,1);");
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id,isdefaultcct, ccttype) values(3,'"
                + MyExApp.getContext().getResources().getString(R.string.data_costcenter_income_misc) + "',1,40,1,1,1);");

        // expense
        db.execSQL("insert into costcenters (_id, name, clevel, sort, hassons,ccttype) values(2,'"
                + MyExApp.getContext().getResources().getString(R.string.data_costcenter_expence) + "',0,10,1,2);");
        db.execSQL("insert into costcenters (_id, name, clevel, sort, parent_id,isdefaultcct,ccttype) values(4,'"
                + MyExApp.getContext().getResources().getString(R.string.data_costcenter_expence_misc) + "',1,20,2,1,2);");
                */
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public ContentValues getContentValues(boolean withId) {
        ContentValues c = new ContentValues();
        if (withId) c.put(_ID,getId());
        c.put("name",getName());
        c.put("parent_id",getParent_id());
        c.put("clevel",getClevel());
        c.put("sort",getSort());
        c.put("hassons",getHassons());
        c.put("ccttype",getCcttype());
        c.put("isdefaultcct",getIsdefaultcct());
        c.put("measure_id",getMeasure_id());
        c.put("created_at",getCreated_at());
        c.put("updated_at",getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        name = c.getAsString("name");
        parent_id = c.getAsLong("parent_id");
        clevel = c.getAsInteger("clevel");
        sort = c.getAsInteger("sort");
        hassons = c.getAsInteger("hassons");
        ccttype = c.getAsInteger("ccttype");
        isdefaultcct = c.getAsInteger("isdefaultcct");
        measure_id = c.getAsLong("measure_id");
    }

    @Override
    public void setValues(Cursor c) {
        if (c!=null) {
            setId(c.getLong(0));
            setName(c.getString(1));
            setParent_id(c.getLong(2));
            setClevel(c.getInt(3));
            setSort(c.getInt(4));
            setHassons(c.getInt(5));
            setCcttype(c.getInt(6));
            setIsdefaultcct(c.getInt(7));
            setMeasure_id(c.getLong(8));
            setCreated_at(c.getString(9));
            setUpdated_at(c.getString(10));
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public int getClevel() {
        return clevel;
    }

    public void setClevel(int clevel) {
        this.clevel = clevel;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getHassons() {
        return hassons;
    }

    public boolean hasSons() {
        return int_to_boolean(hassons);
    }

    public void setHassons(int hassons) {
        this.hassons = hassons;
    }
    public void setHassons(boolean hassons) {
        this.hassons = boolean_to_int(hassons);
    }

    public int getCcttype() {
        return ccttype;
    }

    public void setCcttype(int ccttype) {
        this.ccttype = ccttype;
    }

    public int getIsdefaultcct() {
        return isdefaultcct;
    }
    public boolean isDefaultCct() {
        return int_to_boolean(isdefaultcct);
    }

    public void setIsdefaultcct(int isdefaultcct) {
        this.isdefaultcct = isdefaultcct;
    }
    public void setIsdefaultcct(boolean isdefaultcct) {
        this.isdefaultcct = boolean_to_int(isdefaultcct);
    }

    public long getMeasure_id() {
        return measure_id;
    }

    public void setMeasure_id(long measure_id) {
        this.measure_id = measure_id;
    }


    @Override
    public String toString() {
        return "Costcenter [id="+this.getId()+ " " + getName() +"]";
    }

    @Override
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        ContentValues c = getContentValues(true);
        for (String key : c.keySet()) {
            if (!key.equals("created_at") && !key.equals("updated_at")) {
                result = HashCodeUtil.hash(result, c.get(key));
            }
        }
        return result;
    }

    @Override
    /**
     * Only on Id, should test also all the other fields
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Costcenter)) {
            return false;
        }
        Costcenter other = (Costcenter) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }

}
