package lu.crghost.myex.conf;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * General constants
 */
public final class MyExProperties {

    private static final String APP_PACKAGE_NAME = "lu.crghost.myex";
    public static final String  DATABASE_NAME = "myex.db" ;
    public static final String  DATABASE_NAME_CLEAN = "myexclean.db" ;
    private static final String EXTERNAL_DATA_DIR_NAME = "myexdata";
    public static final String  EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + "/" + EXTERNAL_DATA_DIR_NAME;
    public static final String  DATABASE_PATH = Environment.getDataDirectory() + "/data/" + APP_PACKAGE_NAME + "/databases/" + DATABASE_NAME;
    public static final String  DATABASE_PATH_CLEAN = Environment.getDataDirectory() + "/data/" + APP_PACKAGE_NAME + "/databases/" + DATABASE_NAME_CLEAN;

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

