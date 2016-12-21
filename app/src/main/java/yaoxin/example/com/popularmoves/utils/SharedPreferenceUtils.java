package yaoxin.example.com.popularmoves.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yaoxinxin on 2016/12/20.
 */

public class SharedPreferenceUtils {

    private static final String SHAREDNAME = "popularMovie";

    private static final String MovieCollected = "movieCollected";

    private static SharedPreferenceUtils instance;

    public SharedPreferenceUtils() {

    }

    public static SharedPreferenceUtils getInstance() {
        if (instance == null) {
            synchronized (SharedPreferenceUtils.class) {
                if (instance == null) {
                    instance = new SharedPreferenceUtils();
                }
            }
        }

        return instance;
    }

    public void setMovieCollected(Context c, boolean collected) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(MovieCollected, collected);
        editor.apply();
    }

    public Boolean IsMovieCollected(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(MovieCollected, false);
    }


}
