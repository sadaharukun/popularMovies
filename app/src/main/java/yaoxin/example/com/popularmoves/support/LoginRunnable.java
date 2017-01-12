package yaoxin.example.com.popularmoves.support;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import yaoxin.example.com.popularmoves.MainActivity;
import yaoxin.example.com.popularmoves.MovieApplication;

/**
 * Created by yaoxinxin on 2017/1/11.
 */

public class LoginRunnable implements Runnable {

    private static final String TAG = "LoginRunnable";

    private Context c;
    private String path;
    private static final String REQUEST_TOKEN = "https://api.themoviedb.org/3/authentication/token/new?api_key=";
    private String username;
    private String password;
    private Handler handler;
    private HashMap<String, String> params = new HashMap<>();

    public LoginRunnable(Context c, String username, String password, Handler handler) {
        this.c = c;
        this.username = username;
        this.password = password;
        this.handler = handler;
//        params.put("username", username);
//        params.put("password", password);
//        params.put("request_token", "b74a4e1c05c2b424d0a53a033cb6a601cea6c0cc");
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        InputStreamReader reader = null;
//        byte[] data = getRequestData(params, "utf-8").toString().getBytes();
        try {
            String request_token_path = REQUEST_TOKEN + MovieApplication.APIKEY;
            URL url = new URL(request_token_path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                reader = new InputStreamReader(inputStream);
                StringBuffer buffer = new StringBuffer();
                char[] buf = new char[1024 * 10];
                int len = -1;
                while (-1 != (len = reader.read(buf))) {
                    String s = new String(buf, 0, len);
                    buffer.append(s);
                }
                Log.i(TAG, "request_token.." + buffer.toString());
                JSONObject tokenjson = new JSONObject(buffer.toString());
                boolean success = tokenjson.getBoolean("success");
                if (success) {
                    String requestTime = tokenjson.getString("expires_at");
                    String requestToken = tokenjson.getString("request_token");
                    if (!validwithlogin(conn, requestToken)) {
                        handler.obtainMessage(MainActivity.LOGIN_SESSION, false).sendToTarget();
                    }
                }
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

    private boolean validwithlogin(HttpURLConnection conn, String requestToken) throws IOException, JSONException {
        InputStreamReader reader;
        String validwithLoginPath = "https://api.themoviedb.org/3/authentication/token/validate_with_login?api_key=";
        validwithLoginPath += MovieApplication.APIKEY + "&username=" + username + "&password=" + password
                + "&request_token=" + requestToken;
        URL url = new URL(validwithLoginPath);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            reader = new InputStreamReader(inputStream);
            StringBuffer buffer = new StringBuffer();
            char[] buf = new char[1024 * 10];
            int len = -1;
            while (-1 != (len = reader.read(buf))) {
                String s = new String(buf, 0, len);
                buffer.append(s);
            }
            reader.close();
            Log.i(TAG, "validloginresult.." + buffer.toString());
            JSONObject loginResultJson = new JSONObject(buffer.toString());
            boolean success = loginResultJson.getBoolean("success");
            if (success) {
                String requesttoken = loginResultJson.getString("request_token");
                return createSession(conn, requesttoken);
            }

        }
        return false;
    }

    /**
     * use token to create session
     *
     * @param requesttoken
     */
    private boolean createSession(HttpURLConnection conn, String requesttoken) throws IOException, JSONException {
        InputStreamReader reader;
        String base_session = "https://api.themoviedb.org/3/authentication/session/new?api_key=";
        String sessionPath = base_session + MovieApplication.APIKEY + "&request_token=" + requesttoken;
        URL url = new URL(sessionPath);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            reader = new InputStreamReader(inputStream);
            StringBuffer buffer = new StringBuffer();
            char[] buf = new char[1024 * 10];
            int len = -1;
            while (-1 != (len = reader.read(buf))) {
                String s = new String(buf, 0, len);
                buffer.append(s);
            }
            reader.close();
            Log.i(TAG, "session.." + buffer.toString());
            JSONObject sessionJson = new JSONObject(buffer.toString());
            boolean success = sessionJson.getBoolean("success");
            if (success) {
                String session_id = sessionJson.getString("session_id");
                return getaccountMessage(conn, session_id);
            }
        }
        return false;
    }

    private boolean getaccountMessage(HttpURLConnection conn, String session_id) throws IOException {
        String baseurl = "https://api.themoviedb.org/3/account?api_key=";
        InputStreamReader reader;
        String path = baseurl + MovieApplication.APIKEY + "&session_id=" + session_id;
        URL url = new URL(path);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            reader = new InputStreamReader(inputStream);
            StringBuffer buffer = new StringBuffer();
            char[] buf = new char[1024 * 10];
            int len = -1;
            while (-1 != (len = reader.read(buf))) {
                String s = new String(buf, 0, len);
                buffer.append(s);
            }
            reader.close();
            Log.i(TAG, "account.." + buffer.toString());
            Message msg = handler.obtainMessage();
            msg.what = MainActivity.LOGIN_SESSION;
            msg.obj = true;
            Bundle data = new Bundle();
            data.putString("session_id", session_id);
            data.putString("accountmsg", buffer.toString());
            msg.setData(data);
            handler.sendMessage(msg);
            return true;
        }

        return false;
    }


    private StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer buffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entryset : params.entrySet()) {

                buffer.append(URLEncoder.encode(entryset.getKey(), encode));
                buffer.append("=").append(URLEncoder.encode(entryset.getValue(), encode)).append("&");
            }
            buffer.deleteCharAt(buffer.length() - 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return buffer;
    }


}
