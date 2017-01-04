package yaoxin.example.com.popularmoves.support;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;

/**
 * Created by yaoxinxin on 2017/1/4.
 */

public class MovieSearchAdapter extends CursorAdapter {

    private static final String BASE_URL = "https://image.tmdb.org/t/p/w500";

    public MovieSearchAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listview_searchmovie, parent, false);
        SearchViewHolder holder = new SearchViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SearchViewHolder holder = (SearchViewHolder) view.getTag();
        String url = cursor.getString(DisplayFragment.COL_MOVIE_BACKDROPURL);
        String title = cursor.getString(DisplayFragment.COL_MOVIE_TITLE);
        double voteaverage = cursor.getDouble(DisplayFragment.COL_MOVIE_VOTEAVERAGE);
        Picasso.with(context).load(BASE_URL + url).placeholder(R.mipmap.movie_default_small).into(holder.mImageView);
        holder.mTitle.setText(title);
        holder.mRatingBar.setRating((float) voteaverage / 2);
        holder.mVote.setText(String.valueOf(voteaverage));

    }

    static class SearchViewHolder {
        ImageView mImageView;
        TextView mTitle;
        RatingBar mRatingBar;
        TextView mVote;

        public SearchViewHolder(View itemView) {
            mImageView = (ImageView) itemView.findViewById(R.id.imageview);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
            mVote = (TextView) itemView.findViewById(R.id.vote);

        }

    }
}
