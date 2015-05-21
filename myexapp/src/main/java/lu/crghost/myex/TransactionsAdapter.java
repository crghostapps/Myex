package lu.crghost.myex;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import lu.crghost.cralib.tools.Formats;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.Measure;
import lu.crghost.myex.models.Transaction;
import lu.crghost.myex.tools.MyFormats;

/**
 * Dynamic adapter for transaction list
 */
public class TransactionsAdapter extends SimpleCursorAdapter {

    public static final String TAG = "TransactionsAdapter";

    private int layout;
    private Context context;
    private int orientation;

    private long last_account_id;
    private String last_symbol;
    private MyExApp app;

    public TransactionsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, MyExApp application) {
        super(context, layout, c, from, to, 0);
        this.layout = layout;
        this.context = context;
        this.orientation = context.getResources().getConfiguration().orientation;
        this.last_account_id = 0;
        this.last_symbol = "";
        this.app = application;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Cursor c = getCursor();

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, parent, false);

        fillDatainList(v, context, c);


        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

        fillDatainList(v, context, c);
    }

    /**
     * Fill data in list
     * @param v
     * @param context
     * @param c
     */
    private void fillDatainList(View v,Context context, Cursor c) {
        TextView itemdescription = null;
        TextView itemcostcenter  = null;
        TextView itemamount = null;
        ViewHolder holder = (ViewHolder) v.getTag();
        if (holder != null) {
            itemdescription = holder.itemdescription;
            itemcostcenter  = holder.itemcostcenter;
            itemamount = holder.itemamount;
        } else {
            itemdescription = (TextView) v.findViewById(R.id.item_trans_description);
            itemcostcenter  = (TextView) v.findViewById(R.id.item_trans_costcenter);
            itemamount  = (TextView) v.findViewById(R.id.item_trans_amount);
            holder = new ViewHolder(itemdescription,itemcostcenter,itemamount);
            v.setTag(holder);
        }

        if (itemdescription != null) {
            Transaction transaction = new Transaction(c);
            if (last_account_id != transaction.getAccount_id()) {
                Account account = app.getDataManager().getAccountById(transaction.getAccount_id());
                if (account!=null) {
                    last_account_id = account.getId();
                    Measure measure = app.getDataManager().getMeasureById(account.getMeasure_id());
                    if (measure != null) {
                        if (measure.isCurrency()) {
                            last_symbol = app.getCurrencySymbol();
                        } else {
                            last_symbol = measure.getNameshort();
                        }
                    }
                }
            }
            itemdescription.setText(transaction.getDescription());
            itemcostcenter.setText(transaction.getAmount_at() + "  " + app.getDataManager().getCostenterDescription(transaction.getCostcenter_id()));
            itemamount.setText(MyFormats.formatDecimal(transaction.getAmount(),2) + last_symbol);
        }

    }

    private static class ViewHolder {
        protected final TextView itemdescription;
        protected final TextView itemcostcenter;
        protected final TextView itemamount;

        public ViewHolder(TextView itemdescription, TextView itemcostcenter, TextView itemamount) {
            this.itemdescription = itemdescription;
            this.itemcostcenter = itemcostcenter;
            this.itemamount = itemamount;
        }
    }

}
