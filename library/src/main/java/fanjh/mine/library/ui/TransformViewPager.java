package fanjh.mine.library.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fanjh.mine.library.R;


/**
* @author fanjh
* @date 2017/12/22 10:29
* @description 处理滑动的ViewPager
* @note
**/
public class TransformViewPager extends ViewPager {
    private boolean canTouchScroll = true;

    public TransformViewPager(Context context) {
        this(context,null);
    }

    public TransformViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TransformViewPager);
        canTouchScroll = typedArray.getBoolean(R.styleable.TransformViewPager_canTouchScroll,true);
        typedArray.recycle();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canTouchScroll && super.onInterceptTouchEvent(ev);
    }
}
