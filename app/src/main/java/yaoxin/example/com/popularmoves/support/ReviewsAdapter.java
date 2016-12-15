package yaoxin.example.com.popularmoves.support;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yaoxin.example.com.popularmoves.DetailActivity;
import yaoxin.example.com.popularmoves.R;

/**
 * Created by yaoxinxin on 2016/12/14.
 */

public class ReviewsAdapter extends CursorAdapter {


    public ReviewsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_comments, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
//        holder.mAuthor = (TextView) itemView.findViewById(R.id.author);
//        holder.mComments = (TextView) itemView.findViewById(R.id.comments);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (cursor != null) {
            holder.mAuthor.setText(cursor.getString(DetailActivity.COL_REVIEWS_AUTHOR));
            holder.mComments.setText(cursor.getString(DetailActivity.COL_REVIEWS_COMMENTS));
        }

    }

    private static class ViewHolder {

        TextView mAuthor;
        TextView mComments;

        public ViewHolder(View itemView) {
            this.mAuthor = (TextView) itemView.findViewById(R.id.author);
            this.mComments = (TextView) itemView.findViewById(R.id.comments);
        }

    }
}
