package lu.crghost.myex.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import lu.crghost.myex.models.Measure;

import java.util.List;

/**
 * Simple Measure adapter
 */
public class SimpleMeasureAdapter extends ArrayAdapter<Measure> {

    private static final String TAG = "MeasureAdapter";
    Context context;

    public SimpleMeasureAdapter(Context context, List<Measure> measures) {
        super(context, android.R.layout.simple_spinner_item, measures);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        final TextView text1 = (TextView) listItem.findViewById(android.R.id.text1);

        // fill with the data
        final Measure measure = this.getItem(position);
        text1.setText(measure.getNameshort());

        return listItem;
    }

}
