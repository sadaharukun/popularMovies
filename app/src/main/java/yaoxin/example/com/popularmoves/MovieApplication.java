package yaoxin.example.com.popularmoves;

import android.app.Application;

/**
 * Created by yaoxinxin on 2016/12/13.
 */

public class MovieApplication extends Application {

    public static final String APIKEY = BuildConfig.PoPular_Apikey;

    public static MovieApplication instance;

    public static MovieApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
