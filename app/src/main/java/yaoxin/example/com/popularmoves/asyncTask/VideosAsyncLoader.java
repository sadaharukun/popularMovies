package yaoxin.example.com.popularmoves.asyncTask;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by yaoxinxin on 2016/12/19.
 */

public class VideosAsyncLoader extends AsyncTaskLoader {


    public VideosAsyncLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        return null;
    }

    @Override
    public void deliverResult(Object data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    public void onCanceled(Object data) {
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    
}
