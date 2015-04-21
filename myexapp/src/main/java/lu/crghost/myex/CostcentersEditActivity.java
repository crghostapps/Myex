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

import java.util.List;

import static android.provider.BaseColumns._ID;

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
        public Spinner cmeasure;
        public long cmeasure_selected_id;

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
        holder.cmeasure = (Spinner) findViewById(R.id.costcenters_measure);
        holder.cmeasure_selected_id = 0;

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            costcenter = new Costcenter();
        } else {
            isupdate = true;
            costcenter = app.getDataManager().getCostcenterById(id);
            if (costcenter==null) { // security
                isupdate = false;
            } else {
                holder.cname.setText(costcenter.getName());
                holder.cparent_selected_id = costcenter.getParent_id();
                holder.ctype_selected_id = costcenter.getCcttype();
                holder.cmeasure_selected_id = costcenter.getMeasure_id();
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.costcenters_title_edit));
        } else {
            setTitle(getResources().getString(R.string.costcenters_title_new));
        }

        // fill parent spinner
        if (isupdate) {
            parentList = app.getDataManager().getCostcenters(_ID + " <> ?",new String[] {Long.toString(holder.cparent_selected_id)});
        } else {
            parentList = app.getDataManager().getCostcenters(null,null);
        }
        CostcenterAdapter costcenterAdapter = new CostcenterAdapter(this,parentList);
        costcenterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.cparent.setAdapter(costcenterAdapter);
        int cpos = app.getDataManager().getPositionInList( (List<BaseModel>) (List) parentList,holder.cparent_selected_id);
        holder.cparent.setSelection(cpos);
        holder.cparent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.cparent_selected_id = ((Costcenter)holder.cparent.getItemAtPosition(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        // fill measure spinner
        measureList = app.getDataManager().getMeasures(null,null);
        MeasureAdapter measureArrayAdapter = new MeasureAdapter(this,measureList);
        measureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.cmeasure.setAdapter(measureArrayAdapter);
        int mpos = app.getDataManager().getPositionInList((List<BaseModel>) (List) measureList, holder.cmeasure_selected_id );
        holder.cmeasure.setSelection(mpos);
        holder.cmeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.cmeasure_selected_id = ((Measure)holder.cmeasure.getItemAtPosition(position)).getId();
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
                costcenter.setMeasure_id(holder.cmeasure_selected_id);

                if (isupdate)   app.getDataManager().updateCostcenter(costcenter);
                else            app.getDataManager().insertCostcenter(costcenter);
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
