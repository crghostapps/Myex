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
 * Created by CR on 23/12/2014.
 */
public class Geotrack extends BaseModel implements BaseModelInterface {

    private static final String TAG="Geotrack";

    public static final String TABLE_NAME = "geotracks";
    public static final String TABLE_SQLCRE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  latitude NUMERIC NULL," +
            "  longitude NUMERIC NULL,"+
            "  altitude NUMERIC NULL,"+
            "  status INT NULL," +
            "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
            "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );";

    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "latitude",
            "longitude",
            "altitude",
            "status",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = _ID;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;
    private int status;

    public Geotrack() {};
    public Geotrack(ContentValues c) { setValues(c); }
    public Geotrack(Cursor c) { setValues(c); }

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
        c.put("latitude",getLatitude().doubleValue());
        c.put("longitude",getLongitude().doubleValue());
        c.put("altitude",getAltitude().doubleValue());
        c.put("status", getStatus());
        c.put("created_at",getCreated_at());
        c.put("updated_at",getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        setLatitude(c.getAsDouble("latitude"));
        setLongitude(c.getAsDouble("longitude"));
        setAltitude(c.getAsDouble("altitude"));
        setStatus(c.getAsInteger("status"));
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    public void setLatitude(Double d) {
        if (d==null) this.latitude = BigDecimal.ZERO;
        else this.latitude = new BigDecimal(d.doubleValue());
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    public void setLongitude(Double d) {
        if (d==null) this.longitude = BigDecimal.ZERO;
        else this.longitude = new BigDecimal(d.doubleValue());
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }
    public void setAltitude(Double d) {
        if (d==null) this.altitude = BigDecimal.ZERO;
        else this.altitude = new BigDecimal(d.doubleValue());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*******************************************************************************************************************
     * Overrides
     *******************************************************************************************************************/

    @Override
    public String toString() {
        return "Geotrack [id="+this.getId()+"]";
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
        if (!(obj instanceof Geotrack)) {
            return false;
        }
        Geotrack other = (Geotrack) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }
}
