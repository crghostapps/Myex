package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.Measure;
import lu.crghost.myex.models.Transaction;

import java.util.List;


public class TransactionsEditActivity extends Activity {

    private static final String TAG = "TransactionsEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Transaction transaction;
    private List<Measure> measures;

    static class ViewHolder {
        public EditText description;
        public EditText amount;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_edit);

        holder = new ViewHolder();
        holder.description = (EditText) findViewById(R.id.transactions_description);
        holder.amount = (EditText) findViewById(R.id.transactions_amount);


        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            transaction = new Transaction();
        } else {
            isupdate = true;
            transaction = app.getDataManager().getTransactionById(id);
            if (transaction==null) { // security
                isupdate = false;
            } else {
                holder.description.setText(transaction.getDescription());
                holder.amount.setText(lu.crghost.cralib.tools.Formats.formatDecimal(transaction.getAmount()));
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.transactions_title_edit));
        } else {
            setTitle(getResources().getString(R.string.transactions_title_new));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transactions_edit, menu);
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
                transaction.setDescription(holder.description.getText().toString());
                if (transaction.getDescription().length()<1) {
                    holder.description.setError(getResources().getString(R.string.transactions_description_error));
                    return false;
                }
                transaction.setAmount(lu.crghost.cralib.tools.Formats.parseDecimal(holder.amount.getText().toString()));

                if (isupdate)   app.getDataManager().updateTransaction(transaction);
                else            app.getDataManager().insertTransaction(transaction);
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.action_cancel:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_delete:
                if (isupdate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.transactions_delete_confirmation);
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            app.getDataManager().deleteTransaction(transaction);
                            Toast.makeText(TransactionsEditActivity.this, R.string.transactions_deleted, Toast.LENGTH_SHORT).show();
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
}
