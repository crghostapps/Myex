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
import lu.crghost.myex.models.Transaction;

/**
 * Dynamic adapter for transaction list
 */
public class TransactionsAdapter extends SimpleCursorAdapter {

    public static final String TAG = "TransactionsAdapter";

    private int layout;
    private Context context;
    private int orientation;
    private String currencySymbol;

    public TransactionsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, String currencySymbol) {
        super(context, layout, c, from, to, 0);
        this.layout = layout;
        this.context = context;
        this.currencySymbol = currencySymbol;
        if (this.currencySymbol==null) this.currencySymbol="";
        this.orientation = context.getResources().getConfiguration().orientation;
        Log.d(TAG,"--------------adapter created------------------");
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
        Log.d(TAG,"---------------------------fill data-------------------------" );
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
            Log.d(TAG, "--"+transaction);
            itemdescription.setText(transaction.getDescription());
            itemcostcenter.setText("ccid="+transaction.getCostcenter_id());
            itemamount.setText(Formats.frDecimalFormat.format(0)+ currencySymbol);
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
