package yaoxin.example.com.popularmoves.support.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.utils.Utils;

/**
 * Created by yaoxinxin on 2016/12/26.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    private static final String[] TOPICS = {"global"};

    public static final String REGISTRATION_COMPLETE = "registration_complete";

    public static final String SENT_TOKEN_TO_SERVER = "sent_token_to_server";


    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG,"start services");

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "token==" + token);
            sendRegistration2Server(token);
            subscribeTopics(token);
            Utils.getInstance().setBoolean(this.getApplicationContext(), SENT_TOKEN_TO_SERVER, true);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.getInstance().setBoolean(this.getApplicationContext(), SENT_TOKEN_TO_SERVER, false);
        }

        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    //do your work
    private void sendRegistration2Server(String token) {

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
