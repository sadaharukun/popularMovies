package yaoxin.example.com.popularmoves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.google.android.gms.gcm.GcmReceiver;

import yaoxin.example.com.popularmoves.fragment.DisplayFragment;
import yaoxin.example.com.popularmoves.fragment.ItemFragment;
import yaoxin.example.com.popularmoves.fragment.RefreshListener;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.sync.MovieSyncAdapter;
import yaoxin.example.com.popularmoves.utils.Utils;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    private FrameLayout mContentView;


    private static final String ITEMFRAGMENTTAG = "itemFragmentTag";

    public String popularurl = "http://api.themoviedb.org/3/movie/popular?language=zh&api_key=";
    public String voteUrl = "http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=";
    public String apikey = "a140c3ec88077e7e6c5e738197232747";

    private static final int REQUEST_CODE = 100;

    private FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MovieSyncAdapter.createAccount(this);

        mContentView = (FrameLayout) this.findViewById(R.id.frame_content);
//        ItemFragment fragment = ItemFragment.newInstance(popularurl, apikey, 2);
        DisplayFragment fragment = DisplayFragment.newInstance(popularurl, apikey, 2);
        manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_content, fragment, ITEMFRAGMENTTAG);
        transaction.commit();

        Utils.getInstance().showMovieNotification(this, getString(R.string.app_name), "this is a test",
                R.mipmap.moive, R.mipmap.moive, "ticker ticker", 2);


    }

    private BroadcastReceiver mGCMReceiver;

    @Override
    protected void onStart() {
        super.onStart();


//        Intent intent = new Intent(this, MovieSyncServices.class);
//        intent.setPackage();
//        this.startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("activity onActivityResult");
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.itemfragment, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.menu_refresh:
//                System.out.println("111111111");
//                if (refreshListener != null) {
//                    System.out.print("ready to refresh..");
//                    refreshListener.refresh();
//                }
//                return true;
//
//            case R.id.menu_setting:
//                Intent intent = new Intent(this, SettingActivity.class);
//
//                this.startActivityForResult(intent, REQUEST_CODE);
//
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ITEMFRAGMENTTAG);
//        if (fragment != null) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
//
//    }

    @Override
    public void onLitFragmentInteraction(Move item) {

        if (item != null) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("move", item);
            this.startActivity(intent);
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


    public RefreshListener refreshListener;

    public RefreshListener getRefreshListener() {
        return refreshListener;
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public class MyGCMReceiver extends GcmReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


        }
    }
}
