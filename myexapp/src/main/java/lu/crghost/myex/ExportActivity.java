package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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
import lu.crghost.myex.dao.DbSqlDump;
import lu.crghost.myex.models.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


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

    public void action_export_dump(View v) {

        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.prompt_mail, null);
        final EditText txtemail = (EditText) prompt.findViewById(R.id.prompt_email);
        txtemail.setText(app.getPrefs().getString("usermail","caremilos@gmail.com"));
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(prompt);
        dialog.setTitle(getResources().getString(R.string.export_dump));
        dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                holder.progressBar.setVisibility(View.VISIBLE);
                DoDump dump = new DoDump(txtemail.getText().toString());
                dump.execute("");
            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Cancel pressed");
            }
        });
        dialog.show();

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
                Log.i(TAG, "Cancel pressed");
            }
        });
        dialog.show();

    }


    /**
     * Send DB via Sftp
     */
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


    /**
     * Mail sqldump
     */
    private class DoDump extends AsyncTask<String,Void,String> {

        String email;

        DoDump(String mailAdress) {
            this.email=mailAdress;
        }

        @Override
        protected String doInBackground(String... params) {
            setProgress(1);
            try {
                File mypath = new File(app.getFilesDir(), "exports");
                Log.i(TAG,"----------PATH="+mypath);
                if (!mypath.exists()) {
                    mypath.mkdir();
                    Log.i(TAG, "Path " + mypath + " created");
                }
                File file = new File(mypath, "exdump_"+lu.crghost.cralib.tools.Formats.fileTimeToday() +".sql");
                if (!file.exists()) {
                    file.createNewFile();
                    Log.i(TAG, "File " + file + " created");
                }
                file.setReadable(true,false);


                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write("-- MYEX Data Dump");
                writer.newLine();

                writer.write(DbSqlDump.dump(app, new Account()));
                writer.newLine();
                writer.write(DbSqlDump.dump(app, new Costcenter()));
                writer.newLine();
                writer.write(DbSqlDump.dump(app, new Debtor()));
                writer.newLine();
                writer.write(DbSqlDump.dump(app, new Geotrack()));
                writer.newLine();
                writer.write(DbSqlDump.dump(app, new Measure()));
                writer.newLine();
                writer.write(DbSqlDump.dump(app, new Transaction()));
                writer.newLine();


                /*
                writer.write("-- Accounts ");
                writer.newLine();
                writer.write(Account.TABLE_SQLCRE);
                writer.newLine();
                writer.newLine();
                List<Account> accounts = app.getDataManager().getAccounts(null,null);
                if (accounts.size()>0) {
                    for (Account a : accounts) {
                        StringBuilder s = new StringBuilder();
                        s.append("INSERT INTO " + Account.TABLE_NAME + " (");
                        for (int i=0; i < Account.FIELD_NAMES.length; i++) {
                            s.append(Account.FIELD_NAMES[i]);
                            if (i < (Account.FIELD_NAMES.length-1)) s.append(",");
                        }
                        s.append(") VALUES(");
                        s.append(a.getId() + ", ");
                        s.append(toSqlObject(a.getAcname())+" ,");
                        s.append(toSqlObject(a.getAcnumber())+" ,");
                        s.append(a.getActype()+", ");
                        s.append(toSqlObject(a.getIconpath())+" ,");
                        s.append(a.getInitbalance()+", ");
                        s.append(a.getLimitamount()+", ");
                        s.append(toSqlObject(a.getCreated_at())+", ");
                        s.append(toSqlObject(a.getUpdated_at()));
                        s.append(");");
                        writer.write(s.toString());
                        writer.newLine();
                    }
                }

                // Constcenters
                writer.write("-- Costcenters ");
                writer.newLine();
                writer.write(Costcenter.TABLE_SQLCRE);
                writer.newLine();
                writer.newLine();
                List<Costcenter> costcenters = app.getDataManager().getCostcenters(null, null);
                if (costcenters.size()>0) {
                    for (Costcenter a : costcenters) {
                        StringBuilder s = new StringBuilder();
                        s.append("INSERT INTO " + Costcenter.TABLE_NAME + " (");
                        for (int i=0; i < Costcenter.FIELD_NAMES.length; i++) {
                            s.append(Costcenter.FIELD_NAMES[i]);
                            if (i < (Costcenter.FIELD_NAMES.length-1)) s.append(",");
                        }
                        s.append(") VALUES(");
                        s.append(a.getId() + ", ");
                        s.append(toSqlObject(a.getName())+", ");
                        s.append(a.getParent_id()+",");
                        s.append(a.getClevel()+", ");
                        s.append(a.getSort()+ ", ");
                        s.append(a.hasSons()+ ", ");
                        s.append(a.getCcttype()+", ");
                        s.append(a.isDefaultCct()+", ");
                        s.append(toSqlId(a.getMeasure1_id())+", ");
                        s.append(toSqlId(a.getMeasure2_id())+", ");
                        s.append(toSqlObject(a.getCreated_at())+", ");
                        s.append(toSqlObject(a.getUpdated_at()));
                        s.append(");");
                        writer.write(s.toString());
                        writer.newLine();
                    }
                }

                // Debtors
                writer.write("-- Debtors ");
                writer.newLine();
                writer.write(Debtor.TABLE_SQLCRE);
                writer.newLine();
                writer.newLine();
                List<Debtor> debtors = app.getDataManager().getDebtors(null, null);
                if (debtors.size()>0) {
                    for (Debtor a : debtors) {
                        StringBuilder s = new StringBuilder();
                        s.append("INSERT INTO " + Debtor.TABLE_NAME + " (");
                        for (int i=0; i < Debtor.FIELD_NAMES.length; i++) {
                            s.append(Debtor.FIELD_NAMES[i]);
                            if (i < (Debtor.FIELD_NAMES.length-1)) s.append(",");
                        }
                        s.append(") VALUES(");
                        s.append(a.getId() + ", ");
                        s.append(toSqlObject(a.getName())+", ");
                        s.append(a.getLatitude().doubleValue());
                        s.append(a.getLongitude().doubleValue());
                        s.append(a.getAltitude().doubleValue());
                        s.append(toSqlObject(a.getCreated_at())+", ");
                        s.append(toSqlObject(a.getUpdated_at()));
                        s.append(");");
                        writer.write(s.toString());
                        writer.newLine();
                    }
                }*/

                writer.close();

                // Send mail
                Intent mailintent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                mailintent.setType("text/plain");
                mailintent.putExtra(Intent.EXTRA_EMAIL, new String[]{this.email});
                mailintent.putExtra(Intent.EXTRA_SUBJECT, "MyEx Data Dump");
                mailintent.putExtra(Intent.EXTRA_TEXT, "");
                Uri uri = FileProvider.getUriForFile(app, "lu.crghost.myex.fileprovider", file);
                app.grantUriPermission(MyExProperties.APP_PACKAGE_NAME, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ArrayList<Uri> uris = new ArrayList<Uri>();
                uris.add(uri);
                mailintent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
                mailintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(mailintent, "Send email..."));

            } catch(IOException e) {
                Log.e(TAG,"Error while creation file", e);
            }
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
