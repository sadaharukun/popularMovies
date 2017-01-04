package yaoxin.example.com.popularmoves;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;
import yaoxin.example.com.popularmoves.fragment.bean.Move;
import yaoxin.example.com.popularmoves.support.MovieSearchAdapter;

import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_BACKDROPURL;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_COLLECTED;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_GENRES;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_ID;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_MOVIEID;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_POPULARITY;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_POSTURL;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_REALEASEDATE;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_TITLE;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_VOERVIEW;
import static yaoxin.example.com.popularmoves.fragment.DisplayFragment.COL_MOVIE_VOTEAVERAGE;

public class SearchableActivity extends AppCompatActivity {
    private static final String TAG = SearchableActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private ListView mListView;
    private MovieSearchAdapter movieCursorAdapter;

    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Intent intent = getIntent();

        mListView = (ListView) this.findViewById(R.id.listView);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "navigation click..");
                finish();
            }
        });

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(mQuery)) {
                doMySearch(mQuery.trim());
            }
        }
        mToolbar.setTitle(mQuery);
//        setSupportActionBar(mToolbar);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchableActivity.this, DetailActivity.class);
                Cursor cursor = (Cursor) movieCursorAdapter.getItem(position);
                Move move = cursor2move(cursor);
                intent.putExtra("move", move);
                startActivity(intent);
            }
        });
    }


    private void doMySearch(String query) {
        Log.i(TAG, "query==" + query);
        String sql = "select * from table where title like %query%";
        Cursor cursor = getContentResolver().query(MovieContract.CONTENT_MOVE_URI, DisplayFragment.CONTRACT_MOVIE_PROJECTIONS,
                MovieEntry.TITLE + " LIKE '%" + query + "%'", null, null);
        movieCursorAdapter = new MovieSearchAdapter(this, cursor, true);
        mListView.setAdapter(movieCursorAdapter);
    }

    private Move cursor2move(Cursor cursor) {
        if (cursor != null) {
            Move move = new Move();
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
            return move;
        }
        return null;
    }

}
