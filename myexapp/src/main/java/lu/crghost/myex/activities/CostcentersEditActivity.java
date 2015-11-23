package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.List;

/**
 * CUD a Cost center
 */
public class CostcentersEditActivity extends Activity {

    private static final String TAG = "CostcentersEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Costcenter costcenter;
    private List<Measure> measureList;
    private List<Costcenter> parentList;

    static class ViewHolder {
        public EditText cname;
        public Spinner  cparent;
        public long cparent_selected_id;
        public Spinner ctype;
        public long ctype_selected_id;
        public Spinner cmeasure1;
        public long cmeasure1_selected_id;
        public Spinner cmeasure2;
        public long cmeasure2_selected_id;

    }
    ViewHolder holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costcenters_edit);

        holder = new ViewHolder();
        holder.cname = (EditText) findViewById(R.id.costcenters_name);
        holder.cparent = (Spinner) findViewById(R.id.costcenters_parent);
        holder.cparent_selected_id = 0;
        holder.ctype = (Spinner) findViewById(R.id.costcenters_type);
        holder.ctype_selected_id = 0;
        holder.cmeasure1 = (Spinner) findViewById(R.id.costcenters_measure1);
        holder.cmeasure1_selected_id = 0;
        holder.cmeasure2 = (Spinner) findViewById(R.id.costcenters_measure2);
        holder.cmeasure2_selected_id = 0;

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            costcenter = new Costcenter();
            holder.cparent_selected_id = app.getCostcentersEdit_last_parent_id();
            costcenter.setParent_id(holder.cparent_selected_id);
        } else {
            isupdate = true;
            costcenter = app.getDataManager().getCostcenterById(id);
            if (costcenter==null) { // security
                isupdate = false;
            } else {
                holder.cname.setText(costcenter.getName());
                holder.cparent_selected_id = costcenter.getParent_id();
                holder.ctype_selected_id = costcenter.getCcttype();
                holder.cmeasure1_selected_id = costcenter.getMeasure1_id();
                holder.cmeasure2_selected_id = costcenter.getMeasure2_id();
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.costcenters_title_edit));
        } else {
            setTitle(getResources().getString(R.string.costcenters_title_new));
        }

        // fill cost center type spinner
        ArrayAdapter<CostcenterTypes.CostcenterType> costcenterTypeArrayAdapter = new ArrayAdapter<CostcenterTypes.CostcenterType>(this,android.R.layout.simple_spinner_item, CostcenterTypes.CCTYPES);
        costcenterTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.ctype.setAdapter(costcenterTypeArrayAdapter);
        holder.ctype.setSelection((int) holder.ctype_selected_id);
        holder.ctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sid = CostcenterTypes.CCTYPES.get(position).id;
                holder.ctype_selected_id = 0;
                if (sid != null) {
                    try {
                        holder.ctype_selected_id = Long.parseLong(sid);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid number " + sid);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // fill parent spinner
        parentList = app.getDataManager().getCostcentersForSpinner(getResources().getString(R.string.data_costcenter_root), DataManager.COSTCENTERTYPE_ALL);
        CostcenterAdapter costcenterAdapter = new CostcenterAdapter(this,parentList);
        costcenterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.cparent.setAdapter(costcenterAdapter);
        int cpos = app.getDataManager().getPositionInList( (List<BaseModel>) (List) parentList,holder.cparent_selected_id);
        holder.cparent.setSelection(cpos);
        holder.cparent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Costcenter parentcc = (Costcenter)holder.cparent.getItemAtPosition(position);
                holder.cparent_selected_id = parentcc.getId();
                // Rootid = 0
                if (holder.cparent_selected_id != 0) {
                    holder.ctype.setSelection((int)parentcc.getCcttype());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // fill measure spinner
        measureList = app.getDataManager().getMeasuresForSpinner(true,null);
        SimpleMeasureAdapter measureArrayAdapter = new SimpleMeasureAdapter(this,measureList);
        measureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.cmeasure1.setAdapter(measureArrayAdapter);
        int mpos1 = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.cmeasure1_selected_id );
        holder.cmeasure1.setSelection(mpos1);
        holder.cmeasure1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.cmeasure1_selected_id = ((Measure) holder.cmeasure1.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.cmeasure2.setAdapter(measureArrayAdapter);
        int mpos2 = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.cmeasure2_selected_id );
        holder.cmeasure2.setSelection(mpos2);
        holder.cmeasure2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.cmeasure2_selected_id = ((Measure)holder.cmeasure2.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_costcenters_edit, menu);
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
                costcenter.setName(holder.cname.getText().toString());
                if (costcenter.getName().length()<1) {
                    holder.cname.setError(getResources().getString(R.string.costcenters_name_error));
                    return false;
                }
                costcenter.setCcttype((int) holder.ctype_selected_id);
                costcenter.setParent_id(holder.cparent_selected_id);
                costcenter.setMeasure1_id(holder.cmeasure1_selected_id);
                costcenter.setMeasure2_id(holder.cmeasure2_selected_id);

                if (isupdate)   app.getDataManager().updateCostcenter(costcenter);
                else            costcenter.setId(app.getDataManager().insertCostcenter(costcenter));
                app.setCostcentersEdit_last_parent_id(costcenter.getParent_id());
                app.getDataManager().resortCostcenters();
                Intent rtData = new Intent();
                rtData.putExtra("costcenter_id", costcenter.getId());
                setResult(RESULT_OK, rtData);
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
                    builder.setMessage(R.string.costcenters_delete_confirmation);
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            app.getDataManager().deleteCostcenter(costcenter);
                            Toast.makeText(CostcentersEditActivity.this, R.string.costcenters_deleted, Toast.LENGTH_SHORT).show();
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
