package lu.crghost.myex.activities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Debtor;
import lu.crghost.myex.tools.MyFormats;

import java.util.ArrayList;
import java.util.List;

/**
 * Array adapter for debtors list
 */
public class DebtorsAdapter extends ArrayAdapter<Debtor> implements Filterable {

    private static final String TAG = "DebtorsAdapter";
    Context context;
    MyExApp app;

    List<Debtor> initialDebtorList;
    List<Debtor> filteredDebtorList;

    public DebtorsAdapter(MyExApp application,Context context, List<Debtor> debtors) {
        super(context, R.layout.fragment_debtors_item, R.id.item_debtor_name , debtors);
        this.context = context;
        this.app = application;
        this.initialDebtorList = debtors;
        this.filteredDebtorList = debtors;
    }

    @Override
    public int getCount() {
        return filteredDebtorList.size();
    }

    @Override
    public Debtor getItem(int position) {
        return filteredDebtorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Debtor debtor = filteredDebtorList.get(position);
        return debtor.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        ViewHolder holder = (ViewHolder) listItem.getTag();
        if (holder==null) {
            holder = new ViewHolder(
                    (TextView) listItem.findViewById(R.id.item_debtor_name),
                    (TextView) listItem.findViewById(R.id.item_debtor_amount)
            );
            listItem.setTag(holder);
        }

        // fill with the data
        final Debtor debtor = this.getItem(position);
        holder.debtor_name.setText(debtor.getName());
        holder.debtor_amount.setText(MyFormats.formatDecimal(app.getDataManager().getDebtorTotalAmount(debtor.getId()),2)+app.getCurrencySymbol());
        return listItem;
    }

    /**
     * Views container
     */
    private static class ViewHolder {
        protected final TextView debtor_name;
        protected final TextView debtor_amount;
        public ViewHolder(TextView debtor_name,
                          TextView debtor_amount
                          ) {
            this.debtor_name = debtor_name;
            this.debtor_amount = debtor_amount;
        }

    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    /**
     * Filter on debtor name
     */
    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Debtor> tempList = new ArrayList<Debtor>();
            for (Debtor debtor : initialDebtorList) {
                if (debtor.getName().contains(constraint)) {
                    tempList.add(debtor);
                }
            }
            filterResults.values = tempList;
            filterResults.count  = tempList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint==null || results==null) filteredDebtorList = initialDebtorList;
            else filteredDebtorList = (ArrayList<Debtor>) results.values;
            notifyDataSetChanged();
        }
    };

}
