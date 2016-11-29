package yaoxin.example.com.popularmoves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 排序方式
 */
public class SettingActivity extends AppCompatActivity {


    private static final int SORT_POPULAR = 301;
    private static final int SORT_VOTE = 302;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ListView listview = (ListView) this.findViewById(R.id.list_setting);
        String[] settings = new String[]{getString(R.string.popularMost), getString(R.string.voteMost)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_setting_item, settings);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//最受欢迎
                    Intent intent = new Intent();
                    SettingActivity.this.setResult(SORT_POPULAR, intent);
                    finish();

                } else if (position == 1) {//评分最高
                    Intent intent = new Intent();
                    SettingActivity.this.setResult(SORT_VOTE, intent);
                    finish();
                } else {

                }
            }
        });


    }
}
