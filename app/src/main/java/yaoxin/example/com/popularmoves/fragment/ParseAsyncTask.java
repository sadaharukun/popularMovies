package yaoxin.example.com.popularmoves.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;
import yaoxin.example.com.popularmoves.fragment.bean.Move;

/**
 * Created by yaoxinxin on 2016/11/25.
 */

public class ParseAsyncTask extends AsyncTask<String, Integer, List<Move>> {

    private static final String Log_D = "ParseAsyncTask";
    //    private static final String APIKEY  = "";
    private static final int RESPONSE_OK = 200;

    private static final String BASE_DETAIL_MOVE = "https://api.themoviedb.org/3/movie/language=zh&movieId?api_key=";


    private Handler mHandler;

    private Context mC;

    public ParseAsyncTask(Handler mHandler, Context c) {
        this.mHandler = mHandler;
        this.mC = c;
    }

    @Override
    protected List<Move> doInBackground(String... params) {

        String base_url = params[0];
        String vote_url = params[1];
        String apikey = params[2];
        boolean refreshFlag = Boolean.parseBoolean(params[3]);
        List<Move> movies = getPopularMovies(base_url, apikey, refreshFlag);
        if (movies != null && movies.size() >= 0) {
            save2database(movies);
        }
        saveVoteMovies(vote_url, apikey, refreshFlag);
        return null;
    }


    @Override
    protected void onPostExecute(List<Move> moves) {
        super.onPostExecute(moves);
        mHandler.obtainMessage(RESPONSE_OK, moves).sendToTarget();
    }


    /**
     * vote movies
     *
     * @param json_url
     * @param apikey
     */
    public void saveVoteMovies(String json_url, String apikey, boolean refreshflag) {

        String path = json_url + apikey;
        ContentResolver resolver = mC.getContentResolver();
        if (!refreshflag) {
            Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, DisplayFragment.CONTRACT_MOVIE_PROJECTIONS,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return;
            }
        }
        InputStreamReader reader = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int code = conn.getResponseCode();
            if (code == RESPONSE_OK) {
                StringBuffer buffer = new StringBuffer();
                char[] buf = new char[1024 * 10];
                reader = new InputStreamReader(conn.getInputStream());
                int len = -1;
                while (-1 != (len = reader.read(buf))) {
                    String s = new String(buf, 0, len);
                    buffer.append(s);
                }

                Log.d(Log_D, buffer.toString());
                JSONObject obj = new JSONObject(buffer.toString());
                JSONArray array = obj.optJSONArray("results");
                List<ContentValues> listValues = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String movieId = object.optString("id");
                    Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, new String[]{MovieEntry.TITLE},
                            MovieEntry.MOVIEID + "=?", new String[]{movieId}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        cursor.close();
                        continue;
                    }
                    ContentValues value = new ContentValues();
                    value.put(MovieEntry.TITLE, object.optString("title"));
                    value.put(MovieEntry.MOVIEID, object.optInt("id"));
                    value.put(MovieEntry.POSTURL, object.optString("poster_path"));
                    value.put(MovieEntry.BACKDROPURL, object.optString("backdrop_path"));
                    value.put(MovieEntry.OVERVIEW, object.optString("overview"));
                    value.put(MovieEntry.VOTEAVERAGE, object.optDouble("vote_average"));
                    value.put(MovieEntry.POPULARITY, object.optDouble("popularity"));
                    value.put(MovieEntry.REALEASEDATE, object.optString("release_date"));
                    value.put(MovieEntry.COLLECTED, "0");
                    JSONArray json_genres = object.optJSONArray("genre_ids");
                    StringBuffer stringBuffer = new StringBuffer();
                    if (json_genres != null) {
                        for (int j = 0; j < json_genres.length(); j++) {
//                                JSONObject genre_json = json_genres.getJSONObject(j);
                            int genre = json_genres.getInt(j);
                            if (j != json_genres.length() - 1) {
                                stringBuffer.append(genre);
                                stringBuffer.append("/");
                            } else {
                                stringBuffer.append(genre);
                            }
                        }
                    }
                    value.put(MovieEntry.GENRES, stringBuffer.toString());
                    listValues.add(value);
//                    resolver.insert(MovieContract.CONTENT_MOVE_URI, value);
                }
                ContentValues[] valuesarr = listValues.toArray(new ContentValues[listValues.size()]);
                int num = resolver.bulkInsert(MovieContract.CONTENT_MOVE_URI, valuesarr);
                Log.i(Log_D, "votearrNum = " + num);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * popular movies
     *
     * @param base_url
     * @param apikey
     * @return
     */
    public List<Move> getPopularMovies(String base_url, String apikey, boolean refreshFlag) {
        String json_url = base_url + apikey;
        ContentResolver resolver = mC.getContentResolver();
        if (!refreshFlag) {
            Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, DisplayFragment.CONTRACT_MOVIE_PROJECTIONS,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return null;
            }
        }
        InputStreamReader reader = null;
        try {
            URL url = new URL(json_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int code = conn.getResponseCode();
            if (code == RESPONSE_OK) {
                StringBuffer buffer = new StringBuffer();
                char[] buf = new char[1024 * 10];
                reader = new InputStreamReader(conn.getInputStream());
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
                        JSONObject object = array.optJSONObject(i);
                        String moveId = String.valueOf(object.optInt("id"));
                        m.setMoveId(moveId);
//                        String detailUrl = BASE_DETAIL_MOVE.replace("movieId", moveId) + apikey;
//                        URL url2 = new URL(detailUrl);
//                        HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
//                        int responseCode = conn2.getResponseCode();
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
                        JSONArray json_genres = object.optJSONArray("genre_ids");
                        StringBuffer stringBuffer = new StringBuffer();
                        if (json_genres != null) {
                            for (int j = 0; j < json_genres.length(); j++) {
//                                JSONObject genre_json = json_genres.getJSONObject(j);
                                int genre = json_genres.getInt(j);
                                if (j != json_genres.length() - 1) {
                                    stringBuffer.append(genre);
                                    stringBuffer.append("/");
                                } else {
                                    stringBuffer.append(genre);
                                }
                            }
                        }
                        m.setGenres(stringBuffer.toString());
                        result.add(m);

                    }
                    Log.d(Log_D, "result.length==" + result.size());

                    /*this is a test about the database*/
//                    save2database(result);


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
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void save2database(List<Move> moves) {
        ContentResolver resolver = this.mC.getContentResolver();
//        Cursor cursor = resolver.query(MovieContract.CONTENT_MOVE_URI, DisplayFragment.CONTRACT_MOVIE_PROJECTIONS, null, null, null);
//        if (cursor.getColumnCount() > 0) {
//            return;
//        }
        resolver.delete(MovieContract.CONTENT_MOVE_URI, null, null);//先删除所有的数据
        if (moves != null && moves.size() != 0) {
            ContentValues[] contentValues = new ContentValues[moves.size()];
            ContentValues value;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
//                value.clear();
                value = new ContentValues();
//                value.put(MovieEntry._ID, i);
                value.put(MovieEntry.TITLE, move.getTitle());
                value.put(MovieEntry.MOVIEID, String.valueOf(move.getMoveId()));
                value.put(MovieEntry.POSTURL, move.getPosterUrl());
                value.put(MovieEntry.BACKDROPURL, move.getBackDropUrl());
                value.put(MovieEntry.OVERVIEW, move.getOverView());
                value.put(MovieEntry.VOTEAVERAGE, move.getVoteAverage());
                value.put(MovieEntry.POPULARITY, move.getPopularity());
                value.put(MovieEntry.REALEASEDATE, move.getReleaseDate());
                value.put(MovieEntry.COLLECTED, "0");
                value.put(MovieEntry.GENRES, move.getGenres());
//                value.put(MovieEntry.COMMENT, "");
                contentValues[i] = value;
//                resolver.insert(MovieContract.CONTENT_MOVE_URI, value);

//                System.out.println("数据插入数据库...");
            }
            int num = resolver.bulkInsert(MovieContract.CONTENT_MOVE_URI, contentValues);
            System.out.println("数据插入数据库..." + num);
        }
    }
}
