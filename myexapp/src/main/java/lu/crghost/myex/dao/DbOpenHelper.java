package lu.crghost.myex.dao;

import android.content.Context;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.util.Log;

import lu.crghost.myex.conf.MyExProperties;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.Costcenter;
import lu.crghost.myex.models.Debtor;
import lu.crghost.myex.models.Geotrack;
import lu.crghost.myex.models.Measure;
import lu.crghost.myex.models.Transaction;

/**
 * Created by CR on 24/12/2014.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DbOpenHelper";

    private Context context;

    DbOpenHelper(final Context context) {
        super(context, MyExProperties.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onOpen(final SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // versions of SQLite older than 3.6.19 don't support foreign keys
            // and neither do any version compiled with SQLITE_OMIT_FOREIGN_KEY
            // http://www.sqlite.org/foreignkeys.html#fk_enable
            //
            // make sure foreign key support is turned on if it's there (should be already, just a double-checker)
            db.execSQL("PRAGMA foreign_keys=ON;");

            // then we check to make sure they're on
            // (if this returns no data they aren't even available, so we shouldn't even TRY to use them)
            Cursor c = db.rawQuery("PRAGMA foreign_keys", null);
            if (c.moveToFirst()) {
                int result = c.getInt(0);
                Log.i(TAG, "SQLite foreign key support (1 is on, 0 is off): " + result);
            } else {
                // could use this approach in onCreate, and not rely on foreign keys it not available, etc.
                Log.i(TAG, "SQLite foreign key support NOT AVAILABLE");
                // if you had to here you could fall back to triggers
            }
            if (!c.isClosed()) {
                c.close();
            }
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.i(TAG, "DbOpenHelper onCreate creating database " + MyExProperties.DATABASE_NAME);
        Account.onCreate(db);
        Costcenter.onCreate(db);
        Debtor.onCreate(db);
        Geotrack.onCreate(db);
        Measure.onCreate(db);
        Transaction.onCreate(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.i(TAG, "DbOpenHelper onUpgrade - oldVersion:" + oldVersion + " newVersion:"+ newVersion);
        Account.onUpgrade(db, oldVersion, newVersion);
        Costcenter.onUpgrade(db, oldVersion, newVersion);
        Debtor.onUpgrade(db, oldVersion, newVersion);
        Geotrack.onUpgrade(db, oldVersion, newVersion);
        Measure.onUpgrade(db, oldVersion, newVersion);
        Transaction.onUpgrade(db, oldVersion, newVersion);
    }

}
