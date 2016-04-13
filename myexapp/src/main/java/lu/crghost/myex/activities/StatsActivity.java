package lu.crghost.myex.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import lu.crghost.myex.R;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);


        LineChart chart = (LineChart) findViewById(R.id.chart);

        List<String> xvalues = new ArrayList<String>();
        List<Entry> yvalues = new ArrayList<Entry>();
        for (int i=0; i<=12;i++) {
            Entry entry = new Entry((float) i, i);
            yvalues.add(entry);
            xvalues.add(String.valueOf(i));
        }
        LineDataSet lineDataSet = new LineDataSet(yvalues,"Yvalues");
        LineData data = new LineData(xvalues, lineDataSet);

        chart.setData(data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
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
}
