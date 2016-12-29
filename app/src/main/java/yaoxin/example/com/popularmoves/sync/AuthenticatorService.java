package yaoxin.example.com.popularmoves.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by yaoxinxin on 2016/12/22.
 */

public class AuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private MovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
