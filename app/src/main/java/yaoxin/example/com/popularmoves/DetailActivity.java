package yaoxin.example.com.popularmoves;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import yaoxin.example.com.popularmoves.asyncTask.MovieDetailAsyncTask;
import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;
import yaoxin.example.com.popularmoves.data.ReviewsEntry;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.support.ReviewsAdapter;

/**
 * 电影详情页
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetailActivity";

    public static final int REVIEWS_LOADER_ID = 101;

    private static final String FAVORITE = "0";
    private static final String FAVORITE_ED = "1";

    private Loader<Cursor> mLoaderManager;

    private String base_url = "https://image.tmdb.org/t/p/w500";

    private Move move;

    private Toolbar mToolbar;

    private TextView mName;

    private ImageView mCollect;

    private ImageView mBackDrop;

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
        Log.i(TAG, "onCreate()");
        getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);

        Intent intent = getIntent();
        move = (Move) intent.getSerializableExtra("move");
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mName = (TextView) this.findViewById(R.id.name);
        mCollect = (ImageView) this.findViewById(R.id.collect);
        mBackDrop = (ImageView) this.findViewById(R.id.backdrop);
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
            if (FAVORITE.equals(move.getCollected())) {
                mCollect.setImageResource(R.mipmap.favorite);
            } else if (FAVORITE_ED.equals(move.getCollected())) {
                mCollect.setImageResource(R.mipmap.favorite_ed);
            }
        }

        MovieDetailAsyncTask asyncTask = new MovieDetailAsyncTask(this, mHandler);
        asyncTask.execute(MovieDetailAsyncTask.DETAIL_MOVIE, move.getMoveId());

        MovieDetailAsyncTask asyncTask2 = new MovieDetailAsyncTask(this, mHandler);
        asyncTask2.execute(MovieDetailAsyncTask.GET_REVIEWS, move.getMoveId());


        mAdapter = new ReviewsAdapter(this, null, true);
        mListView.setAdapter(mAdapter);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (move != null) {
                    String collect = move.getCollected();

                    ContentValues values = new ContentValues();
                    ContentResolver resolver = getContentResolver();
                    String movieId = move.getMoveId();
                    Log.i(TAG, "moveId == " + move.getMoveId());
                    if (FAVORITE.equals(collect)) {
                        values.clear();
                        values.put(MovieEntry.COLLECTED, FAVORITE_ED);
                        resolver.update(MovieContract.CONTENT_MOVE_URI, values, MovieEntry.MOVIEID + "=?",
                                new String[]{movieId});
                        ((ImageView) v).setImageResource(R.mipmap.favorite_ed);
                    } else {
                        values.clear();
                        values.put(MovieEntry.COLLECTED, FAVORITE);
                        resolver.update(MovieContract.CONTENT_MOVE_URI, values, MovieEntry.MOVIEID + "=?",
                                new String[]{movieId});
                        ((ImageView) v).setImageResource(R.mipmap.favorite);
                    }

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (move != null) {

            mName.setText(move.getTitle());
            String url = base_url + move.getBackDropUrl();
            Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(mBackDrop, new Callback() {
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
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ondestroy..");
    }

    private void inflateData(Cursor cursor) {

        if (cursor.moveToFirst()) {
            mGenre.setText(cursor.getString(DisplayFragment.COL_MOVIE_GENRES));
            mCountry.setText(cursor.getString(DisplayFragment.COL_MOVIE_PRODUCTIONCONTRY));
        }

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

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
