package yaoxin.example.com.popularmoves.support.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by yaoxinxin on 2016/12/26.
 */

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);
        Log.i("GcmListener", s);
    }
}
