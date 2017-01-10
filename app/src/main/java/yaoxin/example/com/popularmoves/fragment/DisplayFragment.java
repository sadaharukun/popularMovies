package yaoxin.example.com.popularmoves.fragment;


import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import yaoxin.example.com.popularmoves.data.MovieTypeEntry;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.fragment.support.MovieCursorAdapter;
import yaoxin.example.com.popularmoves.fragment.support.OnClickListener;
import yaoxin.example.com.popularmoves.fragment.support.OnLoadMoreListener;
import yaoxin.example.com.popularmoves.sync.MovieSyncAdapter;
import yaoxin.example.com.popularmoves.utils.SyncUtils;
import yaoxin.example.com.popularmoves.utils.Utils;

import static yaoxin.example.com.popularmoves.sync.MovieSyncAdapter.LOADFINISHACTION;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        OnClickListener, OnLoadMoreListener {

    private static final String TAG = "DisplayFragment";

    private static final int REQUESTCODE_SETTINGACTIVITY = 10;
    private static final int REQUESTCODE_DETAILACTIVITY = 11;
    private static final int RESULTCODE_SETTINGACTIVITY_SORTPOPULAR = 301;
    private static final int RESULTCODE_SETTINGACTIVITY_SORTVOTEAVERAGE = 302;
    public static final int RESULTCODE_DETAILACTIVITY_COLLECTCHANGE = 303;
    public static final int RESULTCODE_DETAILACTIVITY_COLLECT_NOTCHANGE = 304;

    private static final int LoderId = 100;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mPosition = ListView.INVALID_POSITION;

    public static final String[] CONTRACT_MOVIE_PROJECTIONS = new String[]{
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.TITLE,
            MovieEntry.MOVIEID,
            MovieEntry.POSTURL,
            MovieEntry.BACKDROPURL,
            MovieEntry.OVERVIEW,
            MovieEntry.TABLE_NAME + "." + MovieEntry.VOTEAVERAGE,
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

    private int mCurrentPage = 1;

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
        setFooterView(mRecyclerView);
//        ParseAsyncTask task = new ParseAsyncTask(handler, getActivity());
//        task.execute(base_url, ((MainActivity) getActivity()).voteUrl, apikey, String.valueOf(false));

        return view;
    }

    private BroadcastReceiver loadFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LOADFINISHACTION.equals(intent.getAction())) {
                getLoaderManager().restartLoader(LoderId, null, DisplayFragment.this);
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
                int page = intent.getIntExtra("currentPage", 1);
                if (Utils.POPULARWAY.equals(Utils.getSortway(getActivity()))) {
                    Utils.setString(getActivity(), Utils.POPULARCURRENTPAGE, page + "");
                } else {
                    Utils.setString(getActivity(), Utils.VOTECURRENTPAGE, page + "");
                }

                if (mCursorAdapter != null) {
                    mCursorAdapter.setFooterView(Utils.inflateView(getActivity(), R.layout.item_footview_loadmore, mRecyclerView),
                            MovieCursorAdapter.ITEM_TYPE_FOOTERVIEW);
                }

                boolean success = intent.getBooleanExtra("loadSuccess", true);
                if (!success) {//load fail show item_footview_loadfail view
                    if (mCursorAdapter != null) {
                        mCursorAdapter.setFooterView(Utils.inflateView(getActivity(), R.layout.item_footview_loadfailed, mRecyclerView),
                                MovieCursorAdapter.ITEM_TYPE_FOOTERVIEW_FAIL);
                    }
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(LOADFINISHACTION);
        getActivity().registerReceiver(loadFinishReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loadFinishReceiver != null) {
            getActivity().unregisterReceiver(loadFinishReceiver);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.itemfragment, menu);
        searchView(menu);//搜索按钮
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void searchView(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        searchView.setBackgroundColor(getActivity().getColor(R.color.gray_C));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "query.." + query);
                searchView.setIconified(true);
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                searchView.setIconified(true);
//                return true;
//            }
//        });
    }

    /*dialog searchview*/
    private void searchView(MenuItem item) {
        getActivity().onSearchRequested();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {

            case R.id.menu_search:
                Log.i(TAG, "search..");
                //you can use search dialog here
//                searchView(item);
                return true;
            case R.id.menu_refresh://刷新
//                ParseAsyncTask task = new ParseAsyncTask(handler, getActivity());
//                task.execute(base_url, ((MainActivity) getActivity()).voteUrl, apikey, String.valueOf(true));
                return true;
            case R.id.menu_setting://设置
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, REQUESTCODE_SETTINGACTIVITY);
                return true;
            case R.id.menu_collect://收藏
                boolean flag = Utils.IsMovieCollected(getActivity());
                ContentResolver resolver = getActivity().getContentResolver();
                if (!flag) {
                    Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS,
                            MovieEntry.COLLECTED + "=?", new String[]{MovieEntry.MOVIE_COLLECTED}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        mCursorAdapter.changeCursor(cursor);

                    } else {
                        mCursorAdapter.changeCursor(null);
                    }
                    mCursorAdapter.removeFooterView();
                    item.setTitle("全部");
                    Utils.setMovieCollected(getActivity(), true);

                    return true;
                } else {
                    String way = Utils.getSortway(this.getActivity());
                    if (Utils.POPULARWAY.equals(way)) {//popular
                        Cursor cursor = resolver.query(MovieContract.CONTENT_MOVIE_MOVIEYTPE_URI, CONTRACT_MOVIE_PROJECTIONS,
                                MovieEntry.TABLE_NAME + "." + MovieEntry.MOVIEID + "=" + MovieTypeEntry.TABLENAME +
                                        "." + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.TABLENAME + "."
                                        + MovieTypeEntry.POPULAR + "=1",
                                null, MovieEntry.POPULARITY_SORT_ORDER);
                        if (cursor != null && cursor.moveToFirst()) {
                            mCursorAdapter.changeCursor(cursor);
                        }
//                        cursor.close();
                    } else if (Utils.VOTEAVERAGEWAY.equals(way)) {//voterage
                        Cursor cursor = resolver.query(MovieContract.CONTENT_MOVIE_MOVIEYTPE_URI, CONTRACT_MOVIE_PROJECTIONS,
                                MovieEntry.TABLE_NAME + "." + MovieEntry.MOVIEID + "=" + MovieTypeEntry.TABLENAME
                                        + "." + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.TABLENAME + "."
                                        + MovieTypeEntry.VOTEAVERAGE + "=1", null, MovieEntry.VOTEAVERAGE_SORT_ORDER);
                        if (cursor != null && cursor.moveToFirst()) {
                            mCursorAdapter.changeCursor(cursor);
                        }
//                        cursor.close();
                    }
                    item.setTitle(getString(R.string.menu_collected));
                    if (mRecyclerView != null) {
                        setFooterView(mRecyclerView);
                    }
                    Utils.setMovieCollected(getActivity(), false);
                    return true;
                }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "resultcode==" + resultCode);
        if (resultCode == RESULTCODE_SETTINGACTIVITY_SORTPOPULAR) {//popular
            ContentResolver resolver = getActivity().getContentResolver();
            String sql = "SELECT * FROM " + MovieEntry.TABLE_NAME + "," + MovieTypeEntry.TABLENAME + " WHERE " + MovieEntry.MOVIEID
                    + "=" + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.POPULAR + "=1";
            Cursor cursor = resolver.query(MovieContract.CONTENT_MOVIE_MOVIEYTPE_URI, CONTRACT_MOVIE_PROJECTIONS,
                    MovieEntry.TABLE_NAME + "." + MovieEntry.MOVIEID + "=" + MovieTypeEntry.TABLENAME +
                            "." + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.TABLENAME + "."
                            + MovieTypeEntry.POPULAR + "=1", null, MovieEntry.POPULARITY_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                mCursorAdapter.changeCursor(cursor);
            } else {
                MovieSyncAdapter.syncImmediately(getActivity(), 0, 1);
            }
        } else if (resultCode == RESULTCODE_SETTINGACTIVITY_SORTVOTEAVERAGE) {//voteaverage
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(MovieContract.CONTENT_MOVIE_MOVIEYTPE_URI, CONTRACT_MOVIE_PROJECTIONS,
                    MovieEntry.TABLE_NAME + "." + MovieEntry.MOVIEID + "=" + MovieTypeEntry.TABLENAME + "." + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.TABLENAME + "." + MovieTypeEntry.VOTEAVERAGE + "=1", null, MovieEntry.VOTEAVERAGE_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                mCursorAdapter.changeCursor(cursor);
            } else {
                MovieSyncAdapter.syncImmediately(getActivity(), 1, 1);
            }
        } else if (resultCode == RESULTCODE_DETAILACTIVITY_COLLECTCHANGE) {//movie collect change
            Log.i(TAG, "collect change..");
            if (!Utils.IsMovieCollected(getActivity())) {
                getLoaderManager().restartLoader(LoderId, null, this);
            } else {
                ContentResolver resolver = getActivity().getContentResolver();
                Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS,
                        MovieEntry.COLLECTED + "=?", new String[]{MovieEntry.MOVIE_COLLECTED}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mCursorAdapter.changeCursor(cursor);
                } else {
                    mCursorAdapter.changeCursor(null);
                }
            }

        } else if (resultCode == RESULTCODE_DETAILACTIVITY_COLLECT_NOTCHANGE) {
            //do nothing
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i(TAG, "onCreateLoader");
        String selection = null;
        String[] selectionArgs = null;
        String way = Utils.getSortway(getActivity());
        String sortOrder = MovieEntry.POPULARITY_SORT_ORDER;
        if (Utils.POPULARWAY.equals(way)) {//popular
            sortOrder = MovieEntry.POPULARITY_SORT_ORDER;
            return new CursorLoader(this.getContext(), MovieContract.CONTENT_MOVIE_MOVIEYTPE_URI, CONTRACT_MOVIE_PROJECTIONS,
                    MovieEntry.TABLE_NAME + "." + MovieEntry.MOVIEID + "=" + MovieTypeEntry.TABLENAME +
                            "." + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.TABLENAME + "."
                            + MovieTypeEntry.POPULAR + "=1", null, sortOrder);
//            return new CursorLoader(this.getContext(), MovieContract.CONTENT_MOVE_URI, CONTRACT_MOVIE_PROJECTIONS, null, null, sortOrder);
        } else if (Utils.VOTEAVERAGEWAY.equals(way)) {//voteaverage
            sortOrder = MovieEntry.VOTEAVERAGE_SORT_ORDER;
            return new CursorLoader(this.getContext(), MovieContract.CONTENT_MOVIE_MOVIEYTPE_URI, CONTRACT_MOVIE_PROJECTIONS,
                    MovieEntry.TABLE_NAME + "." + MovieEntry.MOVIEID + "=" + MovieTypeEntry.TABLENAME +
                            "." + MovieTypeEntry.MOVIEID + " AND " + MovieTypeEntry.TABLENAME + "." +
                            MovieTypeEntry.VOTEAVERAGE + "=1", null, sortOrder);
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
        Log.i(TAG, "onLoadFinished");
        mCursorAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
//        if (mCursorAdapter != null) {
//            mCursorAdapter.setFooterView(Utils.inflateView(getActivity(), R.layout.item_footview_loadmore, mRecyclerView));
//        }

        System.out.println("data==null? " + data == null ? "yes" : "no");
        if (data != null && data.moveToFirst()) {
            Log.i(TAG, "rawCount==" + data.getCount());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void setFooterView(RecyclerView view) {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_footview_loadmore, view, false);

        mCursorAdapter.setFooterView(footerView, MovieCursorAdapter.ITEM_TYPE_FOOTERVIEW);
    }

    /**
     * 点击进入详情页
     *
     * @param position
     * @param cursor
     */
    @Override
    public void click(int position, Cursor cursor) {
        Log.i(TAG, "onClick");
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
        Log.i(TAG, "moveCursor::" + cursor.toString());
        intent.putExtra("move", move);
//        startActivity(intent);
        startActivityForResult(intent, REQUESTCODE_DETAILACTIVITY);
    }

    /**
     * 加载更多
     *
     * @param page
     * @param flag 流行(0)  评分(1)
     */
    @Override
    public void loadMore(int page, int flag) {
        Log.i(TAG, "page=" + page);
        mCurrentPage = page;
        if (mCursorAdapter != null) {
            View view = Utils.inflateView(getActivity(), R.layout.item_footview_loading, mRecyclerView);
            view.findViewById(R.id.content).setClickable(false);
            mCursorAdapter.setFooterView(view, MovieCursorAdapter.ITEM_TYPE_FOOTERVIEW_LOADING);
        }
        SyncUtils.syncImmediately(getActivity(), flag, page);
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
