package yaoxin.example.com.popularmoves;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import yaoxin.example.com.popularmoves.utils.Utils;

/**
 * 排序方式
 */
public class SettingActivity extends AppCompatActivity {

    private static final int SORT_POPULAR = 301;
    private static final int SORT_VOTE = 302;

    private Toolbar mToolbar;
    private ListView listview;
    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        listview = (ListView) this.findViewById(R.id.list_setting);
        String[] settings = new String[]{getString(R.string.popularMost), getString(R.string.voteMost)};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_setting_item, settings);
        mAdapter = new SortAdapter(this, Arrays.asList(settings));

        listview.setAdapter(mAdapter);
        mToolbar.setTitle(getString(R.string.setting));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
//        mToolbar.setNavigationIcon(null);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//最受欢迎
                    Intent intent = new Intent();
                    SettingActivity.this.setResult(SORT_POPULAR, intent);
                    Utils.getInstance().setSortway(SettingActivity.this, Utils.POPULARWAY);
                    finish();

                } else if (position == 1) {//评分最高
                    Intent intent = new Intent();
                    SettingActivity.this.setResult(SORT_VOTE, intent);
                    Utils.getInstance().setSortway(SettingActivity.this, Utils.VOTEAVERAGEWAY);
                    finish();
                } else {

                }
            }
        });


    }


    private static class SortAdapter extends BaseAdapter {

        Context c;
        List<String> mData;

        public SortAdapter(Context context, List<String> data) {
            this.c = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(c).inflate(R.layout.item_setting_sort, parent, false);
                holder.mTextView = (TextView) convertView.findViewById(R.id.text);
                holder.mImageView = (ImageView) convertView.findViewById(R.id.chose);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String way = mData.get(position);
            holder.mTextView.setText(way);
            int pos = Integer.parseInt(Utils.getInstance().getSortway(c).split("_")[1]);
            holder.mImageView.setImageDrawable(null);
            if (position == pos) {
                holder.mImageView.setImageResource(R.mipmap.chose);
            }
            return convertView;
        }

        class ViewHolder {
            TextView mTextView;
            ImageView mImageView;
        }
    }
}
