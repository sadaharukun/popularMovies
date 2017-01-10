package yaoxin.example.com.popularmoves;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import yaoxin.example.com.popularmoves.asyncTask.MovieDetailAsyncTask;
import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;
import yaoxin.example.com.popularmoves.data.ReviewsEntry;
import yaoxin.example.com.popularmoves.data.VideosEntry;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.support.ReviewsAdapter;
import yaoxin.example.com.popularmoves.support.ThreadPoolManager;
import yaoxin.example.com.popularmoves.utils.Utils;

import static yaoxin.example.com.popularmoves.R.id.collect;

/**
 * 电影详情页
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetailActivity";

    public static final int REVIEWS_LOADER_ID = 101;

    private static final String FAVORITE = "0";
    private static final String FAVORITE_ED = "1";
    public static final int MSG_SHOWQRCODE = 200;
    public static final int QRVERSION = 160;

    private Loader<Cursor> mLoaderManager;

    private String base_url = "https://image.tmdb.org/t/p/w500";

    private String base_youtube_url = "https://www.youtube.com/watch?v=";

    private Move move;

    private ScrollView mScrollview;

    private Toolbar mToolbar;

    private TextView mName;

    private ImageView mCollect;

    private RelativeLayout mBackDrop;

    private ImageView mBackdropImageView;

    private TextView mReleaseTime;

    private TextView mGenre;

    private TextView mCountry;

    private RatingBar mAverage;

    private TextView mAverageNum;

    private TextView mOverView;
    private ExpandableTextView expandableTextView;

    private ListView mListView;
    private ReviewsAdapter mAdapter;

    public static final String[] REVIEWS_PROJECTIONS = new String[]{
            ReviewsEntry._ID,
            ReviewsEntry.AUTHOR_ID,
            ReviewsEntry.AUTHOR,
            ReviewsEntry.AUTHOR_URL,
            ReviewsEntry.COMMENTS,
            ReviewsEntry.MOVE_ID

    };
    public static final int COL_REVIEWS_ID = 0;
    public static final int COL_REVIEWS_AUTHORID = 1;
    public static final int COL_REVIEWS_AUTHOR = 2;
    public static final int COL_REVIEWS_AUTHORURL = 3;
    public static final int COL_REVIEWS_COMMENTS = 4;
    public static final int COL_REVIEWS_MOVEID = 5;

    private String mprefavorite;
    private String mCurrentfavorite;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MovieDetailAsyncTask.DETAIL_MOVIE) {
                if (msg.obj != null) {
                    inflateData((Cursor) msg.obj);
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        Utils.hideNavigationBar(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Log.i(TAG, "onCreate()");
        getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);

        Intent intent = getIntent();
        move = (Move) intent.getSerializableExtra("move");
        mScrollview = (ScrollView) this.findViewById(R.id.scrollView);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mName = (TextView) this.findViewById(R.id.name);
        mCollect = (ImageView) this.findViewById(collect);
        mBackDrop = (RelativeLayout) this.findViewById(R.id.backdrop);
        mBackdropImageView = (ImageView) this.findViewById(R.id.backdrop_img);
        mReleaseTime = (TextView) this.findViewById(R.id.releaseTime);
        mGenre = (TextView) this.findViewById(R.id.genre);
        mAverage = (RatingBar) this.findViewById(R.id.voteAverage);
        mAverageNum = (TextView) this.findViewById(R.id.voteAverage_num);
        mCountry = (TextView) this.findViewById(R.id.movie_country);
//        mOverView = (TextView) this.findViewById(R.id.overview);
        mListView = (ListView) this.findViewById(R.id.reviews);

        expandableTextView = (ExpandableTextView) this.findViewById(R.id.expandableView);

        mToolbar.setTitle(getString(R.string.moveDetail));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);

        if (move != null) {
            mprefavorite = move.getCollected();
            mCurrentfavorite = mprefavorite;
            if (FAVORITE.equals(move.getCollected())) {
                mCollect.setImageResource(R.mipmap.favorite);
            } else if (FAVORITE_ED.equals(move.getCollected())) {
                mCollect.setImageResource(R.mipmap.favorite_ed);
            }
        }

        MovieDetailAsyncTask asyncTask = new MovieDetailAsyncTask(this, mHandler);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MovieDetailAsyncTask.DETAIL_MOVIE, move.getMoveId());

        MovieDetailAsyncTask asyncTask2 = new MovieDetailAsyncTask(this, mHandler);
        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MovieDetailAsyncTask.GET_REVIEWS, move.getMoveId());

        MovieDetailAsyncTask videoTask = new MovieDetailAsyncTask(DetailActivity.this, mHandler);
        videoTask.execute(MovieDetailAsyncTask.GET_VIDEOS, move.getMoveId());

        mAdapter = new ReviewsAdapter(this, null, true);
        mListView.setAdapter(mAdapter);

        mScrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                finish();
            }
        });
        mCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (move != null) {
//                    String collect = move.getCollected();

                    ContentValues values = new ContentValues();
                    ContentResolver resolver = getContentResolver();
                    String movieId = move.getMoveId();
                    Log.i(TAG, "moveId == " + move.getMoveId());
                    if (FAVORITE.equals(mCurrentfavorite)) {
                        values.clear();
                        values.put(MovieEntry.COLLECTED, FAVORITE_ED);
                        resolver.update(MovieContract.CONTENT_MOVE_URI, values, MovieEntry.MOVIEID + "=?",
                                new String[]{movieId});
                        ((ImageView) v).setImageResource(R.mipmap.favorite_ed);
                        mCurrentfavorite = FAVORITE_ED;
                    } else {
                        values.clear();
                        values.put(MovieEntry.COLLECTED, FAVORITE);
                        resolver.update(MovieContract.CONTENT_MOVE_URI, values, MovieEntry.MOVIEID + "=?",
                                new String[]{movieId});
                        ((ImageView) v).setImageResource(R.mipmap.favorite);
                        mCurrentfavorite = FAVORITE;
                    }

                }

            }
        });
        mBackDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentResolver resolver = DetailActivity.this.getContentResolver();
                Cursor cursor = resolver.query(MovieContract.CONTENT_VIDEOS_URI, new String[]{VideosEntry.VIDEO_KEYS},
                        VideosEntry.MOVIE_ID + "=?", new String[]{move.getMoveId()}, null);
                String path = "";
                if (cursor != null && cursor.moveToFirst()) {
                    String keys = cursor.getString(0);
                    if (keys.contains("#")) {
                        path = base_youtube_url + keys.split("#")[0];
                    } else {
                        path = base_youtube_url + keys;
                    }
                    Intent intent1 = new Intent();
                    intent1.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(path);
                    intent1.setData(content_url);
                    startActivity(intent1);
                }

            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (move != null) {

            mName.setText(move.getTitle());
            String url = base_url + move.getBackDropUrl();
            Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(mBackdropImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            mReleaseTime.setText(move.getReleaseDate() + "/" + "140(分钟)");
            mAverage.setRating((float) move.getVoteAverage() / 2.0f);
            Log.i(TAG, "genre==" + move.getGenres());
            mGenre.setText(move.getGenres());
            System.out.println("voteaverage=" + move.getVoteAverage());
            mAverageNum.setText(String.valueOf(move.getVoteAverage()));
            expandableTextView.setText(move.getOverView());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ondestroy..");
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onbackpress..");
        setResult();
        super.onBackPressed();
    }

    private void setResult() {
        if (mprefavorite.equals(mCurrentfavorite)) {
            this.setResult(DisplayFragment.RESULTCODE_DETAILACTIVITY_COLLECT_NOTCHANGE);
        } else {
            this.setResult(DisplayFragment.RESULTCODE_DETAILACTIVITY_COLLECTCHANGE);
        }
    }

    private void inflateData(Cursor cursor) {

        if (cursor.moveToFirst()) {
            mGenre.setText(cursor.getString(DisplayFragment.COL_MOVIE_GENRES));
            mCountry.setText(cursor.getString(DisplayFragment.COL_MOVIE_PRODUCTIONCONTRY));
            mReleaseTime.setText(move.getReleaseDate() + "/" + cursor.getInt(DisplayFragment.COL_MOVIE_RUNTIME) + "分钟");
        }

    }


    private android.support.v7.widget.ShareActionProvider mShareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        MenuItem item = menu.findItem(R.id.item_menu_share);
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareMovie();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_menu_qrCode:
                Log.i(TAG, "qrCode click..");
                if (move != null) {
                    Runnable runnable = new VideoRunnable(this, handler);
                    ThreadPoolManager.newInstance().creataPool().run(runnable);
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }


    private void shareMovie() {
        String movieId = "";
        String title = "";
        String youtubeKey = "";
        ContentResolver resolver = this.getContentResolver();
        if (move != null) {
            movieId = move.getMoveId();
            title = move.getTitle();
            Cursor cursor = resolver.query(MovieContract.CONTENT_VIDEOS_URI, new String[]{VideosEntry.VIDEO_KEYS}, VideosEntry.MOVIE_ID + "=?",
                    new String[]{movieId}, null);
            if (cursor != null && cursor.moveToFirst()) {
                String keys = cursor.getString(0);
                if (keys.contains("#")) {
                    youtubeKey = keys.split("#")[0];
                } else {
                    youtubeKey = keys;
                }
            }
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + base_youtube_url + youtubeKey);
        shareIntent.setType("text/plain");
        Log.i(TAG, "share click..");
        mShareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == REVIEWS_LOADER_ID) {
            if (move != null) {
                return new CursorLoader(this, MovieContract.CONTENT_REVIEWS_URI, REVIEWS_PROJECTIONS,
                        ReviewsEntry.MOVE_ID + "=?", new String[]{move.getMoveId()}, null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (mShareActionProvider != null) {
            shareMovie();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentCollect", mCurrentfavorite);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void showQRCode(Bitmap bitmap, int version) {
        int size = (version - 1) * 4 + 21;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ViewGroup rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        View view = LayoutInflater.from(this).inflate(R.layout.alterdialog_qrcode, rootView, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageQRCode);
        imageView.setImageBitmap(bitmap);
        builder.setView(view);
        builder.setTitle(getString(R.string.video));
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOWQRCODE) {
                Bitmap bitmap = (Bitmap) msg.obj;
                showQRCode(bitmap, QRVERSION);
            }

        }
    };

    class VideoRunnable implements Runnable {
        Context c;
        Handler handler;

        VideoRunnable(Context c, Handler handler) {
            this.c = c;
            this.handler = handler;
        }

        @Override
        public void run() {

            ContentResolver resolver = c.getContentResolver();
            Cursor cursor = resolver.query(MovieContract.CONTENT_VIDEOS_URI, new String[]{VideosEntry.VIDEO_KEYS},
                    VideosEntry.MOVIE_ID + "=?", new String[]{move.getMoveId()}, null);
            String path = "";
            if (cursor != null && cursor.moveToFirst()) {
                String keys = cursor.getString(0);
                if (keys.contains("#")) {
                    path = base_youtube_url + keys.split("#")[0];
                } else {
                    path = base_youtube_url + keys;
                }
//                        Bitmap bitmap = Utils.createQRCode(path, 160);
                Bitmap bitmap = Utils.createQRCodewithLogo(path, 160, BitmapFactory.decodeResource(getResources(),
                        R.mipmap.movie_default));
                handler.obtainMessage(MSG_SHOWQRCODE, bitmap).sendToTarget();
//                showQRCode(bitmap, 160);
            }
        }


    }
}
