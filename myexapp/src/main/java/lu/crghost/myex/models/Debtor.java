package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.math.BigDecimal;

import lu.crghost.cralib.tools.HashCodeUtil;

import static android.provider.BaseColumns._ID;

/**
 * Created by CR on 23/12/2014.
 */
public class Debtor extends BaseModel implements BaseModelInterface {

    private static final String TAG="Debtor";

    public static final String TABLE_NAME = "debtors";
    public static final String[] FIELD_NAMES = new String[] {
            BaseColumns._ID,
            "name",
            "latitude",
            "longitude",
            "altitude",
            "created_at",
            "updated_at"
    };
    public static final String SORT_ORDER = "name";

    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;

    public Debtor() {};
    public Debtor(ContentValues c) { setValues(c); }
    public Debtor(Cursor c) { setValues(c); }

    /**
     * Create table
     * @param db
     */
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        "  name TEXT NULL ,"+
                        "  latitude  NUMERIC NULL," +
                        "  longitude NUMERIC NULL,"+
                        "  altitude  NUMERIC NULL,"+
                        "  created_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) ,"+
                        "  updated_at TEXT DEFAULT (datetime(current_timestamp,'localtime')) );");
        Log.i(TAG, TABLE_NAME + " created");
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
        c.put("latitude",getLatitude().doubleValue());
        c.put("longitude",getLongitude().doubleValue());
        c.put("altitude",getAltitude().doubleValue());
        c.put("created_at",getCreated_at());
        c.put("updated_at",getUpdated_at());
        return c;
    }

    @Override
    public void setValues(ContentValues c) {
        name = c.getAsString("name");
        latitude = new BigDecimal(c.getAsDouble("latitude"));
        longitude = new BigDecimal(c.getAsDouble("longitude"));
        altitude = new BigDecimal(c.getAsDouble("altitude"));
    }

    @Override
    public void setValues(Cursor c) {
        if (c!=null) {
            setId(c.getLong(0));
            setName(c.getString(1));
            setLatitude(new BigDecimal(c.getDouble(2)));
            setLongitude(new BigDecimal(c.getDouble(3)));
            setAltitude(new BigDecimal(c.getDouble(4)));
            setCreated_at(c.getString(5));
            setUpdated_at(c.getString(6));
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "Debtor [id="+this.getId()+ " " + getName() +"]";
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
        if (!(obj instanceof Debtor)) {
            return false;
        }
        Debtor other = (Debtor) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }
}
