package yaoxin.example.com.popularmoves.fragment;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yaoxin.example.com.popularmoves.DetailActivity;
import yaoxin.example.com.popularmoves.MainActivity;
import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.SettingActivity;
import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.fragment.support.MovieCursorAdapter;
import yaoxin.example.com.popularmoves.fragment.support.OnClickListener;
import yaoxin.example.com.popularmoves.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        OnClickListener {

    private static final String DISPLAYFRAGMENT = "DisplayFragment";

    private static final int REQUESTCODE_SETTINGACTIVITY = 10;
    private static final int RESULTCODE_SETTINGACTIVITY_SORTPOPULAR = 301;
    private static final int RESULTCODE_SETTINGACTIVITY_SORTVOTEAVERAGE = 302;

    private static final int LoderId = 100;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mPosition = ListView.INVALID_POSITION;

    public static final String[] CONTRACT_MOVIE_PROJECTIONS = new String[]{
            /*MovieEntry.TABLE_NAME + "." +*/ MovieEntry._ID,
            MovieEntry.TITLE,
            MovieEntry.MOVIEID,
            MovieEntry.POSTURL,
            MovieEntry.BACKDROPURL,
            MovieEntry.OVERVIEW,
            MovieEntry.VOTEAVERAGE,
            MovieEntry.REALEASEDATE,
            MovieEntry.RUNTIME,
            MovieEntry.POPULARITY,
            MovieEntry.COLLECTED,
            MovieEntry.GENRES,
            MovieEntry.PRODUCTIONS_COUNTRY

    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_MOVIEID = 2;
    public static final int COL_MOVIE_POSTURL = 3;
    public static final int COL_MOVIE_BACKDROPURL = 4;
    public static final int COL_MOVIE_VOERVIEW = 5;
    public static final int COL_MOVIE_VOTEAVERAGE = 6;
    public static final int COL_MOVIE_REALEASEDATE = 7;
    public static final int COL_MOVIE_RUNTIME = 8;
    public static final int COL_MOVIE_POPULARITY = 9;
    public static final int COL_MOVIE_COLLECTED = 10;
    public static final int COL_MOVIE_GENRES = 11;
    public static final int COL_MOVIE_PRODUCTIONCONTRY = 12;


    private RecyclerView mRecyclerView;
    private TextView mPleaseOnline;
    private ProgressBar mProgressBar;
    MovieCursorAdapter mCursorAdapter;

    private String base_url;
    private String apikey;
    private String currentBaseUrl = "";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressBar.setVisibility(View.GONE);
            if (msg.what == ParseAsyncTask.RESPONSE_WRONG) {
                Utils.getInstance().showToast(getActivity(), "Refresh Fail..");
            }
        }
    };

    public DisplayFragment() {
        // Required empty public constructor
    }

    public static DisplayFragment newInstance(String url, String apikey, int columnCount) {
        DisplayFragment fragment = new DisplayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString("base_url", url);
        args.putString("apikey", apikey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        setHasOptionsMenu(true);
        if (bundle != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            base_url = getArguments().getString("base_url");
            apikey = getArguments().getString("apikey");
            this.currentBaseUrl = base_url;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initLoader
        getLoaderManager().initLoader(LoderId, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mPleaseOnline = (TextView) view.findViewById(R.id.pleaseOnline);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        if (((MainActivity) getActivity()).isOnline()) {
            mPleaseOnline.setVisibility(View.GONE);
        } else {
            mPleaseOnline.setVisibility(View.VISIBLE);
        }

        if (mColumnCount > 1) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getActivity()));
        mCursorAdapter = new MovieCursorAdapter(getActivity(), null, this);
        mRecyclerView.setAdapter(mCursorAdapter);

        ParseAsyncTask task = new ParseAsyncTask(handler, getActivity());
        task.execute(base_url, ((MainActivity) getActivity()).voteUrl, apikey, String.valueOf(false));

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.itemfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_refresh://刷新
                ParseAsyncTask task = new ParseAsyncTask(handler, getActivity());
                task.execute(base_url, ((MainActivity) getActivity()).voteUrl, apikey, String.valueOf(true));
                return true;
            case R.id.menu_setting://设置
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, REQUESTCODE_SETTINGACTIVITY);
                return true;
            case R.id.menu_collect://收藏
                Utils utils = Utils.getInstance();
                boolean flag = utils.IsMovieCollected(getActivity());
                ContentResolver resolver = getActivity().getContentResolver();
                if (!flag) {
                    Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS,
                            MovieEntry.COLLECTED + "=?", new String[]{MovieEntry.MOVIE_COLLECTED}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        mCursorAdapter.changeCursor(cursor);
                    } else {
                        mCursorAdapter.changeCursor(null);
                    }
                    item.setTitle("全部");
                    utils.setMovieCollected(getActivity(), true);
                    return true;
                } else {
                    Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS, null,
                            null, MovieEntry.POPULARITY_SORT_ORDER);
                    if (cursor != null && cursor.moveToFirst()) {
                        mCursorAdapter.changeCursor(cursor);
                    }
                    item.setTitle(getString(R.string.menu_collected));
                    utils.setMovieCollected(getActivity(), false);
                }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULTCODE_SETTINGACTIVITY_SORTPOPULAR) {//popular
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS,
                    null, null, MovieEntry.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                mCursorAdapter.changeCursor(cursor);
            }
        } else if (resultCode == RESULTCODE_SETTINGACTIVITY_SORTVOTEAVERAGE) {//voteaverage
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS,
                    null, null, MovieEntry.VOTEAVERAGE_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                mCursorAdapter.changeCursor(cursor);
            }


        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = null;
        String[] selectionArgs = null;
        String way = Utils.getInstance().getSortway(getActivity()).split("_")[1];
        String sortOrder = MovieEntry.POPULARITY_SORT_ORDER;
        if ("0".equals(way)) {
            sortOrder = MovieEntry.POPULARITY_SORT_ORDER;
        } else if ("1".equals(way)) {
            sortOrder = MovieEntry.VOTEAVERAGE_SORT_ORDER;
        }
        return new CursorLoader(this.getContext(), MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS,
                selection, selectionArgs, sortOrder);
    }

    /**
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    /**
     * 点击进入详情页
     *
     * @param position
     * @param cursor
     */
    @Override
    public void click(int position, Cursor cursor) {
        Log.i(DISPLAYFRAGMENT, "onClick");
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Move move = new Move();
        if (cursor != null) {
            move.setId(cursor.getInt(COL_MOVIE_ID));
            move.setTitle(cursor.getString(COL_MOVIE_TITLE));
            move.setMoveId(cursor.getString(COL_MOVIE_MOVIEID));
            move.setPosterUrl(cursor.getString(COL_MOVIE_POSTURL));
            move.setBackDropUrl(cursor.getString(COL_MOVIE_BACKDROPURL));
            move.setOverView(cursor.getString(COL_MOVIE_VOERVIEW));
            move.setVoteAverage(Double.parseDouble(cursor.getString(COL_MOVIE_VOTEAVERAGE)));
            move.setReleaseDate(cursor.getString(COL_MOVIE_REALEASEDATE));
            move.setComment(cursor.getString(COL_MOVIE_POPULARITY));
            move.setCollected(cursor.getString(COL_MOVIE_COLLECTED));
            move.setGenres(cursor.getString(COL_MOVIE_GENRES));
        }
//        cursor.close();
        Log.i(DISPLAYFRAGMENT, "moveCursor::" + cursor.toString());
        intent.putExtra("move", move);
        startActivity(intent);
    }

    /**
     * itemDecoration for recyclerView
     */
    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        private int color = Color.parseColor("#FFF");

        private Drawable mDivider;


        public SpaceItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);

            a.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            drawVertical(c, parent);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, 0);

        }


        private void drawVertical(Canvas c, RecyclerView parent) {

            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) parent.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }


        }

    }
}
