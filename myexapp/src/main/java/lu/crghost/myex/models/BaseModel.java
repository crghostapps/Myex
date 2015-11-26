package lu.crghost.myex.models;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base model to be extended by all Models
 * Created by CR on 23/12/2014.
 */
public class BaseModel {

    protected long id;
    private String created_at;
    private String updated_at;

    public static final SimpleDateFormat sqlDateFormat   = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat sqlDateTimeFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public long getId() {
        return id;
    }

    public String getIdAsString() { return String.valueOf(id); }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreated_at() {
        if (created_at==null) created_at = sqlDateTimeFormat.format(new Date(System.currentTimeMillis()));
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        if (updated_at==null) updated_at = sqlDateTimeFormat.format(new Date(System.currentTimeMillis()));
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    protected boolean int_to_boolean(int i) {
        if (i==1) return true;
        return false;
    }
    protected int boolean_to_int(boolean b) {
        if (b) return 1;
        return 0;
    }

    public BigDecimal doubleToBigDecimal(Double d) {
        if (d==null) return BigDecimal.ZERO;
        else return new BigDecimal(d.doubleValue());
    }

    public long longTolong(Long l) {
        if (l==null) return 0;
        else return l;
    }

    @Override
    public String toString() {
        return "BaseModel [id=" + this.id + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BaseModel)) {
            return false;
        }
        BaseModel other = (BaseModel) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param i
     * @return
     */
    public static boolean toBoolean(int i) {
        if (i==1) return true;
        return false;
    }

    public static int toInt(boolean b) {
        if (b) return 1;
        else return 0;
    }


}
