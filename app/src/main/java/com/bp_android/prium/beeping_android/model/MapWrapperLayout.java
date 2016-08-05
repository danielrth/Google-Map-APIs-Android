package com.bp_android.prium.beeping_android.model;

import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.content.Context;

/**
 * Created by Vaibhav on 3/15/16.
 */
public class MapWrapperLayout extends FrameLayout {
    private OnDragListener mOnDragListener;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public interface OnDragListener {
        public void onDrag(MotionEvent motionEvent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnDragListener != null) {
            mOnDragListener.onDrag(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnDragListener(OnDragListener mOnDragListener) {
        this.mOnDragListener = mOnDragListener;
    }
}