package yaoxin.example.com.popularmoves.support.views;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by yaoxinxin on 2016/12/21.
 * 解决ScrollView中嵌套ListView的滑动冲突
 */

public class DetailScrollView extends ScrollView {

    private static final String TAG = "ScrollView..";

    float y;

    public DetailScrollView(Context context) {
        this(context, null);
    }

    public DetailScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.y = y;
                Log.i(TAG, TAG + "dispatchTouchEvent....Down");
                break;
            case MotionEvent.ACTION_MOVE:
//                int count = getChildCount();
                ViewGroup viewGroup = (ViewGroup) getChildAt(0);
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = viewGroup.getChildAt(i);
                    if (view instanceof ReviewsListView) {
                        int location[] = new int[2];
                        view.getLocationOnScreen(location);
                        RectF rectF = new RectF(location[0], location[1], location[0] + view.getWidth(),
                                location[1] + view.getHeight());
                        if (rectF.contains(x, y)) {
                            Log.d(TAG, "contaion...");
                            view.getParent().requestDisallowInterceptTouchEvent(true);
//                            return false;
                        }
                    }
                }
                Log.i(TAG, TAG + "dispatchTouchEvent....Move");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, TAG + "dispatchTouchEvent....Up");
                break;
        }

        Log.i(TAG, "Scrollview dispatchTouchEvent is " + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, TAG + "onTouchEvent....Down");
                break;
            case MotionEvent.ACTION_MOVE:

                Log.i(TAG, TAG + "onTouchEvent....Move");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, TAG + "onTouchEvent....Up");
                break;
        }
        Log.i(TAG, "ScrollView onTouchEvent..is " + super.onTouchEvent(ev));
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.y = y;
                Log.i(TAG, TAG + "onInterceptTouchEvent....Down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, TAG + "onInterceptTouchEvent....Move");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, TAG + "onInterceptTouchEvent....Up");
                break;
        }

        Log.i(TAG, "ScrollView onInterceptTouchEvent is .." + super.onInterceptTouchEvent(ev));
        return super.onInterceptTouchEvent(ev);
    }
}
