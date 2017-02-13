package yaoxin.example.com.popularmoves.fragment.support;

import android.os.Handler;
import android.os.Message;
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

import yaoxin.example.com.popularmoves.MovieApplication;
import yaoxin.example.com.popularmoves.fragment.bean.PopularPeople;

/**
 * Created by yaoxinxin on 2017/2/9.
 */

public class PopluarPeopleRunnable implements Runnable {

    private static final String TAG = "PopluarPeopleRunnable";
    public static final int TASK_POPULARPEOPLE = 400;
    private static final int RESULT_OK = 200;
    String baseUrl;
    int page = 1;
    String uri;
    Handler handler;

    public PopluarPeopleRunnable(String baseUrl, int page, Handler handler) {
        this.baseUrl = baseUrl;
        this.page = page;
        this.uri = baseUrl + "&" + "api_key=" + MovieApplication.APIKEY + "&" + "page=" + page;
        this.handler = handler;
    }

    @Override
    public void run() {
        HttpURLConnection conn;
        InputStreamReader reader;
        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int responsecode = conn.getResponseCode();
            if (responsecode == RESULT_OK) {
                StringBuffer buffer = new StringBuffer();
                reader = new InputStreamReader(conn.getInputStream());
                int len = -1;
                char[] buf = new char[1024];
                while (-1 != (len = reader.read(buf))) {
                    buffer.append(buf, 0, len);
                }
                String result_string = buffer.toString();
                Log.i(TAG, result_string);
                JSONObject result_json = new JSONObject(result_string);
                int page = result_json.getInt("page");
                JSONArray array = result_json.optJSONArray("results");
                List<PopularPeople> peoples = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    PopularPeople people = new PopularPeople();
                    JSONObject per_object = array.getJSONObject(i);
                    people.adult = per_object.getBoolean("adult");
                    people.id = per_object.getInt("id");
                    people.name = per_object.getString("name");
                    people.popularity = (float) per_object.getDouble("popularity");
                    people.profile_path = per_object.getString("profile_path");
                    peoples.add(people);
                }

                Message msg = handler.obtainMessage(TASK_POPULARPEOPLE);
                msg.obj = peoples;
                msg.sendToTarget();

            }else{
                handler.obtainMessage(TASK_POPULARPEOPLE).sendToTarget();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
