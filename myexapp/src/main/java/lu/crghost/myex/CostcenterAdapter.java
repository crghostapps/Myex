package lu.crghost.myex;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import lu.crghost.myex.models.Costcenter;

import java.util.List;

/**
 * Simple costcenter adapter
 */
public class CostcenterAdapter extends ArrayAdapter<Costcenter> {

    private static final String TAG = "CostcenterAdapter";
    Context context;

    public CostcenterAdapter(Context context, List<Costcenter> costcenters) {
        super(context, android.R.layout.simple_spinner_item, costcenters);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        final TextView text1 = (TextView) listItem.findViewById(android.R.id.text1);

        // fill with the data
        final Costcenter costcenter = this.getItem(position);
        text1.setText(costcenter.getName());

        return listItem;
    }

}
