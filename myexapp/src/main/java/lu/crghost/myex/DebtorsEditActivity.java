package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import lu.crghost.cralib.tools.Formats;
import lu.crghost.myex.models.Debtor;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * CUD a debtor
 */
public class DebtorsEditActivity extends Activity {

    private static final String TAG = "DebtorsEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Debtor debtor;

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
            if (debtor==null) { // security
                isupdate = false;
            } else {
                holder.name.setText(debtor.getName());
                holder.latitude.setText(lu.crghost.cralib.tools.Formats.formatDecimal(debtor.getLatitude()));
                holder.longitude.setText(lu.crghost.cralib.tools.Formats.formatDecimal(debtor.getLongitude()));
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.debtors_title_edit));
        } else {
            setTitle(getResources().getString(R.string.debtors_title_new));
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                debtor.setName(holder.name.getText().toString());
                if (debtor.getName().length()<1) {
                    holder.name.setError(getResources().getString(R.string.debtors_name_error));
                    return false;
                }
                debtor.setLatitude(lu.crghost.cralib.tools.Formats.parseDecimal(holder.latitude.getText().toString()));
                debtor.setLongitude(lu.crghost.cralib.tools.Formats.parseDecimal(holder.longitude.getText().toString()));
                debtor.setAltitude(new BigDecimal(0));
                if (isupdate)   app.getDataManager().updateDebtor(debtor);
                else            app.getDataManager().insertDebtor(debtor);
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.action_cancel:
                setResult(RESULT_CANCELED);
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
                            Toast.makeText(DebtorsEditActivity.this, R.string.debtors_deleted, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
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
