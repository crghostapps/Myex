package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import net.sqlcipher.database.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Date;

import lu.crghost.cralib3.tools.HashCodeUtil;

import static android.provider.BaseColumns._ID;

/**
 * Transaction model
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
            "  debtor_id INT NULL ," +
            "  amount NUMERIC NULL ,"+          // amount in currency
            "  amountbase NUMERIC NULL, " +     // amount in base currency
            "  measure1 NUMERIC NULL," +
            "  measure1_id INT NULL," +
            "  measure2 NUMERIC NULL," +
            "  measure2_id INT NULL," +
            "  latitude NUMERIC NULL," +
            "  longitude NUMERIC NULL,"+
            "  altitude  NUMERIC NULL,"+
            "  amount_at  TEXT ,"+
            "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
            "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );";
    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "description",
            "transtype",
            "costcenter_id",
            "account_id",
            "account_target_id",
            "debtor_id",
            "amount",
            "amountbase",
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
    private long debtor_id;
    private BigDecimal amount;
    private BigDecimal amountbase;
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
    public Transaction() {
        setAmount(0d);
        setAmountbase(0d);
        setMeasure1(0d);
        setMeasure2(0d);
        setLatitude(0d);
        setLongitude(0d);
        setAltitude(0d);
    }

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
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
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
        c.put("debtor_id",getDebtor_id());
        c.put("amount",getAmount().doubleValue());
        c.put("amountbase",getAmountbase().doubleValue());
        c.put("measure1",getMeasure1().doubleValue());
        c.put("measure1_id",getMeasure1_id());
        c.put("measure2",getMeasure2().doubleValue());
        c.put("measure2_id",getMeasure2_id());
        c.put("latitude",getLatitude().doubleValue());
        c.put("longitude",getLongitude().doubleValue());
        c.put("altitude",getAltitude().doubleValue());
        c.put("amount_at",getAmount_at());
        c.put("created_at", getCreated_at());
        c.put("updated_at", getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        setDescription(c.getAsString("description"));
        setTranstype(c.getAsInteger("transtype"));
        setCostcenter_id(c.getAsLong("costcenter_id"));
        setAccount_id(c.getAsLong("account_id"));
        setAccount_target_id(c.getAsLong("account_target_id"));
        setDebtor_id(c.getAsLong("debtor_id"));
        setAmount(c.getAsDouble("amount"));
        setAmountbase(c.getAsDouble("amountbase"));
        setMeasure1(c.getAsDouble("measure1"));
        setMeasure1_id(c.getAsLong("measure1_id"));
        setMeasure2(c.getAsDouble("measure2"));
        setMeasure2_id(c.getAsLong("measure2_id"));
        setLatitude(c.getAsDouble("latitude"));
        setLongitude(c.getAsDouble("longitude"));
        setAltitude(c.getAsDouble("altitude"));
        setAmount_at(c.getAsString("amount_at"));
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

    public void setCostcenter_id(Long costcenter_id) {
        this.costcenter_id= longTolong(costcenter_id);
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

    public void setAccount_target_id(Long account_target_id) {
        this.account_target_id = longTolong(account_target_id);
    }

    public long getDebtor_id() {
        return debtor_id;
    }

    public void setDebtor_id(Long debtor_id) {
        this.debtor_id = longTolong(debtor_id);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setAmount(Double amount) {
        this.amount = doubleToBigDecimal(amount);
    }

    public BigDecimal getAmountbase() {
        return amountbase;
    }

    public void setAmountbase(BigDecimal amountbase) {
        this.amountbase = amountbase;
    }
    public void setAmountbase(Double amountbase) {
        this.amountbase = doubleToBigDecimal(amountbase);
    }

    public BigDecimal getMeasure2() {
        return measure2;
    }

    public void setMeasure2(BigDecimal measure) {
        this.measure2 = measure;
    }
    public void setMeasure2(Double measure) {
        this.measure2 = doubleToBigDecimal(measure);
    }

    public long getMeasure2_id() {
        return measure2_id;
    }

    public void setMeasure2_id(Long measure_id) {
        this.measure2_id = longTolong(measure_id);
    }

    public BigDecimal getMeasure1() {
        return measure1;
    }

    public void setMeasure1(BigDecimal measure) {
        this.measure1 = measure;
    }
    public void setMeasure1(Double measure) {
        this.measure1 = doubleToBigDecimal(measure);
    }

    public long getMeasure1_id() {
        return measure1_id;
    }

    public void setMeasure1_id(Long measure_id) {
        this.measure1_id = longTolong(measure_id);
    }


    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = doubleToBigDecimal(latitude);
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = doubleToBigDecimal(longitude);
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
                s = sqlDateTimeFormat.format(date);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        amount_at = s;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }
    public void setAltitude(Double altitude) {
        this.altitude = doubleToBigDecimal(altitude);
    }

    public Location getLocation() {
        Location l = new Location("transaction");
        l.setLatitude(getLatitude().doubleValue());
        l.setLongitude(getLongitude().doubleValue());
        l.setAltitude(getAltitude().doubleValue());
        return l;
    }

    public void setLocation(Location location) {
        setLongitude(location.getLongitude());
        setLatitude(location.getLatitude());
        setAltitude(location.getAltitude());
    }

    public LatLng getLatLng() {
        return new LatLng(getLatitude().doubleValue(), getLongitude().doubleValue());
    }

    public void setLatLng(LatLng latLng) {
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
    }



    /*******************************************************************************************************************
     * Overrides
     *******************************************************************************************************************/

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


}
