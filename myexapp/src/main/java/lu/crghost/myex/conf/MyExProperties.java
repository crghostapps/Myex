package lu.crghost.myex.conf;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * Created by CR on 24/12/2014.
 */
public final class MyExProperties {

    private static final String APP_PACKAGE_NAME = "lu.crghost.myex";
    public static final String  DATABASE_NAME = "myex.db" ;
    private static final String EXTERNAL_DATA_DIR_NAME = "myexdata";
    public static final String  EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + "/" + EXTERNAL_DATA_DIR_NAME;
    public static final String  DATABASE_PATH = Environment.getDataDirectory() + "/data/" + APP_PACKAGE_NAME + "/databases/" + DATABASE_NAME;

    public static String getVersionName(Context c) {
        String versionName = "Unknown version";
        try {
            PackageInfo pinfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionName;
    }

}

