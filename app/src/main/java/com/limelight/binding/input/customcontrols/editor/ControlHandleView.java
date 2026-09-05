package com.limelight.binding.input.customcontrols.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


import com.limelight.R;
import com.limelight.binding.input.customcontrols.ControlInterface;

public class ControlHandleView extends View {
    private ControlInterface mView;
    private float mXOffset, mYOffset;

    private final ViewTreeObserver.OnPreDrawListener mPositionListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (mView == null || !mView.getControlView().isShown()) {
                hide();
                return true;
            }
            setX(mView.getControlView().getX() + mView.getControlView().getWidth() - getWidth() / 2f);
            setY(mView.getControlView().getY() + mView.getControlView().getHeight() - getHeight() / 2f);
            return true;
        }
    };

    public ControlHandleView(Context context) {
        super(context);
        init();
    }

    private void init() {
        int size = (int) (28 * getResources().getDisplayMetrics().density);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_view_handle);
        setBackground(drawable);
        setLayoutParams(new ViewGroup.LayoutParams(size, size));
        setTranslationZ(20f);
        setVisibility(GONE);
    }

    public void setControlButton(ControlInterface controlInterface) {
        if (mView != null) {
            mView.getControlView().getViewTreeObserver().removeOnPreDrawListener(mPositionListener);
        }

        mView = controlInterface;
        if (mView == null) {
            hide();
            return;
        }

        setVisibility(VISIBLE);
        mView.getControlView().getViewTreeObserver().addOnPreDrawListener(mPositionListener);
        setX(controlInterface.getControlView().getX() + controlInterface.getControlView().getWidth() - getWidth() / 2f);
        setY(controlInterface.getControlView().getY() + controlInterface.getControlView().getHeight() - getHeight() / 2f);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mView == null) return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mXOffset = event.getX();
                mYOffset = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newW = Math.max(30, event.getRawX() - mView.getControlView().getX());
                float newH = Math.max(30, event.getRawY() - mView.getControlView().getY());
                mView.getProperties().setWidth(newW);
                mView.getProperties().setHeight(newH);
                mView.regenerateDynamicCoordinates();
                break;
        }
        return true;
    }

    public void hide() {
        if (mView != null) {
            mView.getControlView().getViewTreeObserver().removeOnPreDrawListener(mPositionListener);
        }
        setVisibility(GONE);
        mView = null;
    }
}
