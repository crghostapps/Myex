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
    public static final int SHOW_NAME_LONG = 0;
    public static final int SHOW_NAME_SHORT = 1;
    private int showname;

    public SimpleMeasureAdapter(Context context, List<Measure> measures, int showname) {
        super(context, android.R.layout.simple_spinner_item, measures);
        this.context = context;
        this.showname = showname;
    }

    public SimpleMeasureAdapter(Context context, List<Measure> measures) {
        super(context, android.R.layout.simple_spinner_item, measures);
        this.context = context;
        this.showname = SHOW_NAME_SHORT;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        final TextView text1 = (TextView) listItem.findViewById(android.R.id.text1);

        // fill with the data
        final Measure measure = this.getItem(position);
        text1.setText(measure.getNameshort());
        if (showname==SHOW_NAME_LONG) {
            if (measure.getNameshort()==null) text1.setText(measure.getName());
            else text1.setText(measure.getName() + " (" + measure.getNameshort() + ")");
        }

        return listItem;
    }

}
