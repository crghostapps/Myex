package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.math.BigDecimal;

import lu.crghost.cralib3.tools.HashCodeUtil;

import static android.provider.BaseColumns._ID;

/**
 * Account model
 * Created by CR on 23/12/2014.
 */
public class Account extends BaseModel implements BaseModelInterface {

    private static final String TAG="Account";

    public static final String TABLE_NAME = "accounts";

    public static final String TABLE_SQLCRE =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  acname TEXT NULL ,"+
            "  acnumber TEXT NULL ,"+
            "  actype INT NULL ,"+
            "  iconpath TEXT NULL," +
            "  initbalance NUMERIC, " +
            "  limitamount NUMERIC," +
            "  measure_id INTEGER," +
            "  costcenter_id INTEGER," +
            "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
            "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );"
            ;
    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "acname",
            "acnumber",
            "actype",
            "iconpath",
            "initbalance",
            "limitamount",
            "measure_id",
            "costcenter_id",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = "actype, acname";

    public static final int TYPE_WALLET = 0;
    public static final int TYPE_BANK   = 1;
    public static final int TYPE_CARD   = 2;
    public static final int TYPE_COUNTER= 3;

    private String acname;
    private String acnumber;
    private int actype;
    private String iconpath;
    private BigDecimal initbalance;
    private BigDecimal limitamount;
    private long measure_id;
    private long costcenter_id;
    /**
     * Initiate empty model
     */
    public Account() {
        setInitbalance(0d);
        setLimitamount(0d);
    }

    /**
     * Initiate from content values
     * @param c
     */
    public Account(ContentValues c) {
        setValues(c);
    }

    /**
     * Initiate from db cursor
     * @param c
     */
    public Account(Cursor c) {
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

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
    }

    public String getTableName() { return TABLE_NAME; }
    public String getTableSqlCre() { return TABLE_SQLCRE; }
    public String[] getFieldNames() { return FIELD_NAMES; }

    public ContentValues getContentValues(boolean withId) {
        ContentValues c = new ContentValues();
        if (withId) c.put(_ID,getId());
        c.put("acname", getAcname());
        c.put("acnumber", getAcnumber());
        c.put("actype", getActype());
        c.put("iconpath", getIconpath());
        c.put("initbalance", getInitbalance().doubleValue());
        c.put("limitamount", getLimitamount().doubleValue());
        c.put("measure_id", getMeasure_id());
        c.put("costcenter_id", getCostcenter_id());
        c.put("created_at", getCreated_at());
        c.put("updated_at", getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        setAcname(c.getAsString("acname"));
        setAcnumber(c.getAsString("acnumber"));
        setActype(c.getAsInteger("actype"));
        setIconpath(c.getAsString("iconpath"));
        setMeasure_id(c.getAsLong("measure_id"));
        setCostcenter_id(c.getAsLong("costcenter_id"));
        setInitbalance(c.getAsDouble("initbalance"));
        setLimitamount(c.getAsDouble("limitamount"));
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

    public String getAcname() {
        return acname;
    }

    public void setAcname(String acname) {
        this.acname = acname;
    }

    public String getAcnumber() {
        return acnumber;
    }

    public void setAcnumber(String acnumber) {
        this.acnumber = acnumber;
    }

    public int getActype() {
        return actype;
    }

    public void setActype(int actype) {
        this.actype = actype;
    }

    public String getIconpath() {
        return iconpath;
    }

    public void setIconpath(String iconpath) {
        this.iconpath = iconpath;
    }

    public BigDecimal getInitbalance() {
        return initbalance;
    }

    public void setInitbalance(BigDecimal initbalance) {
        this.initbalance = initbalance;
    }

    public void setInitbalance(Double initbalance) {
        if (initbalance==null) this.initbalance = BigDecimal.ZERO;
        else this.initbalance = new BigDecimal(initbalance.doubleValue());
    }

    public BigDecimal getLimitamount() {
        return limitamount;
    }

    public void setLimitamount(BigDecimal limitamount) {
        this.limitamount = limitamount;
    }
    public void setLimitamount(Double limitamount) {
        if (limitamount==null) this.limitamount = BigDecimal.ZERO;
        else this.limitamount = new BigDecimal(limitamount.doubleValue());
    }

    public long getMeasure_id() {
        return measure_id;
    }

    public void setMeasure_id(Long measure_id) {
        if (measure_id==null) this.measure_id=0;
        else this.measure_id = measure_id;
    }

    public long getCostcenter_id() {
        return costcenter_id;
    }

    public void setCostcenter_id(long costcenter_id) {
        this.costcenter_id = costcenter_id;
    }


    /*******************************************************************************************************************
     * Overrides
     *******************************************************************************************************************/

    @Override
    public String toString() {
        return "Account [id="+this.getId()+ " " + getAcname() +"]";
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
        if (!(obj instanceof Account)) {
            return false;
        }
        Account other = (Account) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }



}
