package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Debtor;
import lu.crghost.myex.tools.MyFormats;

/**
 * CUD a debtor
 */
public class DebtorsEditActivity extends Activity implements OnMapReadyCallback {

    private static final String TAG = "DebtorsEditActivity";

    private MyExApp app;
    private boolean isupdate;
    private Debtor debtor;
    String debtor_amount;
    Marker debtor_marker;

    static class ViewHolder {
        public EditText name;
        public MapFragment map;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debtors_edit);

        holder = new ViewHolder();
        holder.name = (EditText) findViewById(R.id.debtors_name);
        holder.map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        app = (MyExApp) getApplication();

        long id = this.getIntent().getLongExtra("id",0);
        debtor_amount = "";
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
                debtor_amount = MyFormats.formatDecimal(app.getDataManager().getDebtorTotalAmount(debtor.getId()),2)+app.getCurrencySymbol();
            }
        }

        if (isupdate) {
            setTitle(getResources().getString(R.string.debtors_title_edit));
        } else {
            setTitle(getResources().getString(R.string.debtors_title_new));
        }

        holder.map.getMapAsync(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debtors_edit, menu);

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
                debtor.setName(holder.name.getText().toString());
                if (debtor.getName().length()<1) {
                    holder.name.setError(getResources().getString(R.string.debtors_name_error));
                    return false;
                }
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

        if (debtor.getLatLng()==null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(app.getLastKnownLatLng(), 15));
        } else {
            // Move the camera instantly to debtor with a zoom of 15.
            moveMarker(debtor.getLatLng());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(debtor.getLatLng(), 15));
        }

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    /**
     * Set location and move marker
     * @param latLng
     */
    private void moveMarker(LatLng latLng) {
        Log.d(TAG,"--marker moved to " + latLng);
        debtor.setLatLng(latLng);
        if (debtor_marker==null) {
            debtor_marker = holder.map.getMap().addMarker(new MarkerOptions()
                    .position(debtor.getLatLng())
                    .title(debtor.getName())
                    .snippet(debtor_amount)
            );
        } else {
            debtor_marker.setPosition(latLng);
        }
    }

}
