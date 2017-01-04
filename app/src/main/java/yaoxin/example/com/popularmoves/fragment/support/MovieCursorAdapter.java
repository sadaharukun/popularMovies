package yaoxin.example.com.popularmoves.fragment.support;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;
import yaoxin.example.com.popularmoves.utils.Utils;

/**
 * Created by yaoxinxin on 2016/12/7.
 * <p>
 * 适用于cursorLoader的RecyclerView.Adapter
 */

public class MovieCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MovieCursorAdapter";

    private static final int ITEM_TYPE_NORMAL = 0;
    public static final int ITEM_TYPE_FOOTERVIEW = 1;//before loading  or  load success
    public static final int ITEM_TYPE_FOOTERVIEW_LOADING = 2;//loading
    public static final int ITEM_TYPE_FOOTERVIEW_FAIL = 3;//fail
    private int type;

    private View mFooterView;

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
//            Log.i(TAG, "count = " + mCursor.getColumnCount());
            if (mFooterView != null) {

                if (mCursor.getCount() % 2 == 0) {

                    return mCursor.getCount() + 1;
                } else {
                    return mCursor.getCount() + 2;
                }
            } else {
                return mCursor.getCount();
            }
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


    public void setFooterView(View footerView, int type) {
        if (this.mFooterView == null) {
            this.mFooterView = footerView;
            this.type = type;
            notifyItemInserted(getItemCount() - 1);
        } else {
            this.mFooterView = footerView;
            this.type = type;
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void removeFooterView() {
        if (this.mFooterView != null) {
            this.mFooterView = null;
            notifyItemRemoved(getItemCount() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if (mFooterView != null) {
//            if (position == getItemCount() - 1) {
//                return ITEM_TYPE_FOOTERVIEW;
//            }
//        }
        if (position == getItemCount() - 1) {
            if (mFooterView != null) {
                if (type == ITEM_TYPE_FOOTERVIEW) {
                    return ITEM_TYPE_FOOTERVIEW;
                } else if (type == ITEM_TYPE_FOOTERVIEW_LOADING) {
                    return ITEM_TYPE_FOOTERVIEW_LOADING;
                } else if (type == ITEM_TYPE_FOOTERVIEW_FAIL) {
                    return ITEM_TYPE_FOOTERVIEW_FAIL;
                }
            }
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.i(TAG, "onCreateViewHolder.");
        if (viewType == ITEM_TYPE_NORMAL) {
            View itemView = LayoutInflater.from(mC).inflate(R.layout.fragment_item, parent, false);
            ItemViewHolder holder = new ItemViewHolder(itemView);
            return holder;
        } else if (viewType == ITEM_TYPE_FOOTERVIEW || viewType == ITEM_TYPE_FOOTERVIEW_FAIL) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mFooterView.getLayoutParams();
            params.width = Utils.getScreenWidthAndHeight(mC)[0];
            mFooterView.setLayoutParams(params);
            RecyclerView.ViewHolder footerViewHolder = new FooterViewHolder(mFooterView);
            return footerViewHolder;
        } else if (viewType == ITEM_TYPE_FOOTERVIEW_LOADING) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mFooterView.getLayoutParams();
            params.width = Utils.getScreenWidthAndHeight(mC)[0];
            mFooterView.setLayoutParams(params);
            RecyclerView.ViewHolder footerViewHolder = new FooterViewLoadingHolder(mFooterView);
            return footerViewHolder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Log.i(TAG, "onBindViewHolder..");
        int type = getItemViewType(position);
        if (getItemViewType(position) == ITEM_TYPE_NORMAL) {
            final Cursor cursor = (Cursor) getItem(position);
            if (cursor != null) {
                if (holder instanceof ItemViewHolder) {
                    String postUrl = cursor.getString(DisplayFragment.COL_MOVIE_POSTURL);
                    System.out.println("postUrl*****" + postUrl);
                    ((ItemViewHolder) holder).imageView.setTag(position);
                    ((ItemViewHolder) holder).mTitle.setText(cursor.getString(DisplayFragment.COL_MOVIE_TITLE));
                    ((ItemViewHolder) holder).mVoteaverage.setRating((float) (Double.parseDouble(cursor.getString(DisplayFragment.COL_MOVIE_VOTEAVERAGE)) / 2));
                    Picasso.with(mC).load(base_url + postUrl).placeholder(R.mipmap.movie_default).into(((ItemViewHolder) holder).imageView);
                    String collected = cursor.getString(DisplayFragment.COL_MOVIE_COLLECTED);
                    if ("1".equals(collected)) {
                        ((ItemViewHolder) holder).collect.setImageResource(R.mipmap.favorite_ed);
                    } else {
                        ((ItemViewHolder) holder).collect.setImageDrawable(null);
                    }

                    ((ItemViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
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
            }
        } else if (type == ITEM_TYPE_FOOTERVIEW || type == ITEM_TYPE_FOOTERVIEW_FAIL) {
            //
            if (holder instanceof FooterViewHolder) {
                ((FooterViewHolder) holder).mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "loadMore click..");
                        if (fragment != null) {
//                            ((FooterViewHolder) holder).mProgressBar.setVisibility(View.VISIBLE);
//                            ((FooterViewHolder) holder).mTextView.setVisibility(View.INVISIBLE);

                            String way = Utils.getSortway(mC);
                            int page = 1;
                            int flag = 0;
                            Log.i(TAG, "sortway==" + way);
                            if (Utils.POPULARWAY.equals(way)) {
                                page = Integer.parseInt(Utils.getString(mC, Utils.POPULARCURRENTPAGE, "1"));
                                flag = 0;
                            } else if (Utils.VOTEAVERAGEWAY.equals(way)) {
                                page = Integer.parseInt(Utils.getString(mC, Utils.VOTECURRENTPAGE, "1"));
                                flag = 1;
                            }
                            Log.i(TAG, "page==" + page);
                            fragment.loadMore(page, flag);
                        }

                    }
                });
            }
        } else if (type == ITEM_TYPE_FOOTERVIEW_LOADING) {
            ((FooterViewLoadingHolder) holder).mContent.setClickable(false);
        }


    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageView collect;
        public TextView mTitle;
        public RatingBar mVoteaverage;


        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movie_poster);
            collect = (ImageView) itemView.findViewById(R.id.collect);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mVoteaverage = (RatingBar) itemView.findViewById(R.id.voteAverage);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mContent;
        private TextView mTextView;
//        private ProgressBar mProgressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mContent = (LinearLayout) itemView.findViewById(R.id.content);
            mTextView = (TextView) itemView.findViewById(R.id.textview);
//            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
        }
    }

    public static class FooterViewLoadingHolder extends RecyclerView.ViewHolder {
        public LinearLayout mContent;
        public ProgressBar mProgressBar;

        public FooterViewLoadingHolder(View itemView) {
            super(itemView);
            mContent = (LinearLayout) itemView.findViewById(R.id.content);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
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
