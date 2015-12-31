package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.support.v4.widget.SimpleCursorAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import lu.crghost.cralib3.tools.Formats;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.dao.DataManager;
import lu.crghost.myex.models.*;
import lu.crghost.myex.tools.MyFormats;
import lu.crghost.myex.tools.SimpleAccountAdapter;
import lu.crghost.myex.tools.SimpleMeasureAdapter;
import net.sqlcipher.Cursor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TransactionsEditActivity extends Activity implements OnMapReadyCallback {

    private static final String TAG = "TransEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Transaction transaction;
    private Marker trans_marker;
    private List<Measure> measureList;
    private List<Account> accountList;
    private List<Costcenter> costcenterList;

    private boolean calccurrency;
    private BigDecimal calccurrencyrate;

    long nearby_debtorid;
    long nearby_costcenterid;
    String nearby_description;

    static class ViewHolder {
        public EditText vamount;
        public TextView vcurrency;
        public TextView vcurrency_symbol;
        public EditText vamountDate;
        public ToggleButton vpm;          // on = - off = +
        public AutoCompleteTextView vdescription;
        public AutoCompleteTextView vdebtor;
        public long vdebtor_id;
        public Spinner vaccountsel;
        public long vaccountsel_id;
        public Spinner vcostcentersel;
        public long vcostcenter_id;
        public TextView vmeasureTitle;
        public EditText vmeasure1;
        public Spinner  vmeasure1sel;
        public long vmeasure1sel_id;
        public EditText vmeasure2;
        public Spinner  vmeasure2sel;
        public long vmeasure2sel_id;
        public ImageButton vbtnShowMap;
        public MapFragment map;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_edit);

        holder = new ViewHolder();
        holder.vamount = (EditText) findViewById(R.id.transactions_amount);
        holder.vcurrency = (TextView) findViewById(R.id.transactions_currency);
        holder.vcurrency_symbol = (TextView) findViewById(R.id.transactions_currencysymbol);
        holder.vamountDate = (EditText) findViewById(R.id.transactions_datetime);
        holder.vpm = (ToggleButton) findViewById(R.id.transactions_pm);
        holder.vdescription = (AutoCompleteTextView) findViewById(R.id.transactions_description);
        holder.vdebtor = (AutoCompleteTextView) findViewById(R.id.transactions_debtor);
        holder.vdebtor_id = 0;
        holder.vaccountsel = (Spinner) findViewById(R.id.transactions_accountsel);
        holder.vaccountsel_id = 0;
        holder.vcostcentersel = (Spinner) findViewById(R.id.transactions_costcentersel);
        holder.vcostcenter_id = 0;

        holder.vmeasureTitle = (TextView) findViewById(R.id.transactions_measure_title);
        holder.vmeasure1 = (EditText) findViewById(R.id.transactions_measure1);
        holder.vmeasure1sel = (Spinner) findViewById(R.id.transactions_sel_measure1);
        holder.vmeasure1sel_id = 0;
        holder.vmeasure2 = (EditText) findViewById(R.id.transactions_measure2);
        holder.vmeasure2sel = (Spinner) findViewById(R.id.transactions_sel_measure2);
        holder.vmeasure2sel_id = 0;
        holder.vbtnShowMap = (ImageButton) findViewById(R.id.btnShowMap);

        // Prepare map
        holder.map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        ViewGroup.LayoutParams maplp = holder.map.getView().getLayoutParams();
        maplp.height = getScreenWidth();
        holder.map.getView().setLayoutParams(maplp);
        holder.map.getView().setVisibility(View.GONE);

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            transaction = new Transaction();
            holder.vaccountsel_id = getIntent().getLongExtra("account_id",0);
            holder.vdebtor_id = getIntent().getLongExtra("debtor_id",0);
            holder.vcostcenter_id = getIntent().getLongExtra("costcenter_id",0);
            holder.vdescription.setText(getIntent().getStringExtra("description"));
            holder.vpm.setChecked(app.isTransactionEdit_last_sign_negatif());
            holder.vamountDate.setText(MyFormats.formatDateTime.format(new Date(System.currentTimeMillis())));
            Log.d(TAG,"new transaction: account="+ holder.vaccountsel_id + " cctr=" + holder.vcostcenter_id);
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
                holder.vdebtor_id = transaction.getDebtor_id();
                holder.vcostcenter_id = transaction.getCostcenter_id();
                holder.vaccountsel_id = transaction.getAccount_id();
                holder.vmeasure1.setText(MyFormats.formatDecimal(transaction.getMeasure1(),2));
                holder.vmeasure1sel_id = transaction.getMeasure1_id();
                holder.vmeasure2.setText(MyFormats.formatDecimal(transaction.getMeasure2(),2));
                holder.vmeasure2sel_id = transaction.getMeasure2_id();
                if (transaction.getDateAmount_at()!=null)
                    holder.vamountDate.setText(MyFormats.formatDateTime.format(transaction.getDateAmount_at()));
                if (transaction.getLatLng() != null) {
                    holder.map.getView().setVisibility(View.VISIBLE);
                    holder.map.getMapAsync(this);
                    holder.vbtnShowMap.setEnabled(false);
                }

            }
        }

        // TextWatcher on amount to calculate currencies if needed
        holder.vamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calcCurrencyConversion();
            }
        });


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
                // Fill data from selected description
                Transaction trans = app.getDataManager().getTransactionById(id);
                if (trans != null && trans.getCostcenter_id() > 0) {
                    holder.vcostcenter_id = trans.getCostcenter_id();
                    holder.vcostcentersel.setSelection(app.getDataManager().getPositionInList((List<BaseModel>) (List) costcenterList,holder.vcostcenter_id));
                    if (trans.getDebtor_id() > 0) {
                        Debtor debtor = app.getDataManager().getDebtortById(trans.getDebtor_id());
                        holder.vdebtor_id = debtor.getId();
                        holder.vdebtor.setText(debtor.getName());
                    }
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
        costcenterList = app.getDataManager().getCostcentersForSpinner(null,cctype);
        CostcenterAdapter costcenterAdapter = new CostcenterAdapter(this,costcenterList);
        costcenterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.vcostcentersel.setAdapter(costcenterAdapter);
        int cpos = app.getDataManager().getPositionInList( (List<BaseModel>) (List) costcenterList,holder.vcostcenter_id);
        holder.vcostcentersel.setSelection(cpos);
        holder.vcostcentersel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Costcenter cc = (Costcenter) holder.vcostcentersel.getItemAtPosition(position);
                Log.d(TAG,"--- costcenter changed --- m1=" + cc.getMeasure1_id());
                holder.vcostcenter_id = cc.getId();
                holder.vmeasureTitle.setVisibility(View.GONE);
                holder.vmeasure1.setVisibility(View.GONE);
                holder.vmeasure1sel.setVisibility(View.GONE);
                holder.vmeasure2.setVisibility(View.GONE);
                holder.vmeasure2sel.setVisibility(View.GONE);
                if (cc.getMeasure1_id() > 0) {
                    holder.vmeasure1sel_id = cc.getMeasure1_id();
                    holder.vmeasure1sel.setSelection(app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList,holder.vmeasure1sel_id ));
                    holder.vmeasureTitle.setVisibility(View.VISIBLE);
                    holder.vmeasure1.setVisibility(View.VISIBLE);
                    holder.vmeasure1sel.setVisibility(View.VISIBLE);
                }
                if (cc.getMeasure2_id() > 0) {
                    holder.vmeasure2sel_id = cc.getMeasure2_id();
                    holder.vmeasure2sel.setSelection(app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList,holder.vmeasure2sel_id ));
                    holder.vmeasureTitle.setVisibility(View.VISIBLE);
                    holder.vmeasure2.setVisibility(View.VISIBLE);
                    holder.vmeasure2sel.setVisibility(View.VISIBLE);
                }
                ((TextView) parent.getChildAt(0)).setText(app.getDataManager().getCostenterDescription(cc.getId()));
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
                Log.d(TAG,"--- account changed ---");
                Account account = (Account) holder.vaccountsel.getItemAtPosition(position);
                holder.vaccountsel_id = account.getId();
                toggle_account(account);
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
     * Reset when account has changed
     * @param account
     */
    private void toggle_account(Account account) {
        calccurrency = false;
        calccurrencyrate = BigDecimal.ONE;
        Log.d(TAG,"->" + account);
        if (account==null) {
            holder.vcurrency.setVisibility(View.GONE);
            holder.vcurrency_symbol.setText(app.getCurrencySymbol());
        } else {
            Measure measure = app.getDataManager().getMeasureById(account.getMeasure_id());
            holder.vcurrency_symbol.setText(measure.getNameshort());
            if (measure.getMctype()==Measure.TYPE_FOREIGN_CURRENCY ) {
                calccurrency = true;
                calccurrencyrate = measure.getCost_per_measure();
                if (calccurrencyrate.doubleValue()==0) calccurrencyrate = BigDecimal.ONE;
            }
            if (calccurrency) {
                holder.vcurrency.setVisibility(View.VISIBLE);
            } else {
                holder.vcurrency.setVisibility(View.GONE);
            }

            // Default costcenter on account
            Log.d(TAG,"holder=" + holder.vcostcenter_id + " account=" + account.getCostcenter_id());
            if (holder.vcostcenter_id==0 && account.getCostcenter_id() > 0) {
                holder.vcostcenter_id = account.getCostcenter_id();
                holder.vcostcentersel.setSelection(app.getDataManager().getPositionInList((List<BaseModel>) (List) costcenterList, holder.vcostcenter_id));
            }

        }
        calcCurrencyConversion();
    }

    /**
     * Reset when costcenter changed
     * @param costcenter
     */
    private void toggle_costcenter(Costcenter costcenter) {

    }

    /**
     * Convert amount to local currency
     */
    private void calcCurrencyConversion() {

        BigDecimal amount = BigDecimal.ZERO;
        if (holder.vamount.getText().length()>0) {
            boolean negatif = holder.vpm.isChecked();
            amount = lu.crghost.cralib3.tools.Formats.parseDecimal(holder.vamount.getText().toString(),2);
            if (negatif && amount.compareTo(BigDecimal.ZERO) > 0) {
                amount = amount.negate();
            }
            if (!negatif && amount.compareTo(BigDecimal.ZERO) < 0) {
                amount = amount.negate();
            }
        }

        if (!calccurrency) {
            holder.vcurrency.setText("");
        } else {
            amount = amount.divide(calccurrencyrate, 2, BigDecimal.ROUND_HALF_UP);
            holder.vcurrency.setText("= " + MyFormats.formatDecimal(amount,2) + app.getCurrencySymbol());
        }
    }

    /**
     * Toogle +Off/-On
     * @param v
     */
    public void action_toggle_pm(View v) {
        // Reload costcenter selection
        int cctype = DataManager.COSTCENTERTYPE_INCOME;
        if (holder.vpm.isChecked()) cctype = DataManager.COSTCENTERTYPE_EXPENSE;
        costcenterList = app.getDataManager().getCostcentersForSpinner(null,cctype);
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

    /**
     * Add a costcenter
     * @param v
     */
    public void action_addcostcenter(View v) {
        Intent costcenteredit = new Intent(this,CostcentersEditActivity.class);
        costcenteredit.putExtra("id", 0L);
        startActivityForResult(costcenteredit, MainFragment.TABITEM_COSTCENTERS);
    }

    /**
     * Show map for localisation
     * @param v
     */
    public void action_showmap(View v) {

        boolean usegrps = app.getPrefs().getBoolean("localisation", true);
        if (!isupdate && usegrps) {
            findNearBy();
        }
        holder.map.getView().setVisibility(View.VISIBLE);
        holder.map.getMapAsync(this);
        holder.vbtnShowMap.setEnabled(false);
    }

    /**
     * Find near by locations
     */
    private void findNearBy() {
        String dlgmessage = null;
        nearby_debtorid     = 0;
        nearby_costcenterid = 0;
        nearby_description  = null;
        Location lastlocation = app.getLastKnownLocation();
        Transaction near_transaction = null;
        float distance_transaction = Float.MAX_VALUE;
        List<Transaction> transactions = app.getDataManager().getTransactions("latitude <> 0 and longitude <> 0 and account_id=?",new String[] {String.valueOf(holder.vaccountsel_id)});
        for (Transaction t : transactions) {
            float distance = lastlocation.distanceTo(t.getLocation());
            if (distance < distance_transaction) {
                distance_transaction = distance;
                near_transaction = t;
            }
        }

        Debtor near_debtor = null;
        float distance_debtor = Float.MAX_VALUE;
        List<Debtor> debtors = app.getDataManager().getDebtors("latitude <> 0 and longitude <> 0",null);
        for (Debtor d : debtors) {
            float distance = lastlocation.distanceTo(d.getLocation());
            if (distance < distance_debtor) {
                distance_debtor = distance;
                near_debtor = d;
            }
        }

        float maxdistence = 100;
        if (distance_transaction < maxdistence && distance_transaction < distance_debtor) {
            dlgmessage = getResources().getString(R.string.transactions_nearby_msg_t) + "\n"
                    + Formats.frDateFormat.format(near_transaction.getDateAmount_at()) + " "
                    + near_transaction.getDescription();
            nearby_debtorid     = near_transaction.getDebtor_id();
            nearby_costcenterid = near_transaction.getCostcenter_id();
            nearby_description  = near_transaction.getDescription();
        } else if (distance_debtor < maxdistence) {
            dlgmessage = getResources().getString(R.string.transactions_nearby_msg_d) + "\n"
                    + near_debtor.getName();
            nearby_debtorid = near_debtor.getId();
        }

        if (dlgmessage != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.transactions_nearby_title);
            builder.setMessage(dlgmessage);
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (nearby_debtorid > 0) holder.vdebtor_id = nearby_debtorid;
                    if (nearby_costcenterid > 0) holder.vcostcenter_id = nearby_costcenterid;
                    if (nearby_description != null) holder.vdescription.setText(nearby_description);
                }
            });
            builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * Attach a new picture
     * @param v
     */
    public void action_attachpicture(View v) {}

    /**
     * Attach a existing file
     * @param v
     */
    public void action_attachfile(View v) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK) {
            switch (requestCode) {
                case MainFragment.TABITEM_COSTCENTERS:
                    long costcenter_id = data.getLongExtra("costcenter_id",0);
                    if (costcenter_id > 0) {
                        holder.vcostcenter_id = costcenter_id;
                        holder.vcostcentersel.setSelection(app.getDataManager().getPositionInList((List<BaseModel>) (List) costcenterList, holder.vcostcenter_id));
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transactions_edit, menu);
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
                transaction.setDescription(holder.vdescription.getText().toString());
                if (transaction.getDescription().length()<1) {
                    holder.vdescription.setError(getResources().getString(R.string.transactions_description_error));
                    return false;
                }
                boolean negatif = holder.vpm.isChecked();
                BigDecimal amount = lu.crghost.cralib3.tools.Formats.parseDecimal(holder.vamount.getText().toString(),2);
                if (negatif && amount.compareTo(BigDecimal.ZERO) > 0) {
                    amount = amount.negate();
                }
                if (!negatif && amount.compareTo(BigDecimal.ZERO) < 0) {
                    amount = amount.negate();
                }
                transaction.setAmount(amount);
                transaction.setAmountbase(amount);
                if (calccurrency) {
                    transaction.setAmountbase(amount.divide(calccurrencyrate, 2, BigDecimal.ROUND_HALF_UP));
                }

                Date amount_at = null;
                try {
                    amount_at = MyFormats.formatDateTime.parse(holder.vamountDate.getText().toString());
                } catch(Exception e) {
                    holder.vamountDate.setError(getResources().getString(R.string.transactions_date_error));
                    return false;
                }
                transaction.setDateAmount_at(amount_at);

                // Localisation is set in moveMarker()

                // create or update debtor ?
                if (holder.vdebtor_id > 0) {
                    if (holder.vdebtor.getText().toString().length() > 0) {
                        Debtor debtor = app.getDataManager().getDebtortById(holder.vdebtor_id);
                        if (debtor!=null && !debtor.getName().equals(holder.vdebtor.getText().toString())) {
                            holder.vdebtor_id = createDebtor(holder.vdebtor.getText().toString(), transaction.getLongitude(), transaction.getLatitude(), transaction.getAltitude());
                        }
                    }
                } else if (holder.vdebtor.getText().toString().length() > 0) {
                    holder.vdebtor_id = createDebtor(holder.vdebtor.getText().toString(), transaction.getLongitude(), transaction.getLatitude(), transaction.getAltitude());
                }
                transaction.setDebtor_id(holder.vdebtor_id);
                transaction.setCostcenter_id(holder.vcostcenter_id);
                transaction.setAccount_id(holder.vaccountsel_id);
                transaction.setMeasure1_id(holder.vmeasure1sel_id);
                transaction.setMeasure1(lu.crghost.cralib3.tools.Formats.parseDecimal(holder.vmeasure1.getText().toString(),2));
                transaction.setMeasure2_id(holder.vmeasure2sel_id);
                transaction.setMeasure2(lu.crghost.cralib3.tools.Formats.parseDecimal(holder.vmeasure2.getText().toString(),2));

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

    /**
     * Create a debtor
     * @param name
     * @param longitude
     * @param latitude
     * @param altitude
     * @return
     */
    private long createDebtor(String name, BigDecimal longitude, BigDecimal latitude, BigDecimal altitude) {
        Debtor debtor = new Debtor();
        debtor.setName(name);
        debtor.setLongitude(longitude);
        debtor.setLatitude(latitude);
        debtor.setAltitude(altitude);
        return app.getDataManager().insertDebtor(debtor);
    }

    /**
     * Google map is loaded
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // configuration of map
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                moveMarker(latLng);
            }
        });

        if (transaction.getLatLng()==null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(app.getLastKnownLatLng(), 15));
        } else {
            // Move the camera instantly to debtor with a zoom of 15.
            moveMarker(transaction.getLatLng());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(transaction.getLatLng(), 15));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }


    }

    /**
     * Set location and move marker
     * @param latLng
     */
    private void moveMarker(LatLng latLng) {
        Log.d(TAG,"--marker moved to " + latLng);
        transaction.setLatLng(latLng);
        if (trans_marker==null) {
            trans_marker = holder.map.getMap().addMarker(new MarkerOptions()
                    .position(transaction.getLatLng())
                    .title(transaction.getDescription())
                    .snippet(holder.vamount.getText() + holder.vcurrency_symbol.getText().toString())
            );
        } else {
            trans_marker.setPosition(latLng);
        }
    }


    /**
     * @return screen size int[width, height]
     *
     * */
    public int getScreenWidth(){
        Point size = new Point();
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            w.getDefaultDisplay().getSize(size);
            return size.x;
        }else{
            Display d = w.getDefaultDisplay();
            //noinspection deprecation
            return d.getWidth();
        }
    }

}
