package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import lu.crghost.cralib3.net.SshClient;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.conf.MyExProperties;
import lu.crghost.myex.dao.DbSqlDump;
import lu.crghost.myex.models.*;
import lu.crghost.myex.tools.MyFormats;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;


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

    public void action_export_transactions(View v) {

    }


    public void action_export_backup(View v) {

        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.prompt_backup, null);
        final CheckBox bkAccounts = (CheckBox) prompt.findViewById(R.id.bk_accounts);
        bkAccounts.setChecked(true);
        final CheckBox bkCostcenters = (CheckBox) prompt.findViewById(R.id.bk_costcenters);
        bkCostcenters.setChecked(true);
        final CheckBox bkDebtors = (CheckBox) prompt.findViewById(R.id.bk_debtors);
        bkDebtors.setChecked(true);
        final CheckBox bkMeasures = (CheckBox) prompt.findViewById(R.id.bk_measures);
        bkMeasures.setChecked(true);
        final CheckBox bkTransactions = (CheckBox) prompt.findViewById(R.id.bk_transactions);
        bkTransactions.setChecked(true);
        final CheckBox bkEncrypt = (CheckBox) prompt.findViewById(R.id.bk_encrypt);
        bkEncrypt.setChecked(false);
        final CheckBox bkLocal = (CheckBox) prompt.findViewById(R.id.bk_local);
        bkLocal.setChecked(true);
        final CheckBox bkMail = (CheckBox) prompt.findViewById(R.id.bk_mail);
        bkMail.setChecked(false);

        final EditText txtkey    = (EditText) prompt.findViewById(R.id.bk_key);
        final EditText txtmailto = (EditText) prompt.findViewById(R.id.bk_mailto);
        txtmailto.setText(app.getPrefs().getString("usermail",""));

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(prompt);
        dialog.setTitle(getResources().getString(R.string.export_backup));
        dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                holder.progressBar.setVisibility(View.VISIBLE);

                try {
                    File file = app.getPublicDocumentDir();
                    String bkpath = null;
                    if (file != null) bkpath = file.getAbsolutePath();
                    else bkLocal.setChecked(false);
                    DoDump sqldump = new DoDump(bkAccounts.isChecked(),
                            bkCostcenters.isChecked(),
                            bkDebtors.isChecked(),
                            bkMeasures.isChecked(),
                            bkTransactions.isChecked(),
                            bkEncrypt.isChecked(),
                            txtkey.getText().toString(),
                            bkLocal.isChecked(),
                            bkpath,
                            bkMail.isChecked(),
                            txtmailto.getText().toString()
                        );
                    sqldump.execute(new String[] {});

                } catch (Exception e) {
                    Toast.makeText(ExportActivity.this, getResources().getText(R.string.export_ok), Toast.LENGTH_LONG).show();
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
            Toast.makeText(ExportActivity.this, getResources().getText(R.string.export_ok), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    } */


    /**
     * Mail sqldump
     */
    private class DoDump extends AsyncTask<String,Void,String> {

        boolean bkaccounts;
        boolean bkcostcenters;
        boolean bkdebtors;
        boolean bkmeasures;
        boolean bktransactions;
        boolean bkencrypt;
        String  bkkey;
        boolean bktopath;
        String bkpath;
        boolean bkmail;
        String bkmailto;

        /**
         * SQL-Dump db
         * @param baccounts
         * @param bcostcenters
         * @param bdebtors
         * @param bmeasures
         * @param btransactions
         * @param bencrypt
         * @param skey
         * @param btopath
         * @param spath
         * @param bmail
         * @param smailto
         */
        DoDump(boolean baccounts,
                boolean bcostcenters,
                boolean bdebtors,
                boolean bmeasures,
                boolean btransactions,
                boolean bencrypt,
                String  skey,
                boolean btopath,
                String spath,
                boolean bmail,
                String smailto) {
            bkaccounts = baccounts;
            bkcostcenters = bcostcenters;
            bkdebtors = bdebtors;
            bkmeasures = bmeasures;
            bktransactions = btransactions;
            bkencrypt = bencrypt;
            bkkey = skey;
            bktopath = btopath;
            bkpath = spath;
            bkmail = bmail;
            bkmailto = smailto;
        }

        @Override
        protected String doInBackground(String... params) {
            setProgress(1);
            try {

                File mypath = new File(app.getFilesDir(), "exports");
                if (!mypath.exists()) {
                    mypath.mkdir();
                }
                String filename = "exdump_"+lu.crghost.cralib3.tools.Formats.fileTimeToday() +".sql";
                File file = new File(mypath, filename);
                if (!file.exists()) {
                    file.createNewFile();
                    Log.i(TAG, "File " + file + " created");
                }
                file.setReadable(true,false);

                String curtime = MyFormats.formatDateTime.format(new Date(System.currentTimeMillis()));

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write("-- MYEX Data Dump " + curtime);
                writer.newLine();

                if (bkaccounts) {
                    writer.write(DbSqlDump.dump(app, new Account(),true));
                    writer.newLine();
                }
                if (bkcostcenters) {
                    writer.write(DbSqlDump.dump(app, new Costcenter(),true));
                    writer.newLine();
                }
                if (bkdebtors) {
                    writer.write(DbSqlDump.dump(app, new Debtor(),true));
                    writer.newLine();
                }
                //writer.write(DbSqlDump.dump(app, new Geotrack()));
                //writer.newLine();
                if (bkmeasures) {
                    writer.write(DbSqlDump.dump(app, new Measure(),true));
                    writer.newLine();
                }
                if (bktransactions) {
                    writer.write(DbSqlDump.dump(app, new Transaction(),true));
                    writer.newLine();
                }
                writer.close();

                if (bkencrypt) {
                    /* @TODO encrypt backup file */
                }

                // Copy backup to a public path
                if (bktopath && bkpath != null) {
                    FileChannel inchannel = null;
                    FileChannel outchannel = null;
                    File fileto = null;
                    try {
                        fileto = new File(bkpath,filename);
                        fileto.createNewFile();
                        inchannel = new FileInputStream(file).getChannel();
                        outchannel = new FileOutputStream(fileto).getChannel();
                        inchannel.transferTo(0, inchannel.size(),outchannel);
                    } catch (Exception e) {
                        Log.e(TAG,"Error creating " + fileto);
                        e.printStackTrace();
                    } finally {
                        if (inchannel != null) {
                            inchannel.close();
                        }
                        if (outchannel != null){
                            outchannel.close();
                        }
                        Log.i(TAG, "File " + fileto + " created");
                    }
                }

                if (bkmail && bkmailto != null) {
                    // Send mail
                    Intent mailintent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    mailintent.setType("text/plain");
                    mailintent.putExtra(Intent.EXTRA_EMAIL, new String[]{this.bkmailto});
                    mailintent.putExtra(Intent.EXTRA_SUBJECT, "MyEx backup from " + curtime);
                    mailintent.putExtra(Intent.EXTRA_TEXT, "");
                    Uri uri = FileProvider.getUriForFile(app, "lu.crghost.myex.fileprovider", file);
                    app.grantUriPermission(MyExProperties.APP_PACKAGE_NAME, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    uris.add(uri);
                    mailintent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
                    mailintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(mailintent, "Send email..."));
                }



            } catch(IOException e) {
                Log.e(TAG,"Error while creation file", e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            holder.progressBar.setVisibility(View.GONE);
            Toast.makeText(ExportActivity.this, getResources().getText(R.string.export_ok), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

}
