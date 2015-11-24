package lu.crghost.myex.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import lu.crghost.cralib3.tools.Formats;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.Measure;
import lu.crghost.myex.tools.MyFormats;

import java.util.List;

/**
 * ArrayAdapter for Accounts list
 */
public class AccountsAdapter extends ArrayAdapter<Account> {

    private static final String TAG = "AccountsAdapter";
    Context context;
    MyExApp app;

    public AccountsAdapter(MyExApp application,Context context, List<Account> accounts) {
        super(context, R.layout.fragment_accounts_item, R.id.item_account_name , accounts);
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
                    (TextView) listItem.findViewById(R.id.item_account_name),
                    (TextView) listItem.findViewById(R.id.item_account_amount),
                    (ProgressBar) listItem.findViewById(R.id.item_account_progressBar),
                    (LinearLayout) listItem.findViewById(R.id.layout_account_col1)
            );
            listItem.setTag(holder);
        }

        // fill with the data
        final Account account = this.getItem(position);
        holder.account_name.setText(account.getAcname());
        holder.account_name.setTextColor(Color.BLACK);

        int maxcre = (int) app.getDataManager().getAccountMaxBalance(account);
        double balance = app.getDataManager().getAccountBalance(account);

        holder.progressBar.setMax(maxcre);
        String symbol = "";
        if (account.getActype() >= Account.TYPE_COUNTER) {
            Measure measure = app.getDataManager().getMeasureById(account.getMeasure_id());
            if (measure != null) symbol = measure.getNameshort();
        } else if (account.isOtherCurrency()) {
            symbol = account.getCurrencyname();
        } else {
            symbol = app.getCurrencySymbol();
        }
        holder.account_amount.setText(MyFormats.formatDouble(balance,2) + symbol);

        boolean isred = false;
        if (balance<0) {
            holder.progressBar.setProgress(0);
            holder.account_name.setTextColor(Color.RED);
        } else {
            holder.progressBar.setProgress((int) balance);
            if (balance < account.getLimitamount().doubleValue()) {
                isred = true;
            }
        }
        if (isred) {
            holder.progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.item_progressbar_red));
        } else {
            holder.progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.item_progressbar));
        }

        return listItem;
    }

    /**
     * Views container
     */
    private static class ViewHolder {
        protected final TextView account_name;
        protected final TextView account_amount;
        protected final ProgressBar progressBar;
        protected final LinearLayout layoutcol1;
        public ViewHolder(TextView account_name,
                        TextView account_amount,
                        ProgressBar progressBar,
                        LinearLayout layoutcol1) {
            this.account_name = account_name;
            this.account_amount = account_amount;
            this.progressBar = progressBar;
            this.layoutcol1 = layoutcol1;
        }

    }


}
