package lu.crghost.myex;

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
public class MeasureAdapter extends ArrayAdapter<Measure> {

    private static final String TAG = "AccountsAdapter";
    Context context;

    public MeasureAdapter(Context context, List<Measure> measures) {
        super(context, R.layout.simple_list_item, measures);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        final TextView text1 = (TextView) listItem.findViewById(R.id.text1);

        // fill with the data
        final Measure measure = this.getItem(position);
        text1.setText(measure.getName());

        return listItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            TextView text = (TextView) convertView.findViewById(R.id.text1);
            text.setText("????????????");
            text.setHeight(100);
        }

        return convertView;
    }
}
