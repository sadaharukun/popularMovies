package yaoxin.example.com.popularmoves.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import yaoxin.example.com.popularmoves.MainActivity;

/**
 * Created by yaoxinxin on 2016/12/20.
 */

public class Utils {

    private static final String SHAREDNAME = "popularMovie";

    private static final String MovieCollected = "movieCollected";

    public static final String SORTWAYKEY = "sortway";
    public static final String POPULARWAY = "sortway_0";
    public static final String VOTEAVERAGEWAY = "sortway_1";

    public static final int NOTIFICATIONID = 10;
    private static final int PENDINGINTENTREQUESTCODE = 0;

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

    public void setString(Context c, String key, String value) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(Context c, String key, String defaultValue) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public void setBoolean(Context c, String key, boolean value) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(Context c, String key, boolean defaultvalue) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultvalue);
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

    public void showMovieNotification(Context c, String title, String text, int iconRes, int largeIconRes, String ticker, int number) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, PENDINGINTENTREQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(c)
                .setContentText(text).setContentTitle(title).setSmallIcon(iconRes)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), largeIconRes))
                .setWhen(System.currentTimeMillis())//设置发出通知的时间为发出通知时的系统时间
                .setTicker(ticker)
                .setOngoing(false)//设置为true则无法通过左右滑动清除，只能通过clear()清除
                .setAutoCancel(true)//点击通知后消失
                .setNumber(number)
                .setPriority(Notification.PRIORITY_DEFAULT)//默认方式
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)//优先级
                .build();
//        RemoteViews views;

        manager.notify(NOTIFICATIONID, notification);


    }


    public int[] getScreenWidthAndHeight(Context c) {
        int wh[] = new int[2];
        WindowManager windowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        wh[0] = windowManager.getDefaultDisplay().getWidth();
        wh[1] = windowManager.getDefaultDisplay().getHeight();
//        DisplayMetrics metrics = new DisplayMetrics();
        return wh;
    }


}
