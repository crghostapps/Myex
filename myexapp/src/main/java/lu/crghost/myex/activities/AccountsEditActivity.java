package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.dao.DataManager;
import lu.crghost.myex.models.*;
import lu.crghost.myex.tools.SimpleMeasureAdapter;

import java.util.Formatter;
import java.util.List;

/**
 * CUD Account
 * @TODO mesure_id should by 1 if account_type < 3
 */
public class AccountsEditActivity extends Activity {

    private static final String TAG = "AccountsEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Account account;
    private List<Measure> measures;
    private List<Costcenter> costcenterList;

    static class ViewHolder {
        public EditText aname;
        public EditText anumber;
        public Spinner  atype;
        public long atype_selected_id;
        public EditText ainitbalance;
        public EditText alimitamount;
        public Spinner ameasure;
        public long ameasure_selected_id;
        public Spinner acostcentersel;
        public long acostcenter_id;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_edit);
        Log.d(TAG,"- on create");

        holder = new ViewHolder();
        holder.aname = (EditText) findViewById(R.id.account_name);
        holder.anumber = (EditText) findViewById(R.id.account_number);
        holder.atype = (Spinner) findViewById(R.id.account_type);
        holder.atype_selected_id = 0;
        holder.ainitbalance = (EditText) findViewById(R.id.account_initbalance);
        holder.alimitamount = (EditText) findViewById(R.id.account_limitamount);
        holder.ameasure = (Spinner) findViewById(R.id.account_measure);
        holder.ameasure_selected_id = 1;
        holder.acostcentersel = (Spinner) findViewById(R.id.account_costcenter);
        holder.acostcenter_id = 0;

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
                holder.ainitbalance.setText(lu.crghost.cralib3.tools.Formats.formatDecimal(account.getInitbalance(),2));
                holder.alimitamount.setText(lu.crghost.cralib3.tools.Formats.formatDecimal(account.getLimitamount(),2));
                holder.ameasure_selected_id = account.getMeasure_id();
                holder.acostcenter_id = account.getCostcenter_id();
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
        List<Measure> measureList = app.getDataManager().getMeasuresForSpinner(false,null);
        SimpleMeasureAdapter measureArrayAdapter = new SimpleMeasureAdapter(this,measureList, SimpleMeasureAdapter.SHOW_NAME_LONG);
        measureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.ameasure.setAdapter(measureArrayAdapter);
        int mpos1 = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.ameasure_selected_id );
        holder.ameasure.setSelection(mpos1);
        holder.ameasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.ameasure_selected_id = ((Measure) holder.ameasure.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Costcenter spinner
        costcenterList = app.getDataManager().getCostcentersForSpinner(null,DataManager.COSTCENTERTYPE_ALL);
        CostcenterAdapter costcenterAdapter = new CostcenterAdapter(this,costcenterList);
        costcenterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.acostcentersel.setAdapter(costcenterAdapter);
        int cpos = app.getDataManager().getPositionInList( (List<BaseModel>) (List) costcenterList,holder.acostcenter_id);
        holder.acostcentersel.setSelection(cpos);
        holder.acostcentersel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Costcenter cc = (Costcenter) holder.acostcentersel.getItemAtPosition(position);
                holder.acostcenter_id = cc.getId();
                ((TextView) parent.getChildAt(0)).setText(app.getDataManager().getCostenterDescription(cc.getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"- onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_accounts_edit, menu);

        // Hide delete
        if (!isupdate) {
            MenuItem mnudel = menu.findItem(R.id.action_delete);
            mnudel.setVisible(false);
            //invalidateOptionsMenu(); STACKOVERFLOW because it calls onCreateOptionsMenu again
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
                account.setInitbalance(lu.crghost.cralib3.tools.Formats.parseDecimal(holder.ainitbalance.getText().toString(),2));
                account.setLimitamount(lu.crghost.cralib3.tools.Formats.parseDecimal(holder.alimitamount.getText().toString(),2));
                account.setMeasure_id(holder.ameasure_selected_id);
                account.setCostcenter_id(holder.acostcenter_id);

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
                    String msg = getResources().getString(R.string.account_delete_confirmation);
                    List<Transaction> transactions = app.getDataManager().getTransactions("account_id=?", new String[] {account.getIdAsString()});
                    if (transactions.size() >0 ) {
                        msg = String.format(getResources().getString(R.string.account_delete_confirmation_ri),transactions.size(), account.getAcname());
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(msg);
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            app.getDataManager().beginTransaction();
                            app.getDataManager().deleteTransactions("account_id=?", new String[] {account.getIdAsString()});
                            app.getDataManager().deleteAccount(account);
                            app.getDataManager().commit();
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
