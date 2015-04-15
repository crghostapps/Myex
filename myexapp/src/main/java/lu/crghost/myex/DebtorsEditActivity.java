package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import lu.crghost.cralib.tools.Formats;
import lu.crghost.myex.models.Debtor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;


public class DebtorsEditActivity extends Activity {

    private static final String TAG = "MyExApp";

    private MyExApp app;
    private boolean isupdate;
    private Debtor debtor;

    NumberFormat numberFormat = NumberFormat.getNumberInstance();

    static class ViewHolder {
        public EditText name;
        public EditText latitude;
        public EditText longitude;
        public ImageButton btngps;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debtors_edit);

        holder = new ViewHolder();
        holder.name = (EditText) findViewById(R.id.debtors_name);
        holder.latitude = (EditText) findViewById(R.id.debtors_latitude);
        holder.longitude = (EditText) findViewById(R.id.debtors_longitude);
        holder.btngps = (ImageButton) findViewById(R.id.debtors_btngps);

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            debtor = new Debtor();
        } else {
            isupdate = true;
            debtor = app.getDataManager().getDebtortById(id);
            holder.name.setText(debtor.getName());
            holder.latitude.setText(formatDecimal(debtor.getLatitude()));
            holder.longitude.setText(formatDecimal(debtor.getLongitude()));
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debtors_edit, menu);

        // Hide delete
        if (!isupdate) {
            MenuItem mnudel = menu.findItem(R.id.action_delete);
            mnudel.setVisible(false);
            invalidateOptionsMenu();
        }

        return true;
    }

    private BigDecimal parseDecimal(String s) {
        BigDecimal big = BigDecimal.ZERO;
        if (s!=null && s.length() > 0) {
            try {
                Number n = NumberFormat.getInstance().parse(s);
                big = new BigDecimal(n.doubleValue());
            } catch (ParseException e) {
                Log.w(TAG, "Numberformat error for " + s);
            }
        }
        return big;
    }

    private String formatDecimal(BigDecimal big) {
        String s = "";
        if (big!=null && big.compareTo(BigDecimal.ZERO) != 0 ) {
            s = NumberFormat.getInstance().format(big.doubleValue());
        }
        return s;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                debtor.setName(holder.name.getText().toString());
                if (debtor.getName().length()<1) {
                    holder.name.setError(getResources().getString(R.string.debtors_name_error));
                    return false;
                }
                debtor.setLatitude(parseDecimal(holder.latitude.getText().toString()));
                debtor.setLongitude(parseDecimal(holder.longitude.getText().toString()));
                debtor.setAltitude(new BigDecimal(0));
                if (isupdate)   app.getDataManager().updateDebtor(debtor);
                else            app.getDataManager().insertDebtor(debtor);
                finish();
                return true;
            case R.id.action_cancel:
                finish();
                return true;
            case R.id.action_delete:
                if (isupdate) {
                    // @TODO: check RI on Transactions
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.debtors_delete_confirmation);
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            app.getDataManager().deleteDebtor(debtor);
                            Toast.makeText(DebtorsEditActivity.this,R.string.debtors_deleted,Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * GPS button clicked
     * @param v
     */
    public void btnGps_click(View v) {

    }
}
