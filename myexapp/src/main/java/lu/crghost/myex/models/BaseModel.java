package lu.crghost.myex.models;

import android.content.ContentValues;

/**
 * Base model to be extended by all Models
 * Created by CR on 23/12/2014.
 */
public class BaseModel {

    protected long id;
    private String created_at;
    private String updated_at;

    public long getId() {
        return id;
    }

    public String getIdAsString() { return String.valueOf(id); }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
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


}
