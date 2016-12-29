package yaoxin.example.com.popularmoves.support.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by yaoxinxin on 2016/12/26.
 */

public class MyInstanceIDListenerService extends InstanceIDListenerService {


    @Override
    public void onTokenRefresh() {
//        super.onTokenRefresh();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
