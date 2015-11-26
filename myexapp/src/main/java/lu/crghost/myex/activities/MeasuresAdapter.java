package lu.crghost.myex.activities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Measure;

import java.math.BigDecimal;
import java.util.List;

/**
 * Array for MeasuresList
 */
public class MeasuresAdapter extends ArrayAdapter<Measure> {

    private static final String TAG = "MeasuresAdapter";
    Context context;
    MyExApp app;

    public MeasuresAdapter(MyExApp application,Context context, List<Measure> measures) {
        super(context, R.layout.activity_measures_item, R.id.item_measure_name , measures);
        this.context = context;
        this.app = application;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        ViewHolder holder = (ViewHolder) listItem.getTag();
        if (holder==null) {
            holder = new ViewHolder(
                    (TextView) listItem.findViewById(R.id.item_measure_name),
                    (TextView) listItem.findViewById(R.id.item_measure_nameshort)
            );
            listItem.setTag(holder);
        }

        // fill with the data
        final Measure measure = this.getItem(position);
        holder.measure_name.setText(measure.getName());
        if (measure.getCost_per_measure()==null || measure.getCost_per_measure().compareTo(BigDecimal.ZERO)==0) {
            holder.measure_nameshort.setText(measure.getNameshort());
        } else {
            holder.measure_nameshort.setText(lu.crghost.cralib3.tools.Formats.formatDecimal(measure.getCost_per_measure(),Measure.DECIMALS) + "/" + measure.getNameshort() );
        }
        return listItem;
    }

    /**
     * Views container
     */
    private static class ViewHolder {
        protected final TextView measure_name;
        protected final TextView measure_nameshort;
        public ViewHolder(TextView measure_name,
                          TextView measure_nameshort
        ) {
            this.measure_name = measure_name;
            this.measure_nameshort = measure_nameshort;
        }

    }

}
