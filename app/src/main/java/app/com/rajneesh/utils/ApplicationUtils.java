package app.com.rajneesh.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Contains all the Utility method used in project
 */
public class ApplicationUtils {

    // Show Log
    public static void showLogError(String TAG, String message){
        Log.e(TAG, message);
    }

    // Show the short Toast message
    public static void showShortToast(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    // Show the long Toast message
    public static void showLongToast(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
