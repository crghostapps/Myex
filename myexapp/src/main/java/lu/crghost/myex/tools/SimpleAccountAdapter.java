package lu.crghost.myex.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.Measure;

import java.util.List;

/**
 * Simple Account adapter
 */
public class SimpleAccountAdapter extends ArrayAdapter<Account> {

    private static final String TAG = "SimpleAccountAdapter";
    Context context;

    public SimpleAccountAdapter(Context context, List<Account> accounts) {
        super(context, android.R.layout.simple_spinner_item, accounts);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get refs to views
        View listItem =  super.getView(position, convertView, parent);
        final TextView text1 = (TextView) listItem.findViewById(android.R.id.text1);

        // fill with the data
        final Account account = this.getItem(position);
        text1.setText(account.getAcname());

        return listItem;
    }

}
