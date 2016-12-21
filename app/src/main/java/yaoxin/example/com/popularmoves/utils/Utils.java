package yaoxin.example.com.popularmoves.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by yaoxinxin on 2016/12/20.
 */

public class Utils {

    private static final String SHAREDNAME = "popularMovie";

    private static final String MovieCollected = "movieCollected";

    public static final String SORTWAYKEY = "sortway";
    public static final String POPULARWAY = "sortway_0";
    public static final String VOTEAVERAGEWAY = "sortway_1";

    private static Utils instance;

    private Toast toast;

    private Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
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

    public void setSortway(Context c, String sort) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SORTWAYKEY, sort);
        editor.apply();
    }

    public String getSortway(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getString(SORTWAYKEY, POPULARWAY);
    }

    public void showToast(Context c, String text) {
        if (toast == null) {
            toast = Toast.makeText(c, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }


}
