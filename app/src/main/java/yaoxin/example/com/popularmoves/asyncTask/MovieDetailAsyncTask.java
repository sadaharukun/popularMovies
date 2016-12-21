package yaoxin.example.com.popularmoves.asyncTask;

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

import yaoxin.example.com.popularmoves.DetailActivity;
import yaoxin.example.com.popularmoves.MovieApplication;
import yaoxin.example.com.popularmoves.data.MovieContract;
import yaoxin.example.com.popularmoves.data.MovieEntry;
import yaoxin.example.com.popularmoves.data.ReviewsEntry;
import yaoxin.example.com.popularmoves.data.VideosEntry;
import yaoxin.example.com.popularmoves.fragment.DisplayFragment;

/**
 * Created by yaoxinxin on 2016/12/13.
 */

public class MovieDetailAsyncTask extends AsyncTask<Object, Object, Object> {

    private static final String TAG = "MovieDetailActivity";
    private static final String base_url = "https://api.themoviedb.org/3/movie/movieId?api_key=";
    private static final String reviews_url = "https://api.themoviedb.org/3/movie/moveId/reviews?api_key=";
    private static final String vidos_url = "http://api.themoviedb.org/3/movie/movieId/videos?api_key=";
    private static final int RESULT_OK = 200;
    public static final int DETAIL_MOVIE = 1000;
    public static final int GET_REVIEWS = 1001;
    public static final int GET_VIDEOS = 1002;

    private Context mC;
    private Handler mHandler;

    public MovieDetailAsyncTask(Context c, Handler handler) {
        this.mC = c;
        this.mHandler = handler;
    }

    @Override
    protected Object doInBackground(Object... params) {

        int taskId = (int) params[0];
        if (taskId == DETAIL_MOVIE) {
            String movieId = (String) params[1];
            String path = base_url.replace("movieId", movieId) + MovieApplication.APIKEY;
            HttpURLConnection conn = null;
            InputStreamReader reader = null;
            try {
                URL url = new URL(path);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == RESULT_OK) {
                    StringBuffer buffer = new StringBuffer();
                    char[] buf = new char[1024 * 10];
                    reader = new InputStreamReader(conn.getInputStream());
                    int len = -1;
                    while (-1 != (len = reader.read(buf))) {
                        String s = new String(buf, 0, len);
                        buffer.append(s);
                    }

                    Log.d(TAG, buffer.toString());

                    JSONObject object = new JSONObject(buffer.toString());
                    int runtime = object.optInt("runtime");
                    JSONArray genres = object.optJSONArray("genres");
                    JSONArray production_countries = object.optJSONArray("production_countries");
                    StringBuffer buffer2 = new StringBuffer();
                    for (int i = 0; i < genres.length(); i++) {
                        JSONObject genre_json = genres.optJSONObject(i);
                        String genre = genre_json.optString("name");
                        buffer2.append(genre);
                        if (i == genres.length() - 1) {
                        } else {
                            buffer2.append("/");
                        }
                    }

                    StringBuffer buffer3 = new StringBuffer();
                    for (int i = 0; i < production_countries.length(); i++) {
                        JSONObject object1 = production_countries.optJSONObject(i);
                        String country = object1.optString("iso_3166_1");
                        buffer3.append(country);
                        if (i != production_countries.length() - 1) {
                            buffer3.append("/");
                        }
                    }


                    ContentValues values = new ContentValues();
                    values.put(MovieEntry.RUNTIME, runtime);
                    values.put(MovieEntry.GENRES, buffer2.toString());
                    values.put(MovieEntry.PRODUCTIONS_COUNTRY, buffer3.toString());
                    ContentResolver resolver = mC.getContentResolver();
                    int num = resolver.update(MovieContract.buildMovieUri(Long.parseLong(movieId)),
                            values, null, null);
                    Cursor cursor = resolver.query(MovieContract.buildMovieUri(Long.parseLong(movieId)), DisplayFragment.CONTRACT_MOVIE_PROJECTIONS, null, null, null);
                    Log.i(TAG, "update row = " + num);

                    return cursor;
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
        } else if (taskId == GET_REVIEWS) {

            String movieId = (String) params[1];
            HttpURLConnection conn = null;
            String path = reviews_url.replace("moveId", movieId) + MovieApplication.APIKEY;
            Log.i(TAG, "moveId==" + movieId);
            ContentResolver resolver = mC.getContentResolver();
            Cursor cursor = resolver.query(MovieContract.CONTENT_REVIEWS_URI, DetailActivity.REVIEWS_PROJECTIONS,
                    ReviewsEntry.MOVE_ID + "=?", new String[]{movieId}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    cursor.close();
                    return null;
                }
            }

            InputStreamReader reader = null;
            try {
                URL url = new URL(path);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    StringBuffer buffer = new StringBuffer();
                    char[] buf = new char[1024 * 10];
                    reader = new InputStreamReader(conn.getInputStream());
                    int len = -1;
                    while (-1 != (len = reader.read(buf))) {
                        String s = new String(buf, 0, len);
                        buffer.append(s);
                    }

                    Log.d(TAG, buffer.toString());
                    JSONObject result_json = new JSONObject(buffer.toString());
                    int totalPage = result_json.optInt("total_pages");
                    int totalResult = result_json.optInt("total_results");
                    JSONArray results_array = result_json.optJSONArray("results");
                    ContentValues[] valuesarr = new ContentValues[results_array.length()];
                    for (int i = 0; i < results_array.length(); i++) {
                        ContentValues values = new ContentValues();
                        JSONObject result = results_array.getJSONObject(i);
                        String authorId = result.optString("id");
                        String author = result.optString("author");
                        String comments = result.optString("content");
                        String authorUrl = result.optString("url");
//                        values.put(ReviewsEntry._ID, i);
                        values.put(ReviewsEntry.AUTHOR_ID, authorId);
                        values.put(ReviewsEntry.AUTHOR, author);
                        values.put(ReviewsEntry.AUTHOR_URL, authorUrl);
                        values.put(ReviewsEntry.COMMENTS, comments);
                        values.put(ReviewsEntry.MOVE_ID, movieId);
                        valuesarr[i] = values;
                    }
                    int num = resolver.bulkInsert(MovieContract.CONTENT_REVIEWS_URI, valuesarr);
                    Log.e(TAG, "insert over and num = " + num);

                    return true;
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
        } else if (taskId == GET_VIDEOS) {
            String movieId = (String) params[1];
            String path = vidos_url.replace("movieId", movieId) + MovieApplication.APIKEY;
            ContentResolver resolver = this.mC.getContentResolver();
            Cursor cursor = resolver.query(MovieContract.CONTENT_VIDEOS_URI, new String[]{VideosEntry._ID},
                    VideosEntry.MOVIE_ID + "=?", new String[]{movieId}, null);
            if (cursor != null && cursor.moveToFirst()) {
                return null;
            }

            InputStreamReader reader = null;
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == RESULT_OK) {
                    StringBuffer buffer = new StringBuffer();
                    char[] buf = new char[1024 * 10];
                    reader = new InputStreamReader(conn.getInputStream());
                    int len = -1;
                    while (-1 != (len = reader.read(buf))) {
                        String s = new String(buf, 0, len);
                        buffer.append(s);
                    }
                    Log.i(TAG, "video_json==" + buffer.toString());
                    JSONObject obj = new JSONObject(buffer.toString());
                    JSONArray array = obj.optJSONArray("results");
                    ContentValues values = new ContentValues();
                    StringBuffer keys = new StringBuffer();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        if (i != array.length() - 1) {
                            keys.append(object.optString("key"));
                            keys.append("#");
                        } else {
                            keys.append(object.optString("key"));
                        }
                    }
                    values.put(VideosEntry.MOVIE_ID, movieId);
                    values.put(VideosEntry.VIDEO_KEYS, keys.toString());
                    resolver.insert(MovieContract.CONTENT_VIDEOS_URI, values);

                    return true;
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


        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (o != null && o instanceof Cursor) {
            mHandler.obtainMessage(DETAIL_MOVIE, o).sendToTarget();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
