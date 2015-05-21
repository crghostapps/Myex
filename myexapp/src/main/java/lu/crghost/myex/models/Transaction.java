package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Date;

import lu.crghost.cralib.tools.HashCodeUtil;

import static android.provider.BaseColumns._ID;

/**
 * Created by CR on 23/12/2014.
 */
public class Transaction extends BaseModel implements BaseModelInterface {

    private static final String TAG="Transaction";

    public static final String TABLE_NAME = "transactions";
    public static final String TABLE_SQLCRE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  description TEXT NULL ,"+
            "  transtype INT NULL, " +
            "  costcenter_id INT NULL ,"+
            "  account_id INT NULL ,"+
            "  account_target_id INT NULL ,"+
            "  debitor_id INT NULL ," +
            "  amount NUMERIC NULL ,"+
            "  measure1 NUMERIC NULL," +
            "  measure1_id INT NULL," +
            "  measure2 NUMERIC NULL," +
            "  measure2_id INT NULL," +
            "  latitude NUMERIC NULL," +
            "  longitude NUMERIC NULL,"+
            "  altitude  NUMERIC NULL,"+
            "  amount_at  TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
            "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
            "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );";
    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "description",
            "transtype",
            "costcenter_id",
            "account_id",
            "account_target_id",
            "debitor_id",
            "amount",
            "measure1",
            "measure1_id",
            "measure2",
            "measure2_id",
            "latitude",
            "longitude",
            "altitude",
            "amount_at",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = "amount_at desc";

    public static final int DEBIT  = 0;		// -
    public static final int CREDIT = 1;		// +

    private String description;
    private int transtype;
    private long costcenter_id;
    private long account_id;
    private long account_target_id;
    private long debitor_id;
    private BigDecimal amount;
    private BigDecimal measure1;
    private long measure1_id;
    private BigDecimal measure2;
    private long measure2_id;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;
    private String amount_at;

    /**
     * Initiate empty model
     */
    public Transaction() {}

    /**
     * Initiate from content values
     * @param c
     */
    public Transaction(ContentValues c) {
        setValues(c);
    }

    /**
     * Initiate from db cursor
     * @param c
     */
    public Transaction(Cursor c) {
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

    @Override
    public ContentValues getContentValues(boolean withId) {
        ContentValues c = new ContentValues();
        if (withId) c.put(_ID,getId());
        c.put("description",getDescription());
        c.put("transtype",getTranstype());
        c.put("costcenter_id",getCostcenter_id());
        c.put("account_id",getAccount_id());
        c.put("account_target_id",getAccount_target_id());
        c.put("debitor_id",getDebitor_id());
        c.put("amount",getAmount().doubleValue());
        c.put("measure1",getMeasure1().doubleValue());
        c.put("measure1_id",getMeasure1_id());
        c.put("measure2",getMeasure2().doubleValue());
        c.put("measure2_id",getMeasure2_id());
        c.put("latitude",getLatitude().doubleValue());
        c.put("longitude",getLongitude().doubleValue());
        c.put("altitude",getAltitude().doubleValue());
        c.put("amount_at",getAmount_at());
        c.put("created_at",getCreated_at());
        c.put("updated_at",getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        description = c.getAsString("description");
        transtype = c.getAsInteger("transtype");
        costcenter_id = c.getAsLong("costcenter_id");
        account_id = c.getAsLong("account_id");
        account_target_id = c.getAsLong("account_target_id");
        debitor_id = c.getAsLong("debitor_id");
        amount = new BigDecimal(c.getAsDouble("amount"));
        measure1 = new BigDecimal(c.getAsDouble("measure1"));
        measure1_id = c.getAsLong("measure1_id");
        measure2 = new BigDecimal(c.getAsDouble("measure2"));
        measure2_id = c.getAsLong("measure2_id");
        latitude = new BigDecimal(c.getAsDouble("latitude"));
        longitude = new BigDecimal(c.getAsDouble("longitude"));
        altitude = new BigDecimal(c.getAsDouble("altitude"));
        amount_at = c.getAsString("amount_at");
    }

    @Override
    public void setValues(Cursor c) {
        if (c!=null) {
            setId(c.getLong(0));
            setDescription(c.getString(1));
            setTranstype(c.getInt(2));
            setCostcenter_id(c.getLong(3));
            setAccount_id(c.getLong(4));
            setAccount_target_id(c.getLong(5));
            setDebitor_id(c.getLong(6));
            setAmount(new BigDecimal(c.getDouble(7)));
            setMeasure1(new BigDecimal(c.getDouble(8)));
            setMeasure1_id(c.getLong(9));
            setMeasure2(new BigDecimal(c.getDouble(10)));
            setMeasure2_id(c.getLong(11));
            setLatitude(new BigDecimal(c.getDouble(12)));
            setLongitude(new BigDecimal(c.getDouble(13)));
            setAltitude(new BigDecimal(c.getDouble(14)));
            setAmount_at(c.getString(15));
            setCreated_at(c.getString(16));
            //setUpdated_at(c.getString(17));
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTranstype() {
        return transtype;
    }

    public void setTranstype(int transtype) {
        this.transtype = transtype;
    }

    public long getCostcenter_id() {
        return costcenter_id;
    }

    public void setCostcenter_id(long costcenter_id) {
        this.costcenter_id = costcenter_id;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public long getAccount_target_id() {
        return account_target_id;
    }

    public void setAccount_target_id(long account_target_id) {
        this.account_target_id = account_target_id;
    }

    public long getDebitor_id() {
        return debitor_id;
    }

    public void setDebitor_id(long debitor_id) {
        this.debitor_id = debitor_id;
    }

    public BigDecimal getAmount() {
        if (amount==null) return BigDecimal.ZERO;
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMeasure2() {
        if (measure2==null) return BigDecimal.ZERO;
        return measure2;
    }

    public void setMeasure2(BigDecimal measure) {
        this.measure2 = measure;
    }

    public long getMeasure2_id() {
        return measure2_id;
    }

    public void setMeasure2_id(long measure_id) {
        this.measure2_id = measure_id;
    }

    public BigDecimal getMeasure1() {
        if (measure1==null) return BigDecimal.ZERO;
        return measure1;
    }

    public void setMeasure1(BigDecimal measure) {
        this.measure1 = measure;
    }

    public long getMeasure1_id() {
        return measure1_id;
    }

    public void setMeasure1_id(long measure_id) {
        this.measure1_id = measure_id;
    }

    public BigDecimal getLatitude() {
        if (latitude==null) return BigDecimal.ZERO;
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        if (longitude==null) return BigDecimal.ZERO;
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getAmount_at() {
        return amount_at;
    }

    public void setAmount_at(String amount_at) {
        this.amount_at = amount_at;
    }

    public Date getDateAmount_at() {
        Date date = null;
        if (amount_at!=null) {
            try {
                date = sqlDateTimeFormat.parse(amount_at);
            } catch(Exception e) { }
        }
        return date;
    }

    public void setDateAmount_at(Date date) {
        String s = null;
        if (date!=null) {
            try {
                amount_at = sqlDateTimeFormat.format(date);
            } catch(Exception e) {}
        }
        amount_at = s;
    }



    @Override
    public String toString() {
        return "Transaction [id="+this.getId()+ " " + getDescription() +"]";
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
        if (!(obj instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }

    public BigDecimal getAltitude() {
        if (altitude==null) return BigDecimal.ZERO;
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }
}
