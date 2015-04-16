package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.AccountTypes;
import lu.crghost.myex.models.Debtor;
import lu.crghost.myex.models.Measure;

import java.math.BigDecimal;
import java.util.List;


public class AccountsEditActivity extends Activity {

    private static final String TAG = "DebtorsEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Account account;
    private List<Measure> measures;

    static class ViewHolder {
        public EditText aname;
        public EditText anumber;
        public Spinner  atype;
        public long atype_selected_id;
        public EditText ainitbalance;
        public EditText alimitamount;
        public Spinner  ameasure;
        public long ameasure_selected_id;
        public EditText ameasurecost;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_edit);

        holder = new ViewHolder();
        holder.aname = (EditText) findViewById(R.id.account_name);
        holder.anumber = (EditText) findViewById(R.id.account_number);
        holder.atype = (Spinner) findViewById(R.id.account_type);
        holder.atype_selected_id = 0;
        holder.ainitbalance = (EditText) findViewById(R.id.account_initbalance);
        holder.alimitamount = (EditText) findViewById(R.id.account_limitamount);
        holder.ameasure = (Spinner) findViewById(R.id.account_measure);
        holder.ameasure_selected_id = 0;
        holder.ameasurecost = (EditText) findViewById(R.id.account_measure_cost);

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            account = new Account();
        } else {
            isupdate = true;
            account = app.getDataManager().getAccountById(id);
            if (account==null) { // security
                isupdate = false;
            } else {
                holder.aname.setText(account.getAcname());
                holder.anumber.setText(account.getAcnumber());
                holder.atype_selected_id = account.getActype();
                holder.ainitbalance.setText(lu.crghost.cralib.tools.Formats.formatDecimal(account.getInitbalance()));
                holder.alimitamount.setText(lu.crghost.cralib.tools.Formats.formatDecimal(account.getLimitamount()));
                holder.ameasure_selected_id = account.getMeasure_id();
                holder.ameasurecost.setText(lu.crghost.cralib.tools.Formats.formatDecimal(account.getCost_per_measure()));
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.accounts_title_edit));
        } else {
            setTitle(getResources().getString(R.string.accounts_title_new));
        }

        // fill accounttype spinner
        ArrayAdapter<AccountTypes.AccountType> accountTypeArrayAdapter = new ArrayAdapter<AccountTypes.AccountType>(this,android.R.layout.simple_spinner_item, AccountTypes.ACTYPES);
        accountTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.atype.setAdapter(accountTypeArrayAdapter);
        holder.atype.setSelection((int) holder.atype_selected_id);
        holder.atype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "------------AccountTypes selected:" + AccountTypes.ACTYPES.get(position));
                String sid = AccountTypes.ACTYPES.get(position).id;
                holder.atype_selected_id = 0;
                if (sid != null) {
                    try {
                        holder.atype_selected_id = Long.parseLong(sid);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid number " + sid);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // fill measure spinner
        measures = app.getDataManager().getMeasures(null,null);
        MeasureAdapter measureArrayAdapter = new MeasureAdapter(this,measures);
        measureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.ameasure.setAdapter(measureArrayAdapter);
        int mpos = app.getDataManager().getMeasurePosition(measures,holder.ameasure_selected_id);
        Log.d(TAG,"------------------Init measure position="+mpos+" id="+holder.ameasure_selected_id);
        holder.ameasure.setSelection(mpos);
        holder.ameasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.ameasure_selected_id = holder.ameasure.getAdapter().getItemId(position);
                Log.d(TAG,"------------Measure selected position="+ position + " id="+holder.ameasure_selected_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accounts_edit, menu);

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
                account.setAcname(holder.aname.getText().toString());
                if (account.getAcname().length()<1) {
                    holder.aname.setError(getResources().getString(R.string.account_name_error));
                    return false;
                }
                account.setAcnumber(holder.anumber.getText().toString());
                account.setActype((int) holder.atype_selected_id);
                account.setInitbalance(lu.crghost.cralib.tools.Formats.parseDecimal(holder.ainitbalance.getText().toString()));
                account.setLimitamount(lu.crghost.cralib.tools.Formats.parseDecimal(holder.alimitamount.getText().toString()));
                account.setMeasure_id(holder.ameasure_selected_id);
                account.setCost_per_measure(lu.crghost.cralib.tools.Formats.parseDecimal(holder.ameasurecost.getText().toString()));

                if (isupdate)   app.getDataManager().updateAccount(account);
                else            app.getDataManager().insertAccount(account);
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
                    builder.setMessage(R.string.account_delete_confirmation);
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            app.getDataManager().deleteAccount(account);
                            Toast.makeText(AccountsEditActivity.this, R.string.account_deleted, Toast.LENGTH_SHORT).show();
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
