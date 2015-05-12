package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.math.BigDecimal;

import lu.crghost.cralib.tools.HashCodeUtil;

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
            "  currency_id INTEGER," +
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
            "currency_id",
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
    private long currency_id;

    /**
     * Initiate empty model
     */
    public Account() {}

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
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
        c.put("currency_id", getCurrency_id());
        c.put("created_at", getCreated_at());
        c.put("updated_at", getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        acname =  c.getAsString("acname");
        acnumber = c.getAsString("acnumber");
        actype   = c.getAsInteger("actype");
        iconpath =  c.getAsString("iconpath");
        currency_id = c.getAsLong("currency_id");
        initbalance = new BigDecimal(c.getAsDouble("initbalance"));
        limitamount = new BigDecimal(c.getAsDouble("limitamount"));
    }

    @Override
    public void setValues(Cursor c) {
        if (c!=null) {
            setId(c.getLong(0));
            setAcname(c.getString(1));
            setAcnumber(c.getString(2));
            setActype(c.getInt(3));
            setIconpath(c.getString(4));
            setInitbalance(new BigDecimal(c.getDouble(5)));
            setLimitamount(new BigDecimal(c.getDouble(6)));
            setCurrency_id(c.getLong(7));
            setCreated_at(c.getString(8));
            setUpdated_at(c.getString(9));
        }
    }

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

    public BigDecimal getLimitamount() {
        return limitamount;
    }

    public void setLimitamount(BigDecimal limitamount) {
        this.limitamount = limitamount;
    }

    public long getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(long currency_id) {
        this.currency_id = currency_id;
    }


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
