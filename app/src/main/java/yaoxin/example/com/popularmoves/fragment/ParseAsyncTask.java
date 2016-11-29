package yaoxin.example.com.popularmoves.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import yaoxin.example.com.popularmoves.fragment.dummy.Move;

/**
 * Created by yaoxinxin on 2016/11/25.
 */

public class ParseAsyncTask extends AsyncTask<String, Integer, List<Move>> {

    private static final String Log_D = "ParseAsyncTask";
    //    private static final String APIKEY  = "";
    private static final int RESPONSE_OK = 200;


    private Handler mHandler;

    private Context c;

    public ParseAsyncTask(Handler mHandler, Context c) {
        this.mHandler = mHandler;
        this.c = c;
    }

    @Override
    protected List<Move> doInBackground(String... params) {
        try {
            String base_url = params[0];
            String apikey = params[1];
            String json_url = base_url + apikey;
            URL url = new URL(json_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int code = conn.getResponseCode();
            if (code == RESPONSE_OK) {
                StringBuffer buffer = new StringBuffer();
                char[] buf = new char[1024 * 10];
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                int len = -1;
                while (-1 != (len = reader.read(buf))) {
                    String s = new String(buf, 0, len);
                    buffer.append(s);
                }

                Log.d(Log_D, buffer.toString());
                JSONObject obj = new JSONObject(buffer.toString());
                JSONArray array = obj.optJSONArray("results");
                if (array != null) {
                    List<Move> result = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        Move m = new Move();
                        JSONObject object = array.getJSONObject(i);
                        m.setId(object.optInt("id"));
                        m.setAdult(object.optBoolean("adult"));
                        m.setBackDropUrl(object.optString("backdrop_path"));
                        m.setOriginTitle(object.optString("original_title"));
                        m.setOverView(object.optString("overview"));
                        m.setPopularity(object.optDouble("popularity"));
                        m.setPosterUrl(object.optString("poster_path"));
                        m.setReleaseDate(object.optString("release_date"));
                        m.setTitle(object.optString("title"));
                        m.setVoteAverage(object.optDouble("vote_average"));
                        m.setVoteCount(object.optInt("vote_count"));
                        result.add(m);
                    }
                    Log.d(Log_D, "result.length==" + result.size());
                    return result;
                }
            } else {
                System.out.print("parse failed.....");
                return null;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(List<Move> moves) {
        super.onPostExecute(moves);
        mHandler.obtainMessage(RESPONSE_OK, moves).sendToTarget();
    }
}
