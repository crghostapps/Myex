package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import lu.crghost.cralib3.security.StringEncoder;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Measure;
import lu.crghost.myex.models.MeasureTypes;
import lu.crghost.myex.tools.MyFormats;

/**
 * CUD a measure
 */
public class MeasuresEditActivity extends Activity {

    private static final String TAG = "MeasuresEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Measure measure;

    static class ViewHolder {
        public EditText name;
        public EditText nameShort;
        public Spinner mtype;
        public long mtype_selected_id;
        public EditText cost;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measures_edit);

        holder = new ViewHolder();
        holder.name = (EditText) findViewById(R.id.measures_name);
        holder.nameShort = (EditText) findViewById(R.id.measures_nameshort);
        holder.mtype = (Spinner) findViewById(R.id.measures_type);
        holder.mtype_selected_id = 0;
        holder.cost = (EditText) findViewById(R.id.measures_cost);

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        if (id==0) {
            isupdate = false;
            measure = new Measure();
        } else {
            isupdate = true;
            measure = app.getDataManager().getMeasureById(id);
            if (measure==null) { // security
                isupdate = false;
            } else {
                holder.name.setText(measure.getName());
                holder.nameShort.setText(measure.getNameshort());
                holder.mtype_selected_id = measure.getMctype();
                holder.cost.setText(MyFormats.formatDecimal(measure.getCost_per_measure(), Measure.DECIMALS));
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.measures_edit));
        } else {
            setTitle(getResources().getString(R.string.measures_new));
        }

        //Log.d(TAG,"---->" + measure.getCost_per_measure());

        // fill mtype spinner
        ArrayAdapter<MeasureTypes.MeasureType> measureTypeArrayAdapter = new ArrayAdapter<MeasureTypes.MeasureType>(this,android.R.layout.simple_spinner_item, MeasureTypes.MTYPES);
        measureTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.mtype.setAdapter(measureTypeArrayAdapter);
        holder.mtype.setSelection((int) holder.mtype_selected_id);
        holder.mtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sid = MeasureTypes.MTYPES.get(position).id;
                holder.mtype_selected_id = 0;
                if (sid != null) {
                    try {
                        holder.mtype_selected_id = Long.parseLong(sid);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid number " + sid);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_measures_edit, menu);
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
                measure.setName(holder.name.getText().toString());
                if (measure.getName().length() < 1) {
                    holder.name.setError(getResources().getString(R.string.measures_name_error));
                    return false;
                }
                measure.setNameshort(holder.nameShort.getText().toString());
                if (measure.getNameshort().length() < 1) {
                    holder.nameShort.setError(getResources().getString(R.string.measures_name_error));
                    return false;
                }

                // @TODO verivy unicity of default currency

                measure.setMctype((int) holder.mtype_selected_id);
                measure.setCost_per_measure(lu.crghost.cralib3.tools.Formats.parseDecimal(holder.cost.getText().toString(),Measure.DECIMALS));
                //Log.d(TAG,"update---->" + measure.getCost_per_measure() + "double=" + measure.getCost_per_measure().doubleValue());
                if (isupdate) app.getDataManager().updateMeasure(measure);
                else app.getDataManager().insertMeasure(measure);

                // Update preferences for default currency symbol
                if (measure.getMctype()==Measure.TYPE_DEFAULT_CURRENCY) {
                    SharedPreferences.Editor editor = app.getPrefs().edit();
                    editor.putString("currency", measure.getNameshort());
                    editor.putLong("currency_id", measure.getId());
                    editor.commit();
                }

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
                    builder.setMessage(R.string.measures_delete_confirmation);
                    builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            app.getDataManager().deleteMeasure(measure);
                            Toast.makeText(MeasuresEditActivity.this, R.string.measures_deleted, Toast.LENGTH_SHORT).show();
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
