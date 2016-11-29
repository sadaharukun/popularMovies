package yaoxin.example.com.popularmoves.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.fragment.ItemFragment.OnListFragmentInteractionListener;
import yaoxin.example.com.popularmoves.fragment.dummy.Move;

/**
 * {@link RecyclerView.Adapter} that can display a {@link yaoxin.example.com.popularmoves.fragment.dummy.Move} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private String base_url = "https://image.tmdb.org/t/p/w185";

    private  List<Move> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mC;

    public MyItemRecyclerViewAdapter(Context c,List<Move> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mC = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String posturl = base_url + holder.mItem.getPosterUrl();
        Picasso.with(mC).load(posturl).placeholder(R.mipmap.ic_launcher).into(holder.mPoster, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Log.d("ItemRecyclerViewAdapter","download failed");

            }
        });


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onLitFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues== null?0:mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView mPoster;
        public Move mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPoster = (ImageView) view.findViewById(R.id.move_poster);

        }

        @Override
        public String toString() {
            return super.toString() ;
        }
    }


    public List<Move> getmValues() {
        return mValues;
    }

    public void setmValues(List<Move> values){
        this.mValues = values;
    }
}
