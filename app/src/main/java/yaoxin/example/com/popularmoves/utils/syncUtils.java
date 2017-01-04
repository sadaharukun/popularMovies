package yaoxin.example.com.popularmoves.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import yaoxin.example.com.popularmoves.R;


/**
 * Created by yaoxinxin on 2016/12/30.
 */

public class SyncUtils {


    private static final String TAG = "SyncUtils";
    public static final int SYNC_INTERVAL = 60 * 180;//3hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;


    public static Account createAccount(Context c) {
        Account newAccount = new Account(c.getString(R.string.app_name), c.getString(R.string.account_type));
        AccountManager accountManager = (AccountManager) c.getSystemService(Context.ACCOUNT_SERVICE);
//        if (null == accountManager.getPassword(newAccount)) {
//
//            if (!accountManager.addAccountExplicitly(newAccount, c.getString(R.string.account_psw), null)) {
//                return null;
//            }
//
//            onCreateAccount(newAccount, c);
//        }
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            ContentResolver.setIsSyncable(newAccount, c.getString(R.string.content_authority), 1);
            ContentResolver.setSyncAutomatically(newAccount, c.getString(R.string.content_authority), true);
            onCreateAccount(newAccount, c);
        } else {
            //if account exists or error
        }
        return newAccount;
    }

    public static void onCreateAccount(Account newAccount, Context c) {

        configurePeriodicSync(c, SYNC_INTERVAL, SYNC_FLEXTIME);
//        ContentResolver.SYNC_EXTRAS_MANUAL = true;
        ContentResolver.setIsSyncable(newAccount, c.getString(R.string.content_authority), 1);
        ContentResolver.setSyncAutomatically(newAccount, c.getString(R.string.content_authority), true);
        syncImmediately(c, 0, 1);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.i(TAG, "configurePeriodicSync....");
        Account account = createAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context c, int flag, int page) {

        Account account = createAccount(c);
        if (ContentResolver.isSyncPending(account, c.getString(R.string.content_authority))
                || ContentResolver.isSyncActive(account, c.getString(R.string.content_authority))) {

            Log.i("ContentResolver", "SyncPending, canceling");
            ContentResolver.cancelSync(account, c.getString(R.string.content_authority));
        }


//        Account account = createAccount(c);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt("display_flag", flag);
        bundle.putInt("display_page", page);
        ContentResolver.requestSync(account,
                c.getString(R.string.content_authority), bundle);


    }


}
