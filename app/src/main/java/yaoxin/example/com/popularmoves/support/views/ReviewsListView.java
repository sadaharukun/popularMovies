package yaoxin.example.com.popularmoves.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by yaoxinxin on 2016/12/19.
 */

public class ReviewsListView extends ListView {

    private static final String TAG = "ListView";

    float y;

    public ReviewsListView(Context context) {
        this(context, null);
    }

    public ReviewsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.y = y;
                Log.i(TAG, TAG + "dispatchTouchEvent....Down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, TAG + "dispatchTouchEvent....Move");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, TAG + "dispatchTouchEvent....Up");
                break;
        }

        Log.i(TAG, "ListView dispatchTouchEvent is .." + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.y = y;
                Log.i(TAG, TAG + "onTouchEvent....Down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, TAG + "onTouchEvent....Move");
                break;
            case MotionEvent.ACTION_UP:

                Log.i(TAG, TAG + "onTouchEvent....Up");
                break;
        }

        Log.i(TAG, "listView onTouchEvent..is" + super.onTouchEvent(ev));
        return super.onTouchEvent(ev);
    }


}
