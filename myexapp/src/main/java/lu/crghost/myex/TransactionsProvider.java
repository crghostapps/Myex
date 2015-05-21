package lu.crghost.myex;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Content provider for transactions
 */
public class TransactionsProvider extends ContentProvider {

    public static final String TAG = "TransactionsProvider";

    public static final String PROVIDER_NAME = "lu.crghost.myex.trans";

    /** A uri to do operations on prices table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/transactions" );

    /** Constants to identify the requested operation */
    private static final int TRANSACTIONS = 1;
    private static final int TRANSACTION_ID = 2;

    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "transactions", TRANSACTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "transactions/#", TRANSACTION_ID);
    }

    /** This content provider does the database operations by this object */
    MyExApp app;


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        app = (MyExApp) getContext().getApplicationContext();
        return false;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri)==TRANSACTIONS){
            Cursor c = app.getDataManager().getTransactionsCursor(selection,selectionArgs,sortOrder);
            return c;
        }else{
            String id = uri.getPathSegments().get(1);
            return app.getDataManager().getTransactionCursorById(Long.parseLong(id));
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
