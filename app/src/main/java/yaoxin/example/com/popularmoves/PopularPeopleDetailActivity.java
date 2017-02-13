package yaoxin.example.com.popularmoves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import yaoxin.example.com.popularmoves.fragment.bean.PopularPeople;
import yaoxin.example.com.popularmoves.utils.Utils;

public class PopularPeopleDetailActivity extends AppCompatActivity {


    private static final String TAG = "PopularPeopleDetail";

    private static final String BASEIMGURL = "https://image.tmdb.org/t/p/w500";

    private Toolbar mToolbar;
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_people_detail);
        Utils.hideNavigationBar(this);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        imageview = (ImageView) this.findViewById(R.id.people_poster);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.mipmap.back);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        PopularPeople people = intent.getParcelableExtra("popularpeople");
        Log.i(TAG, "name==" + people.name);


        Picasso.with(this).load(BASEIMGURL + people.profile_path).into(imageview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popularpeople_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movie_home:
                return true;
            case R.id.viewonIMDb:
                return true;
            case R.id.viewonwikipedia:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class PeopleInfoRunnable implements Runnable {

        private String id;
        private String baseurl = "https://api.themoviedb.org/3/person/personId?language=en-US";

        public PeopleInfoRunnable(String id) {
            this.id = id;
        }

        @Override
        public void run() {

            String path = baseurl.replace("personId", id) + "&" + "api_key=" + MovieApplication.APIKEY;

        }
    }
}
