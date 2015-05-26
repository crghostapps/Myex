package lu.crghost.myex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import lu.crghost.myex.tools.FileOpenDialog;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImportActivity extends Activity {

    private final String TAG = getClass().getName();
    FileOpenDialog fileDialog;
    MyExApp app;
    File fileToRestore;

    static class ViewHolder {
        public ProgressBar progressBar;
    }
    ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        app = (MyExApp) getApplication();
        holder = new ViewHolder();
        holder.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        holder.progressBar.setVisibility(View.GONE);
    }


    public void action_import_bankfile(View v) {}

    public void action_import_restore(View v) {

        File mPath = app.getPublicDocumentDir();
        fileDialog = new FileOpenDialog(this, mPath);
        fileDialog.setFileEndsWith(".sql");
        fileDialog.setSelectDirectoryOption(false);
        fileDialog.setTitle(getResources().getString(R.string.import_filedialog_title));
        fileDialog.addFileListener(new FileOpenDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                try {
                    fileToRestore = file;
                    checkRestore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fileDialog.showDialog();

    }

    private void checkRestore() throws Exception {
        String line = null;
        List<String> tabelsToRestore = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileToRestore));
        while ( (line = reader.readLine()) != null ) {
            if (line.trim().toUpperCase().startsWith("INSERT INTO")) {
                String table = line.substring(11).trim();
                table = table.substring(0,table.indexOf(" ")).trim();
                if (!tabelsToRestore.contains(table)) tabelsToRestore.add(table);
            }
        }
        reader.close();

        // Confirm restore
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.import_confirm_restore));
        String message = getResources().getString(R.string.import_confirm_restore_message) + "\n";
        message = message + fileToRestore.getName() + "\n";
        for (String t:tabelsToRestore) {
            message = message + t + "\n";
        }
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.stat_sys_warning);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (doRestore()) {
                    Toast.makeText(ImportActivity.this, getResources().getText(R.string.import_restore_ok), Toast.LENGTH_LONG).show();
                    finish();
                    return;
                } else {
                    Toast.makeText(ImportActivity.this, getResources().getText(R.string.import_restore_error), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private boolean doRestore()  {
        boolean ok = false;
        String line = null;
        Log.i(TAG,"Restoring " + fileToRestore);
        SQLiteDatabase db = app.getDataManager().getDb();
        db.beginTransaction();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileToRestore));
            while ((line = reader.readLine()) != null) {
                if (line.trim().endsWith(";")) {
                    Log.i(TAG, line);
                    db.rawExecSQL(line);
                }
            }
            db.setTransactionSuccessful();
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            if (reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ok;
    }

}
