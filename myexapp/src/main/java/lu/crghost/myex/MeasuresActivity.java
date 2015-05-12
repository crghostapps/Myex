package lu.crghost.myex;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;


public class MeasuresActivity extends FragmentActivity {

    private static final String TAG = "MeasuresActivity";
    MyExApp app;

    private ListView mListView;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measures_list);

        app = (MyExApp) getApplication();
        mAdapter = new MeasuresAdapter(app,this,app.getDataManager().getMeasures(null, null));

        // Click on list
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListView.setClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                Bundle params = new Bundle();
                params.putLong("id", id);
                //Intent intent = new Intent(CustomersActivity.this, CustomerShowActivity.class);
                //intent.putExtras(params);
                //startActivity(intent);
            }

        });

        // Fill the list
        mAdapter = new MeasuresAdapter(app,this,app.getDataManager().getMeasures(null,null));
        mListView.setAdapter(mAdapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_measures, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
