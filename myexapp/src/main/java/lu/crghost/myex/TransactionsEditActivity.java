package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import lu.crghost.myex.dao.DataManager;
import lu.crghost.myex.models.*;
import lu.crghost.myex.tools.MyFormats;
import lu.crghost.myex.tools.SimpleAccountAdapter;
import lu.crghost.myex.tools.SimpleMeasureAdapter;
import net.sqlcipher.Cursor;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TransactionsEditActivity extends Activity {

    private static final String TAG = "TransEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Transaction transaction;
    private List<Measure> measureList;
    private List<Account> accountList;
    private List<Costcenter> costcenterList;

    static class ViewHolder {
        public EditText vamount;
        public EditText vamountDate;
        public ToggleButton vpm;          // on = - off = +
        public AutoCompleteTextView vdescription;
        public AutoCompleteTextView vdebtor;
        public long vdebtor_id;
        public Spinner vaccountsel;
        public long vaccountsel_id;
        public Spinner vcostcentersel;
        public long vcostcenter_id;
        public EditText vmeasure1;
        public Spinner  vmeasure1sel;
        public long vmeasure1sel_id;
        public EditText vmeasure2;
        public Spinner  vmeasure2sel;
        public long vmeasure2sel_id;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_edit);

        holder = new ViewHolder();
        holder.vamount = (EditText) findViewById(R.id.transactions_amount);
        holder.vamountDate = (EditText) findViewById(R.id.transactions_datetime);
        holder.vpm = (ToggleButton) findViewById(R.id.transactions_pm);
        holder.vdescription = (AutoCompleteTextView) findViewById(R.id.transactions_description);
        holder.vdebtor = (AutoCompleteTextView) findViewById(R.id.transactions_debtor);
        holder.vdebtor_id = 0;
        holder.vaccountsel = (Spinner) findViewById(R.id.transactions_accountsel);
        holder.vaccountsel_id = 0;
        holder.vcostcentersel = (Spinner) findViewById(R.id.transactions_costcentersel);
        holder.vcostcenter_id = 0;

        holder.vmeasure1 = (EditText) findViewById(R.id.transactions_measure1);
        holder.vmeasure1sel = (Spinner) findViewById(R.id.transactions_sel_measure1);
        holder.vmeasure1sel_id = 0;
        holder.vmeasure2 = (EditText) findViewById(R.id.transactions_measure2);
        holder.vmeasure2sel = (Spinner) findViewById(R.id.transactions_sel_measure2);
        holder.vmeasure2sel_id = 0;


        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            transaction = new Transaction();
            holder.vaccountsel_id = getIntent().getLongExtra("account_id",0);
            holder.vdebtor_id = getIntent().getLongExtra("debtor_id",0);
            holder.vcostcenter_id = getIntent().getLongExtra("costcenter_id",0);
            holder.vpm.setChecked(app.isTransactionEdit_last_sign_negatif());
            holder.vamountDate.setText(MyFormats.formatDateTime.format(new Date(System.currentTimeMillis())));
        } else {
            isupdate = true;
            transaction = app.getDataManager().getTransactionById(id);
            if (transaction==null) { // security
                isupdate = false;
            } else {
                holder.vdescription.setText(transaction.getDescription());
                if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    holder.vamount.setText(MyFormats.formatDecimal(transaction.getAmount().negate(),2));
                    holder.vpm.setChecked(true);
                } else {
                    holder.vamount.setText(MyFormats.formatDecimal(transaction.getAmount(),2));
                    holder.vpm.setChecked(false);
                }
                holder.vdebtor_id = transaction.getDebitor_id();
                holder.vcostcenter_id = transaction.getCostcenter_id();
                holder.vaccountsel_id = transaction.getAccount_id();
                holder.vmeasure1.setText(MyFormats.formatDecimal(transaction.getMeasure1(),2));
                holder.vmeasure1sel_id = transaction.getMeasure1_id();
                holder.vmeasure2.setText(MyFormats.formatDecimal(transaction.getMeasure2(),2));
                holder.vmeasure2sel_id = transaction.getMeasure1_id();
            }
        }

        // Autocomplete desciption
        Cursor descs = app.getDataManager().getDescriptionCursor(null,null);
        SimpleCursorAdapter madapter4 = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line,descs,new String[]{"description"}, new int[]{android.R.id.text1},0);
        madapter4.setCursorToStringConverter(new android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(android.database.Cursor cursor) {
                final int colindex = cursor.getColumnIndexOrThrow("description");
                return cursor.getString(colindex);
            }
        });
        madapter4.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint==null) return null;
                Cursor cursor = app.getDataManager().getDescriptionCursor ("description LIKE ?", new String[]{ "%" + constraint.toString() + "%" });
                return cursor;
            }
        });
        holder.vdescription.setAdapter(madapter4);
        holder.vdescription.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                //Log.i(TAG, "desc item selected " + id);
                // Set costcenter
                Transaction trans = app.getDataManager().getTransactionById(id);
                if (trans != null && trans.getCostcenter_id() > 0) {
                    holder.vcostcenter_id = trans.getCostcenter_id();
                }
            }
        });

        // Debtor + autocomplete
        String debtorname = null;
        if (holder.vdebtor_id > 0) {
            Debtor debtor = app.getDataManager().getDebtortById(holder.vdebtor_id);
            debtorname = debtor.getName();
        }
        holder.vdebtor.setText(debtorname);
        Cursor cdeptors = app.getDataManager().getDebtorsCursor(null,null);
        SimpleCursorAdapter madapter1 = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line,cdeptors,new String[]{"name"}, new int[]{android.R.id.text1},0);
        madapter1.setCursorToStringConverter(new android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(android.database.Cursor cursor) {
                final int colindex = cursor.getColumnIndexOrThrow("name");
                return cursor.getString(colindex);
            }
        });
        madapter1.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint==null) return null;
                Cursor cursor = app.getDataManager().getDebtorsCursor("name LIKE ?", new String[]{"%" + constraint.toString() + "%" });
                return cursor;
            }
        });
        holder.vdebtor.setAdapter(madapter1);
        holder.vdebtor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                holder.vdebtor_id = id;
            }
        });

        // Costcenter spinner
        int cctype = DataManager.COSTCENTERTYPE_INCOME;
        if (holder.vpm.isChecked()) cctype = DataManager.COSTCENTERTYPE_EXPENSE;
        costcenterList = app.getDataManager().getCostcentersForSpinner(getResources().getString(R.string.transactions_ccspinner_root),cctype);
        CostcenterAdapter costcenterAdapter = new CostcenterAdapter(this,costcenterList);
        costcenterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.vcostcentersel.setAdapter(costcenterAdapter);
        int cpos = app.getDataManager().getPositionInList( (List<BaseModel>) (List) costcenterList,holder.vcostcenter_id);
        holder.vcostcentersel.setSelection(cpos);
        holder.vcostcentersel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Costcenter cc = (Costcenter) holder.vcostcentersel.getItemAtPosition(position);
                holder.vcostcenter_id = cc.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Account spinner
        accountList = app.getDataManager().getAccounts(null,null);
        SimpleAccountAdapter accountArrayAdapter = new SimpleAccountAdapter(this,accountList);
        accountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.vaccountsel.setAdapter(accountArrayAdapter);
        int mpos = app.getDataManager().getPositionInList((List<BaseModel>) (List) accountList, holder.vaccountsel_id );
        holder.vaccountsel.setSelection(mpos);
        holder.vaccountsel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.vaccountsel_id = ((Account) holder.vaccountsel.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Measure 1 spinner
        measureList = app.getDataManager().getMeasuresForSpinner(true,"");
        SimpleMeasureAdapter measureArrayAdapter = new SimpleMeasureAdapter(this,measureList);
        measureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.vmeasure1sel.setAdapter(measureArrayAdapter);
        int mpos1 = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.vmeasure1sel_id );
        holder.vmeasure1sel.setSelection(mpos1);
        holder.vmeasure1sel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.vmeasure1sel_id = ((Measure) holder.vmeasure1sel.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Measure 2 spinner
        holder.vmeasure2sel.setAdapter(measureArrayAdapter);
        int mpos2 = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.vmeasure2sel_id );
        holder.vmeasure2sel.setSelection(mpos1);
        holder.vmeasure2sel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.vmeasure2sel_id = ((Measure) holder.vmeasure2sel.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (isupdate) {
            setTitle(getResources().getString(R.string.transactions_title_edit));
        } else {
            setTitle(getResources().getString(R.string.transactions_title_new));
        }

        holder.vamount.requestFocus();

    }

    /**
     * Toogle +Off/-On
     * @param v
     */
    public void action_toggle_pm(View v) {
        // Reload costcenter selection
        int cctype = DataManager.COSTCENTERTYPE_INCOME;
        if (holder.vpm.isChecked()) cctype = DataManager.COSTCENTERTYPE_EXPENSE;
        costcenterList = app.getDataManager().getCostcentersForSpinner(getResources().getString(R.string.transactions_ccspinner_root),cctype);
        CostcenterAdapter costcenterAdapter = new CostcenterAdapter(this,costcenterList);
        holder.vcostcentersel.setAdapter(costcenterAdapter);
    }

    /**
     * Show calendar to select date and time
     * @param v
     */
    public void action_calendar(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.prompt_datetime, null);
        final DatePicker picDate = (DatePicker) prompt.findViewById(R.id.prompt_datePicker);
        final TimePicker picTime = (TimePicker) prompt.findViewById(R.id.prompt_timePicker);


        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(prompt);
        dialog.setTitle(getResources().getString(R.string.prompt_datetime_title));
        dialog.setIcon(android.R.drawable.ic_menu_my_calendar);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(picDate.getYear(), picDate.getMonth(), picDate.getDayOfMonth());
                calendar.set(Calendar.HOUR, picTime.getCurrentHour());
                calendar.set(Calendar.MINUTE, picTime.getCurrentMinute());
                holder.vamountDate.setText(MyFormats.formatDateTime.format(calendar.getTime()));
            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Cancel pressed");
            }
        });
        dialog.show();
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
                transaction.setDescription(holder.vdescription.getText().toString());
                if (transaction.getDescription().length()<1) {
                    holder.vdescription.setError(getResources().getString(R.string.transactions_description_error));
                    return false;
                }
                boolean negatif = holder.vpm.isChecked();
                BigDecimal amount = lu.crghost.cralib.tools.Formats.parseDecimal(holder.vamount.getText().toString(),2);
                if (negatif && amount.compareTo(BigDecimal.ZERO) > 0) {
                    amount = amount.negate();
                }
                if (!negatif && amount.compareTo(BigDecimal.ZERO) < 0) {
                    amount = amount.negate();
                }
                transaction.setAmount(amount);
                Date amount_at = null;
                try {
                    amount_at = MyFormats.formatDateTime.parse(holder.vamountDate.getText().toString());
                } catch(Exception e) {
                    holder.vamountDate.setError(getResources().getString(R.string.transactions_date_error));
                    return false;
                }
                transaction.setDateAmount_at(amount_at);
                transaction.setDebitor_id(holder.vdebtor_id);
                transaction.setCostcenter_id(holder.vcostcenter_id);
                transaction.setAccount_id(holder.vaccountsel_id);
                transaction.setMeasure1_id(holder.vmeasure1sel_id);
                transaction.setMeasure1(lu.crghost.cralib.tools.Formats.parseDecimal(holder.vmeasure1.getText().toString(),2));
                transaction.setMeasure2_id(holder.vmeasure1sel_id);
                transaction.setMeasure2(lu.crghost.cralib.tools.Formats.parseDecimal(holder.vmeasure1.getText().toString(),2));

                if (isupdate)   app.getDataManager().updateTransaction(transaction);
                else            app.getDataManager().insertTransaction(transaction);

                app.setTransactionEdit_last_sign_negatif(holder.vpm.isChecked());

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
