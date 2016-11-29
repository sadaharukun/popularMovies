package yaoxin.example.com.popularmoves;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import yaoxin.example.com.popularmoves.fragment.dummy.Move;

public class DetailActivity extends AppCompatActivity {

    private String base_url = "https://image.tmdb.org/t/p/w185";

    private Move move;

    private TextView mName;

    private ImageView mBackDrop;

    private TextView mReleaseTime;

    private RatingBar mAverage;

    private TextView mOverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        move = (Move) intent.getSerializableExtra("move");

        mName = (TextView) this.findViewById(R.id.name);
        mBackDrop = (ImageView) this.findViewById(R.id.backdrop);
        mReleaseTime = (TextView) this.findViewById(R.id.releaseTime);
        mAverage = (RatingBar) this.findViewById(R.id.voteAverage);
        mOverView = (TextView) this.findViewById(R.id.overview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (move != null) {

            mName.setText(move.getTitle());
            String url = base_url + move.getBackDropUrl();
            Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(mBackDrop, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            mReleaseTime.setText(move.getReleaseDate());
            mAverage.setRating((float) move.getVoteAverage()/2.0f);
            System.out.println("voteaverage="+move.getVoteAverage());
            mOverView.setText(move.getOverView());


        }
    }
}
