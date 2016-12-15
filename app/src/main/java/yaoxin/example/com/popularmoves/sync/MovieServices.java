package yaoxin.example.com.popularmoves.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by yaoxinxin on 2016/12/2.
 */

public class MovieServices extends Service {

    private static final Object MovieAdapterLock = new Object();
    private static MovieSyncAdapter mMovieSyncAdapter ;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (MovieAdapterLock){
            if(mMovieSyncAdapter == null){
                mMovieSyncAdapter = new MovieSyncAdapter(this.getApplicationContext(),true);
            }

        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMovieSyncAdapter.getSyncAdapterBinder();
    }
}
