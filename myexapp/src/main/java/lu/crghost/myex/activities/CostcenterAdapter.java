package lu.crghost.myex.activities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import lu.crghost.myex.models.Costcenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple costcenter adapter
 */
public class CostcenterAdapter extends ArrayAdapter<Costcenter> implements Filterable {

    private static final String TAG = "CostcenterAdapter";
    Context context;

    List<Costcenter> initialCostcenterList;
    List<Costcenter> filteredCostcenterList;

    public CostcenterAdapter(Context context, List<Costcenter> costcenters) {
        super(context, android.R.layout.simple_spinner_item, costcenters);
        this.context = context;
        this.initialCostcenterList = costcenters;
        this.filteredCostcenterList = costcenters;
    }

    @Override
    public int getCount() {
        return filteredCostcenterList.size();
    }

    @Override
    public Costcenter getItem(int position) {
        return filteredCostcenterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Costcenter costcenter = filteredCostcenterList.get(position);
        return costcenter.getId();
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

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    /**
     * Filter on costcenter name
     */
    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Costcenter> tempList = new ArrayList<Costcenter>();
            for (Costcenter costcenter : initialCostcenterList) {
                if (costcenter.getName().contains(constraint)) {
                    tempList.add(costcenter);
                }
            }
            filterResults.values = tempList;
            filterResults.count  = tempList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint==null || results==null) filteredCostcenterList = initialCostcenterList;
            else filteredCostcenterList = (ArrayList<Costcenter>) results.values;
            notifyDataSetChanged();
        }
    };

}
