package com.limelight.binding.input.customcontrols;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.limelight.binding.input.customcontrols.editor.EditControlPopup;

@SuppressLint({"ViewConstructor", "AppCompatCustomView"})
public class ControlButton extends TextView implements ControlInterface {
    private final Paint mRectPaint = new Paint();
    protected ControlData mProperties;
    private final ControlLayout mControlLayout;
    private float mComputedRadius;
    protected boolean mIsToggled = false;
    protected boolean mIsPointerOutOfBounds = false;

    public ControlButton(ControlLayout layout, ControlData properties) {
        super(layout.getContext());
        mControlLayout = layout;
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setPadding(4, 4, 4, 4);
        setTextSize(13);
        setOutlineProvider(null);

        setProperties(properties, true);
        injectBehaviors();
    }

    @Override
    public View getControlView() {
        return this;
    }

    @Override
    public ControlData getProperties() {
        return mProperties;
    }

    @Override
    public void setProperties(ControlData properties, boolean changePos) {
        this.mProperties = properties;
        ControlInterface.super.setProperties(properties, changePos);
        this.mComputedRadius = computeCornerRadius(mProperties.cornerRadius);

        if (mProperties.isToggle) {
            mRectPaint.setColor(0x884D90FE);
        } else {
            mRectPaint.setColor(Color.WHITE);
            mRectPaint.setAlpha(80);
        }

        setText(properties.name);
        setAlpha(mProperties.opacity);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsToggled || (!mProperties.isToggle && isActivated())) {
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), mComputedRadius, mComputedRadius, mRectPaint);
        }
    }

    @Override
    public void loadEditValues(EditControlPopup editControlPopup) {
        editControlPopup.loadValues(getProperties());
    }

    @Override
    public void cloneButton() {
        ControlData cloneData = new ControlData(getProperties());
        cloneData.dynamicX = "${screen_width} / 2 - ${width} / 2";
        cloneData.dynamicY = "${screen_height} / 2 - ${height} / 2";
        mControlLayout.addControlButton(cloneData);
    }

    @Override
    public void removeButton() {
        mControlLayout.getLayout().mControlDataList.remove(getProperties());
        mControlLayout.removeView(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!mProperties.isToggle) {
                    sendKeyPresses(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mProperties.passThruEnabled) {
                    View stream = mControlLayout.getStreamSurface();
                    if (stream != null) stream.dispatchTouchEvent(event);
                }

                if (event.getX() < 0 || event.getX() > getWidth() ||
                        event.getY() < 0 || event.getY() > getHeight()) {
                    if (mProperties.isSwipeable && !mIsPointerOutOfBounds) {
                        if (!triggerToggle()) {
                            sendKeyPresses(false);
                        }
                    }
                    mIsPointerOutOfBounds = true;
                    break;
                }

                if (mIsPointerOutOfBounds) {
                    if (mProperties.isSwipeable && !mProperties.isToggle) {
                        sendKeyPresses(true);
                    }
                }
                mIsPointerOutOfBounds = false;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                if (mProperties.passThruEnabled) {
                    View stream = mControlLayout.getStreamSurface();
                    if (stream != null) stream.dispatchTouchEvent(event);
                }
                mIsPointerOutOfBounds = false;
                if (!triggerToggle()) {
                    sendKeyPresses(false);
                }
                break;
            default:
                return false;
        }

        return true;
    }

    public boolean triggerToggle() {
        if (mProperties.isToggle) {
            mIsToggled = !mIsToggled;
            invalidate();
            sendKeyPresses(mIsToggled);
            return true;
        }
        return false;
    }

    @Override
    public void sendKeyPresses(boolean isDown) {
        setActivated(isDown);
        invalidate();

        if (mProperties.keycodes != null && mControlLayout.getBridge() != null) {
            for (int keycode : mProperties.keycodes) {
                if (keycode == ControlData.SPECIALBTN_TOGGLECTRL) {
                    if (isDown) mControlLayout.toggleControlVisible();
                } else {
                    mControlLayout.getBridge().dispatchAction(keycode, isDown);
                }
            }
        }
    }
}
