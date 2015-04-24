package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import lu.crghost.cralib.net.SshClient;
import lu.crghost.myex.conf.MyExProperties;

import java.net.URI;
import java.net.URISyntaxException;


public class ExportActivity extends Activity {

    private static final String TAG = "ExportActivity";

    private MyExApp app;

    static class ViewHolder {
        public ProgressBar progressBar;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        app = (MyExApp) getApplication();
        holder = new ViewHolder();
        holder.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        holder.progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void action_export_transactions(View v) {

    }

    public void action_export_backup(View v) {

        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.prompt_backup, null);
        final EditText txtbkserver = (EditText) prompt.findViewById(R.id.prompt_bkserver);
        final EditText txtbkpass   = (EditText) prompt.findViewById(R.id.prompt_bkserverpass);
        txtbkserver.setText(app.getPrefs().getString("pref_bkserver",""));
        txtbkpass.setText(app.getPrefs().getString("pref_bkserverpass",""));
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(prompt);
        dialog.setTitle(getResources().getString(R.string.export_backup));
        dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                holder.progressBar.setVisibility(View.VISIBLE);
                URI uri = null;
                try {
                    uri = new URI(txtbkserver.getText().toString());
                    DoBackup doBackup = new DoBackup(uri, txtbkpass.getText().toString());
                    doBackup.execute("");
                } catch (URISyntaxException e) {
                    Toast.makeText(ExportActivity.this, getResources().getText(R.string.pref_bkserver_error), Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"Cancel pressed");
            }
        });
        dialog.show();

    }


    private class DoBackup extends AsyncTask<String,Void,String> {

        URI bkserveruri;
        String bkpass;

        DoBackup(URI serveruri, String pass) {
            bkserveruri = serveruri;
            bkpass = pass;
        }

        @Override
        protected String doInBackground(String... params) {
            setProgress(1);
            SshClient.putFiles(bkserveruri,bkpass,new String[] {MyExProperties.DATABASE_PATH});
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            holder.progressBar.setVisibility(View.GONE);
            Toast.makeText(ExportActivity.this, getResources().getText(R.string.export_backup_end), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
