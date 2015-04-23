package lu.crghost.myex.models;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by CR on 23/12/2014.
 */
public interface BaseModelInterface {
    public ContentValues getContentValues(boolean withId);
    public void setValues(ContentValues c);
    public void setValues(Cursor c);
}
