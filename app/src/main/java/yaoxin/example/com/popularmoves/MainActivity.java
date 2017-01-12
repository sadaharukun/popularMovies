package yaoxin.example.com.popularmoves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import yaoxin.example.com.popularmoves.fragment.AboutFragment;
import yaoxin.example.com.popularmoves.fragment.CinemaFragment;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;
import yaoxin.example.com.popularmoves.fragment.HelpFragment;
import yaoxin.example.com.popularmoves.fragment.ItemFragment;
import yaoxin.example.com.popularmoves.fragment.RefreshListener;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.support.AccountBean;
import yaoxin.example.com.popularmoves.support.LoginRunnable;
import yaoxin.example.com.popularmoves.support.ObtainAccountListener;
import yaoxin.example.com.popularmoves.sync.MovieSyncAdapter;
import yaoxin.example.com.popularmoves.utils.Utils;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    public static final int LOGIN_SESSION = 10;

    private FrameLayout mContentView;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerLeft;
    private Toolbar mToobar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    private static final String MOVIEFRAGMENT = "moviefragment";

    public String popularurl = "http://api.themoviedb.org/3/movie/popular?language=zh&api_key=";
    public String voteUrl = "http://api.themoviedb.org/3/movie/top_rated?language=zh&api_key=";
    public String apikey = "a140c3ec88077e7e6c5e738197232747";

    private static final int REQUEST_CODE = 100;

    private FragmentManager manager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOGIN_SESSION) {
                if ((Boolean) msg.obj) {
                    handleAccount(msg);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.loginfailed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private ObtainAccountListener mAccountListener;

    private void handleAccount(Message msg) {
        Bundle data = msg.getData();
        String session_id = data.getString("session_id");
        String accountmsg = data.getString("accountmsg");
        try {
            JSONObject accountJson = new JSONObject(accountmsg);
            String name = accountJson.getString("name");
            String username = accountJson.getString("username");
            Boolean includeadult = accountJson.getBoolean("include_adult");
            String id = accountJson.getString("id");
            String hash = accountJson.getJSONObject("avatar").getJSONObject("gravatar").getString("hash");
            AccountBean account = new AccountBean(id, hash, includeadult, name, username);
            if (mAccountListener != null)
                mAccountListener.obtainAccount(account);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.hideNavigationBar(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        MovieSyncAdapter.createAccount(this);

        mContentView = (FrameLayout) this.findViewById(R.id.frame_content);
        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mToobar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(mToobar);
//        mDrawerLeft = (ListView) this.findViewById(R.id.left_drawer);
//        ItemFragment fragment = ItemFragment.newInstance(popularurl, apikey, 2);

        DisplayFragment fragment = DisplayFragment.newInstance(popularurl, apikey, 2);
        manager = this.getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_content, fragment, MOVIEFRAGMENT);
        transaction.commit();

        Utils.getInstance().showMovieNotification(this, getString(R.string.app_name), "this is a test",
                R.mipmap.moive, R.mipmap.moive, "ticker ticker", 2);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToobar, R.string.open_drawer, R.string.close_drawer) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        inflateHeadView(headerView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.movie);

    }

    private void inflateHeadView(View headerView) {
        LinearLayout.LayoutParams params = (LinearLayout
                .LayoutParams) headerView.getLayoutParams();
        params.height = Utils.dp2px(this, 200);
        final CircleImageView face = (CircleImageView) headerView.findViewById(R.id.face);
        final TextView textview_name = (TextView) headerView.findViewById(R.id.name);
        final TextView textview_username = (TextView) headerView.findViewById(R.id.username);
        this.setAccountListener(new ObtainAccountListener() {
            @Override
            public void obtainAccount(AccountBean bean) {
                textview_name.setText(bean.name);
                textview_username.setText(bean.username);
                face.setBackgroundResource(R.mipmap.user);
            }
        });
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "login onclick..");
                //8b6dba29b4719ca30943c55284acd58d5c333d95
                loginAccountOrAccountDetail(v);
            }
        });


    }

    /**
     * here is two method to login(1.developer login use apikey 2.normal user login use uesrname,password)
     * we use the second method.
     *
     * @param v
     */
    private void loginAccountOrAccountDetail(View v) {
        //request token  b74a4e1c05c2b424d0a53a033cb6a601cea6c0cc
        //e6927f69fa9d576e2b1ffa9818e946b9d955190d
        //session_id 895707742b02b294d38932be787844b9da36576f
        LoginRunnable loginRunnable = new LoginRunnable(this, "yaoxinbeijing", "7256338", handler);
        new Thread(loginRunnable).start();

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Utils.hideNavigationBar(this);
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

    public void setAccountListener(ObtainAccountListener listener) {
        this.mAccountListener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }

        FragmentTransaction transaction = manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.movie:
                Log.i(TAG, "movie....");
                mToobar.setTitle(getString(R.string.movie));
                DisplayFragment displayFragment = (DisplayFragment) manager.findFragmentByTag(MOVIEFRAGMENT);
                if (displayFragment == null) {
                    displayFragment = new DisplayFragment();
                }
                transaction.replace(R.id.frame_content, displayFragment, MOVIEFRAGMENT);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.cinema:
                Log.i(TAG, "cinema..");
                mToobar.setTitle(getString(R.string.cinema));
                CinemaFragment fragment = new CinemaFragment();
                transaction.replace(R.id.frame_content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.drawer_about:
                mToobar.setTitle(getString(R.string.about));
                AboutFragment aboutFragment = new AboutFragment();
                transaction.replace(R.id.frame_content, aboutFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.help:
                mToobar.setTitle(getString(R.string.help));
                HelpFragment helpFragment = new HelpFragment();
                transaction.replace(R.id.frame_content, helpFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
        }


        return false;
    }

    public class MyGCMReceiver extends GcmReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


        }
    }
}
