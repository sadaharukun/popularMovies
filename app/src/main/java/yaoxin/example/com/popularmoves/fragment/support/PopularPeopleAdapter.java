package yaoxin.example.com.popularmoves.fragment.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.fragment.bean.PopularPeople;

/**
 * Created by yaoxinxin on 2017/2/9.
 */

public class PopularPeopleAdapter<T> extends RecyclerView.Adapter<PopularPeopleAdapter.PpViewHolder> {

    public List<T> data;

    public Context context;

    private static final String BASEURL = "https://image.tmdb.org/t/p/w185";

    public PopularPeopleAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public PpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_popularpeople, parent, false);
        PpViewHolder holder = new PpViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PopularPeopleAdapter.PpViewHolder holder, final int position) {

        final PopularPeople people = (PopularPeople) data.get(position);
        holder.nameView.setText(people.name);
        Picasso.with(context).load(BASEURL + people.profile_path).placeholder(R.mipmap.default_user)
                .into(holder.faceView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.click(v, people);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class PpViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView faceView;
        private TextView nameView;

        public PpViewHolder(View itemView) {
            super(itemView);
            this.faceView = (CircleImageView) itemView.findViewById(R.id.face);
            this.nameView = (TextView) itemView.findViewById(R.id.name);
        }
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public interface OnClickPopularPeopleListener {
        void click(View v, PopularPeople people);
    }

    OnClickPopularPeopleListener listener;

    public void setOnClickPopularPeopleListener(OnClickPopularPeopleListener listener) {
        this.listener = listener;
    }
}
