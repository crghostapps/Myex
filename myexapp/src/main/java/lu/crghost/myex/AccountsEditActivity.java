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
import lu.crghost.myex.models.*;
import lu.crghost.myex.tools.SimpleMeasureAdapter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;


public class AccountsEditActivity extends Activity {

    private static final String TAG = "AccountsEditActivity";

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
        public Spinner acurrency;
        public long acurrency_selected_id;
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
        holder.acurrency = (Spinner) findViewById(R.id.account_currency);
        holder.acurrency_selected_id = 1;

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
                holder.ainitbalance.setText(lu.crghost.cralib.tools.Formats.formatDecimal(account.getInitbalance(),2));
                holder.alimitamount.setText(lu.crghost.cralib.tools.Formats.formatDecimal(account.getLimitamount(),2));
                holder.acurrency_selected_id = account.getCurrency_id();
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

        // fill currency spinner
        List<Measure> measureList = app.getDataManager().getMeasuresForSpinner(false,true);
        SimpleMeasureAdapter measureArrayAdapter = new SimpleMeasureAdapter(this,measureList);
        measureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.acurrency.setAdapter(measureArrayAdapter);
        int mpos1 = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.acurrency_selected_id );
        holder.acurrency.setSelection(mpos1);
        holder.acurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.acurrency_selected_id = ((Measure) holder.acurrency.getItemAtPosition(position)).getId();
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
                account.setInitbalance(lu.crghost.cralib.tools.Formats.parseDecimal(holder.ainitbalance.getText().toString(),2));
                account.setLimitamount(lu.crghost.cralib.tools.Formats.parseDecimal(holder.alimitamount.getText().toString(),2));
                account.setCurrency_id(holder.acurrency_selected_id);

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
