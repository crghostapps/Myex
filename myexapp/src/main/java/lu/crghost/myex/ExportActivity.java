package lu.crghost.myex;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import net.sqlcipher.database.SQLiteDatabase;


public class ExportActivity extends Activity {

    private static final String TAG = "ExportActivity";

    private MyExApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        app = (MyExApp) getApplication();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void action_export_transactions(View v) {

    }

    public void action_export_backup(View v) {

        // https://www.zetetic.net/sqlcipher/sqlcipher-api/#sqlcipher_export
        SQLiteDatabase db = app.getDataManager().getDb();
        String sql = "ATTACH DATABASE 'myexdbclean.db' AS myexdbclean KEY ''";
        db.rawExecSQL(sql);
        db.rawExecSQL("SELECT sqlcipher_export('myexdbclean')");
        db.rawExecSQL("DETACH DATABASE myexdbclean");

        Log.d(TAG, "Db recryped");

    }
}
