package yaoxin.example.com.popularmoves.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import yaoxin.example.com.popularmoves.MovieApplication;
import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;

/**
 * Created by yaoxinxin on 2016/12/1.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = MovieSyncAdapter.class.getSimpleName();

    public static final String LOADFINISHACTION = "yaoxin.example.com.popularmoves.loadfinish";
    public static final int TIMEOUT = 10 * 1000;
    public static final int SYNC_INTERVAL = 60 * 180;//3hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final int RESPONSE_OK = 200;
    private ContentResolver resolver;
    private Context c;

    public String popularurl = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public String voteUrl = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String SEPARATOR = "&";


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.c = context;
        resolver = context.getContentResolver();
    }

    public MovieSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.c = context;
        resolver = context.getContentResolver();
    }

    /**
     * parse data here
     *
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "start sync...");
        int flag = extras.getInt("display_flag");
        int page = extras.getInt("display_page");
        Log.i(TAG, "page==" + page);
        String path = popularurl + MovieApplication.APIKEY + SEPARATOR + "page=" + page;
        if (flag == 0) {
            path = popularurl + MovieApplication.APIKEY + SEPARATOR + "page=" + page;
        } else if (flag == 1) {
            path = voteUrl + MovieApplication.APIKEY + SEPARATOR + "page=" + page;
        }
        InputStreamReader reader = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(TIMEOUT);
            int code = conn.getResponseCode();
            if (code == RESPONSE_OK) {
                StringBuffer buffer = new StringBuffer();
                char[] buf = new char[1024 * 10];
                reader = new InputStreamReader(conn.getInputStream());
                int len = -1;
                while (-1 != (len = reader.read(buf))) {
                    String s = new String(buf, 0, len);
                    buffer.append(s);
                }

                Log.d(TAG, buffer.toString());
                JSONObject obj = new JSONObject(buffer.toString());
                int pages = obj.optInt("total_pages");
                JSONArray array = obj.optJSONArray("results");
//                ContentValues[] valuesarr = new ContentValues[array.length()];
                List<ContentValues> valuesList = new ArrayList<>();
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        String moveId = String.valueOf(object.optInt("id"));

                        //update table movieType(popular,votevrage)
//                        Cursor cursor = resolver.query(MovieContract.CONTENT_TYPE_URI, new String[]{MovieTypeEntry._ID},
//                                MovieTypeEntry.MOVIEID + "=?", new String[]{moveId}, null);
//                        ContentValues values1 = new ContentValues();
//                        if (flag == 0) {
//                            values1.put(MovieTypeEntry.POPULAR, "1");
//                        } else if (flag == 1) {
//                            values1.put(MovieTypeEntry.VOTEAVERAGE, "1");
//                        }
//                        if (cursor != null && cursor.moveToFirst()) {
//                            resolver.update(MovieContract.CONTENT_TYPE_URI, values1, MovieTypeEntry.MOVIEID + "=?", new String[]{moveId});
//                        } else {
//                            values1.put(MovieTypeEntry.MOVIEID, moveId);
//                            resolver.insert(MovieContract.CONTENT_TYPE_URI, values1);
//                        }

                        Cursor cur = resolver.query(MovieContract.CONTENT_MOVE_URI, new String[]{MovieEntry._ID},
                                MovieEntry.MOVIEID + "=?", new String[]{moveId}, null);
                        if (cur != null && cur.moveToFirst()) {
                            continue;
                        }
                        ContentValues values = new ContentValues();
                        values.put(MovieEntry.TITLE, object.optString("title"));
                        values.put(MovieEntry.MOVIEID, moveId);
                        values.put(MovieEntry.POSTURL, object.optString("poster_path"));
                        values.put(MovieEntry.BACKDROPURL, object.optString("backdrop_path"));
                        values.put(MovieEntry.OVERVIEW, object.optString("overview"));
                        values.put(MovieEntry.VOTEAVERAGE, object.optDouble("vote_average"));
                        values.put(MovieEntry.POPULARITY, object.optDouble("popularity"));
                        values.put(MovieEntry.REALEASEDATE, object.optString("release_date"));
                        values.put(MovieEntry.COLLECTED, "0");
                        JSONArray json_genres = object.optJSONArray("genre_ids");
                        StringBuffer genres = new StringBuffer();
                        if (json_genres != null) {
                            for (int j = 0; j < json_genres.length(); j++) {
                                int genre = json_genres.getInt(j);
                                if (j != json_genres.length() - 1) {
                                    genres.append(genre);
                                    genres.append("/");
                                } else {
                                    genres.append(genre);
                                }
                            }
                        }
                        values.put(MovieEntry.GENRES, genres.toString());
                        valuesList.add(values);

                        cur.close();
                    }
                    Log.d(TAG, "result.length==" + valuesList.size());
                    int num = resolver.bulkInsert(MovieContract.CONTENT_MOVE_URI, valuesList.toArray(new ContentValues[valuesList.size()]));
                    if (num < 0) {
                        throw new SQLiteException("insert error..");
                    }
//                    sendLoadfinshBroadcast(c);
                    return;
                }
            } else {
                System.out.print("parse failed.....");
                return;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        catch (RemoteException e) {
//            e.printStackTrace();
//        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onSyncCanceled() {
        Log.i(TAG, "onSyncCanceled...");
        super.onSyncCanceled();

    }

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

//    public static Account getAccount(Context c, String accountType) {
//        AccountManager accountManager = (AccountManager) c.getSystemService(Context.ACCOUNT_SERVICE);
//        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return null;
//        }
//        Account[] accounts = accountManager.getAccountsByType(accountType);
//        if (accounts != null && accounts.length > 0) {
//            Log.i(TAG, "getaccount...");
//            return accounts[0];
//        } else {
//            return createAccount(c);
//        }
//
//    }


    public static void onCreateAccount(Account newAccount, Context c) {

        configurePeriodicSync(c, SYNC_INTERVAL, SYNC_FLEXTIME);
//        ContentResolver.SYNC_EXTRAS_MANUAL = true;
//        ContentResolver.setIsSyncable(newAccount, c.getString(R.string.content_authority), 1);
//        ContentResolver.setSyncAutomatically(newAccount, c.getString(R.string.content_authority), true);
        syncImmediately(c, 0, 1);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.i(TAG, "configurePeriodicSync....");
        Account account = createAccount(context);
        String authority = context.getString(R.string.content_authority);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // we can enable inexact timers in our periodic sync
//            SyncRequest request = new SyncRequest.Builder().
//                    syncPeriodic(syncInterval, flexTime).
//                    setSyncAdapter(account, authority).
//                    setExtras(new Bundle()).build();
//            ContentResolver.requestSync(request);
//        } else {
        ContentResolver.addPeriodicSync(account,
                authority, new Bundle(), syncInterval);
//        }
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

    private void sendLoadfinshBroadcast(Context c) {
        Intent intent = new Intent();
        intent.setAction(LOADFINISHACTION);
        c.sendBroadcast(intent);
    }
}
