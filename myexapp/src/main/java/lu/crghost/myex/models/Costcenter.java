package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import lu.crghost.cralib3.tools.HashCodeUtil;

import static android.provider.BaseColumns._ID;

/**
 * Costcenter model
 */
public class Costcenter extends BaseModel implements BaseModelInterface {

    private static final String TAG="Costcenter";

    public static final String TABLE_NAME = "costcenters";
    public static final String TABLE_SQLCRE =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  name TEXT NULL ,"+
            "  parent_id INT NULL ,"+
            "  clevel INT ,"+
            "  sort   INT ," +
            "  hassons INT," +
            "  ccttype INT," +
            "  isdefaultcct INT," +
            "  measure1_id INT NULL, " +
            "  measure2_id INT NULL, " +
            "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime'))  ,"+
            "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );";
    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "name",
            "parent_id",
            "clevel",
            "sort",
            "hassons",
            "ccttype",
            "isdefaultcct",
            "measure1_id",
            "measure2_id",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = "sort";

    private String name;
    private long parent_id;
    private int clevel;
    private int sort;
    private int hassons;
    private int ccttype;
    private int isdefaultcct;
    private long measure1_id;
    private long measure2_id;

    public static final int TYPE_INCOME  = 0;
    public static final int TYPE_EXPENSE = 1;
    //public static final int MAXLEVEL = 4;			// 1->4
    //public static final int MAXSTDID = 4;			// Id's 1-4 are fixed

    public Costcenter() {};
    public Costcenter(ContentValues c) { setValues(c); }
    public Costcenter(Cursor c) { setValues(c); }

    public String getTableName() { return TABLE_NAME; }
    public String getTableSqlCre() { return TABLE_SQLCRE; }
    public String[] getFieldNames() { return FIELD_NAMES; }

    /**
     * Create table
     * @param db
     */
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_SQLCRE);
        Log.i(TAG, TABLE_NAME + " created");

    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
    }

    @Override
    public ContentValues getContentValues(boolean withId) {
        ContentValues c = new ContentValues();
        if (withId) c.put(_ID,getId());
        c.put("name",getName());
        if (getParent_id()==0) c.putNull("parent_id");
        else c.put("parent_id",getParent_id());
        c.put("clevel",getClevel());
        c.put("sort",getSort());
        c.put("hassons",getHassons());
        c.put("ccttype",getCcttype());
        c.put("isdefaultcct",getIsdefaultcct());
        c.put("measure1_id",getMeasure1_id());
        c.put("measure2_id",getMeasure2_id());
        c.put("created_at",getCreated_at());
        c.put("updated_at",getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        setName(c.getAsString("name"));
        setParent_id(c.getAsLong("parent_id"));
        setClevel(c.getAsInteger("clevel"));
        setSort(c.getAsInteger("sort"));
        setHassons(c.getAsInteger("hassons"));
        setCcttype(c.getAsInteger("ccttype"));
        setIsdefaultcct(c.getAsInteger("isdefaultcct"));
        setMeasure1_id(c.getAsLong("measure1_id"));
        setMeasure2_id(c.getAsLong("measure2_id"));
    }

    @Override
    public void setValues(Cursor c) {
        if (c!=null) {
            id = c.getLong(c.getColumnIndex(_ID));
            ContentValues co = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(c,co);
            setValues(co);
        }
    }

    /*******************************************************************************************************************
     * Getters & setters
     *******************************************************************************************************************/

    public String getName() {
        return name;
    }

    public String getLevelName() {
        String levels = "";
        if (clevel > 0) {
            for (int i=0;i<=clevel;i++) {
                levels = levels + "- ";
            }
        }
        return (levels + name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        if (parent_id==null) this.parent_id = 0;
        else this.parent_id = parent_id;
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

    public long getMeasure1_id() {
        return measure1_id;
    }

    public void setMeasure1_id(Long measure1_id) {
        if (measure1_id==null) this.measure1_id=0;
        else this.measure1_id = measure1_id;
    }

    public long getMeasure2_id() {
        return measure2_id;
    }

    public void setMeasure2_id(Long measure2_id) {
        if (measure2_id==null) this.measure2_id=0;
        else this.measure2_id = measure2_id;
    }


    /*******************************************************************************************************************
     * Overrides
     *******************************************************************************************************************/

    @Override
    public String toString() {
        //return "Costcenter [id="+this.getId()+ " " + getName() +"]";
        return getLevelName();
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
