package lu.crghost.myex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import lu.crghost.cralib3.security.StringEncoder;
import lu.crghost.myex.activities.MainFragment;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private boolean firstlogin = true;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i(TAG,"-------------------- MYEX is starting -----------------------");
        Log.i(TAG,"App-Version = " + MyExApp.getVersionName(this));
        Log.i(TAG,"DB-Version  = " + MyExApp.DATABASE_VERSION);
        Log.i(TAG,"--------------------------------------------------------------------");
        setContentView(R.layout.activity_login);

        // set preferences
        PreferenceManager.setDefaultValues(this,MyExApp.PREFS_FILENAME, Context.MODE_MULTI_PROCESS,R.xml.preferences,false);
        preferences = getSharedPreferences(MyExApp.PREFS_FILENAME, Context.MODE_PRIVATE);
        firstlogin = preferences.getBoolean("firstlogin", true);

        final TextView lbl1message = (TextView) findViewById(R.id.lblinit);
        final CheckBox chkBoxpass  = (CheckBox) findViewById(R.id.chkPass);

        if (!firstlogin) {
            boolean chkpass = preferences.getBoolean("checkpass", true);
            if (!chkpass) startMainFragment();
            lbl1message.setVisibility(View.GONE);
            chkBoxpass.setVisibility(View.GONE);
        }

    }

    public void actionLogin(View button) {

        final EditText txtpass = (EditText) findViewById(R.id.txtpass);
        if (txtpass.getText().toString().length() < 1) {
            txtpass.setError(getResources().getString(R.string.login_passwd_err));
            return;
        }

        if (firstlogin) {

            final CheckBox chkBoxpass  = (CheckBox) findViewById(R.id.chkPass);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstlogin", false);
            editor.putBoolean("checkpass", chkBoxpass.isChecked());
            editor.putString("dbpassword", StringEncoder.encode(this, txtpass.getText().toString()));
            editor.commit();

            MyExApp app = (MyExApp) getApplication();
            app.reloadPreferences();
            app.reloadDataManager();
            app.initData();

        } else {

            String password = StringEncoder.decode(this, preferences.getString("dbpassword",""));
            if (!password.equals(txtpass.getText().toString())) {
                txtpass.setError(getResources().getString(R.string.login_passwd_err2));
                return;
            }

        }
        startMainFragment();

    }

    private void startMainFragment() {
        Intent mainFragment = new Intent(this, MainFragment.class);
        startActivity(mainFragment);
        finish();
    }


}
