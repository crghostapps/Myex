package lu.crghost.myex;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import lu.crghost.cralib.tools.Formats;
import lu.crghost.myex.models.Debtor;

import java.util.List;

/**
 * Array adapter for debtors list
 */
public class DebtorsAdapter extends ArrayAdapter<Debtor> {

    private static final String TAG = "DebtorsAdapter";
    Context context;
    MyExApp app;

    public DebtorsAdapter(MyExApp application,Context context, List<Debtor> debtors) {
        super(context, R.layout.fragment_debtors_item, R.id.item_debtor_name , debtors);
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
                    (TextView) listItem.findViewById(R.id.item_debtor_name),
                    (TextView) listItem.findViewById(R.id.item_debtor_amount)
            );
            listItem.setTag(holder);
        }

        // fill with the data
        final Debtor debtor = this.getItem(position);
        holder.debtor_name.setText(debtor.getName());
        holder.debtor_amount.setText(Formats.frDecimalFormat.format(0) + app.getPrefs().getString("currency",""));
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
}
