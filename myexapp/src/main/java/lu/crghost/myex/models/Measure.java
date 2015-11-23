package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import lu.crghost.cralib3.tools.HashCodeUtil;

import java.math.BigDecimal;

import static android.provider.BaseColumns._ID;

/**
 * Created by CR on 23/12/2014.
 */
public class Measure extends BaseModel implements BaseModelInterface {

    private static final String TAG="Measure";

    public static final String TABLE_NAME = "measures";

    public static final String TABLE_SQLCRE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name TEXT NULL ,"+
            " nameshort TEXT," +
            " cost_per_measure NUMERIC," +
            " created_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
            " updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );";

    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "name",
            "nameshort",
            "cost_per_measure",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = "name";

    private String name;
    private String nameshort;
    private BigDecimal cost_per_measure;

    /**
     * Initiate empty model
     */
    public Measure() {}

    /**
     * Initiate from content values
     * @param c
     */
    public Measure(ContentValues c) {
        setValues(c);
    }

    /**
     * Initiate from db cursor
     * @param c
     */
    public Measure(Cursor c) {
        setValues(c);
    }

    /**
     * Create table
     * @param db
     */
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_SQLCRE);
        Log.i(TAG, TABLE_NAME + " created");

    }

    public String getTableName() { return TABLE_NAME; }
    public String getTableSqlCre() { return TABLE_SQLCRE; }
    public String[] getFieldNames() { return FIELD_NAMES; }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
    }

    @Override
    public ContentValues getContentValues(boolean withId) {
        ContentValues c = new ContentValues();
        if (withId) c.put(_ID,getId());
        c.put("name",getName());
        c.put("nameshort",getNameshort());
        c.put("cost_per_measure", getCost_per_measure().doubleValue());
        c.put("created_at",getCreated_at());
        c.put("updated_at",getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        setName(c.getAsString("name"));
        setNameshort(c.getAsString("nameshort"));
        setCost_per_measure(c.getAsDouble("cost_per_measure"));
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

    public boolean isCurrency() {
        if (getId()==1) return true;
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameshort() {
        return nameshort;
    }

    public void setNameshort(String nameshort) {
        this.nameshort = nameshort;
    }

    public BigDecimal getCost_per_measure() {
        return cost_per_measure;
    }

    public void setCost_per_measure(Double cost_per_measure) {
        if (cost_per_measure==null) this.cost_per_measure = BigDecimal.ZERO;
        else this.cost_per_measure = new BigDecimal(cost_per_measure);
    }

    public void setCost_per_measure(BigDecimal cost_per_measure) {
        this.cost_per_measure = cost_per_measure;
    }


    /**
     * Used in dropdown lists
     * @return
     */
    @Override
    //public String toString() {
    //    return "Measure [id="+this.getId()+ " " + getName() +"]";
    //}
    public String toString() {
        return getName();
    }

    /*******************************************************************************************************************
     * Overrides
     *******************************************************************************************************************/

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
        if (!(obj instanceof Measure)) {
            return false;
        }
        Measure other = (Measure) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }


}
