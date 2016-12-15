package yaoxin.example.com.popularmoves.fragment.support;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
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
 * Created by yaoxinxin on 2016/12/7.
 * <p>
 * 适用于cursorLoader的RecyclerView.Adapter
 */

public class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.ViewHolder> {

    private String base_url = "https://image.tmdb.org/t/p/w500";

    private Context mC;

    private Cursor mCursor;

    private Boolean mDataValid;

    private int mRowIdColumn;

    private DataSetObserver mDataSetObserver;

    private OnClickListener mOnClickListener;

    private DisplayFragment fragment;


    public MovieCursorAdapter(Context c, Cursor cursor, DisplayFragment fragment) {
        boolean cursorPresent = cursor != null;
        this.mC = c;
        this.mCursor = cursor;
        this.mDataSetObserver = new MyDataSetObserver();
        mDataValid = cursorPresent;
        mRowIdColumn = cursorPresent ? cursor.getColumnIndexOrThrow("_id") : -1;
        if (cursorPresent) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        this.fragment = fragment;
    }

    public Cursor getCursor() {
        return this.mCursor;
    }

    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getColumnCount();
        } else {
            return 0;
        }

    }

    public Object getItem(int position) {
        if (mDataValid && mCursor != null) {
            boolean flag = mCursor.moveToPosition(position);
            if (flag) {
                return mCursor;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            mCursor.getLong(mRowIdColumn);//????
        }

        return 0L;
    }

    public void changeCursor(Cursor newCursor) {
        Cursor old = swapCursor(newCursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (this.mCursor == newCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
            mDataValid = true;
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            mRowIdColumn = -1;
            notifyDataSetChanged();
        }

        return oldCursor;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mC).inflate(R.layout.fragment_item, parent, false);
        ViewHolder holder = new ViewHolder(itemView);


        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Cursor cursor = (Cursor) getItem(position);
        if (cursor != null ) {
            String postUrl = cursor.getString(DisplayFragment.COL_MOVIE_POSTURL);
            System.out.println("postUrl*****" + postUrl);
            holder.imageView.setTag(position);
            holder.mTitle.setText(cursor.getString(DisplayFragment.COL_MOVIE_TITLE));
            holder.mVoteaverage.setRating((float) (Double.parseDouble(cursor.getString(DisplayFragment.COL_MOVIE_VOTEAVERAGE)) / 2));
            Picasso.with(mC).load(base_url + postUrl).placeholder(R.mipmap.ic_launcher).into(holder.imageView);
            String collected = cursor.getString(DisplayFragment.COL_MOVIE_COLLECTED);
            if ("1".equals(collected)) {
                holder.collect.setImageResource(R.mipmap.favorite_ed);
            } else {
                holder.collect.setImageDrawable(null);
            }

        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    int pos = (int) v.getTag();
                    Cursor clickCursor = (Cursor) getItem(pos);
                    ((OnClickListener) (fragment)).click(pos, clickCursor);
                }

            }
        });

    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageView collect;
        public TextView mTitle;
        public RatingBar mVoteaverage;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movie_poster);
            collect = (ImageView) itemView.findViewById(R.id.collect);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mVoteaverage = (RatingBar) itemView.findViewById(R.id.voteAverage);
        }
    }

    private class MyDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}
