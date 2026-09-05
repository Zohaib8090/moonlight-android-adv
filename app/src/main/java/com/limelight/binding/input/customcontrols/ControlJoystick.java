package com.limelight.binding.input.customcontrols;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.limelight.binding.input.customcontrols.editor.EditControlPopup;

@SuppressLint("ViewConstructor")
public class ControlJoystick extends View implements ControlInterface {
    private final ControlLayout mControlLayout;
    private ControlJoystickData mData;

    private final Paint mBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mHatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mHatX = 0f;
    private float mHatY = 0f;
    private boolean mActive = false;

    // Active directional keys state for WASD mode
    private boolean mPressedW = false;
    private boolean mPressedS = false;
    private boolean mPressedA = false;
    private boolean mPressedD = false;

    public ControlJoystick(ControlLayout layout, ControlJoystickData data) {
        super(layout.getContext());
        this.mControlLayout = layout;
        this.mData = data;

        mBasePaint.setColor(0x55000000);
        mHatPaint.setColor(0x994D90FE);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2f);

        setProperties(data, true);
        injectBehaviors();
    }

    @Override
    public View getControlView() {
        return this;
    }

    @Override
    public ControlData getProperties() {
        return mData;
    }

    @Override
    public void setProperties(ControlData properties, boolean changePos) {
        if (properties instanceof ControlJoystickData) {
            this.mData = (ControlJoystickData) properties;
        }
        ControlInterface.super.setProperties(properties, changePos);
        setAlpha(mData.opacity);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHatX = w / 2f;
        mHatY = h / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float baseRadius = Math.min(cx, cy) * 0.85f;
        float hatRadius = baseRadius * 0.45f;

        // Base
        canvas.drawCircle(cx, cy, baseRadius, mBasePaint);
        canvas.drawCircle(cx, cy, baseRadius, mBorderPaint);

        // Center / Hat
        float hx = mActive ? mHatX : cx;
        float hy = mActive ? mHatY : cy;
        canvas.drawCircle(hx, hy, hatRadius, mHatPaint);
    }

    @Override
    public void removeButton() {
        mControlLayout.getLayout().mJoystickDataList.remove(mData);
        mControlLayout.removeView(this);
    }

    @Override
    public void cloneButton() {
        ControlJoystickData clone = new ControlJoystickData(mData.joystickMode);
        clone.dynamicX = "${screen_width} / 2 - ${width} / 2";
        clone.dynamicY = "${screen_height} / 2 - ${height} / 2";
        mControlLayout.addJoystickButton(clone);
    }

    @Override
    public void sendKeyPresses(boolean isDown) {
        if (!isDown) {
            resetInput();
        }
    }

    @Override
    public void loadEditValues(EditControlPopup editControlPopup) {
        editControlPopup.loadValues(mData);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mControlLayout.getModifiable()) {
            return super.onTouchEvent(event);
        }

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float maxRadius = Math.min(cx, cy) * 0.85f;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                mActive = true;
                float dx = event.getX() - cx;
                float dy = event.getY() - cy;
                float distance = (float) Math.hypot(dx, dy);

                if (distance > maxRadius) {
                    mHatX = cx + (dx / distance) * maxRadius;
                    mHatY = cy + (dy / distance) * maxRadius;
                } else {
                    mHatX = event.getX();
                    mHatY = event.getY();
                }

                float normX = Math.max(-1f, Math.min(1f, (mHatX - cx) / maxRadius));
                float normY = Math.max(-1f, Math.min(1f, (mHatY - cy) / maxRadius));

                processJoystickMovement(normX, normY);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                mActive = false;
                mHatX = cx;
                mHatY = cy;
                resetInput();
                invalidate();
                break;
        }

        return true;
    }

    private void processJoystickMovement(float normX, float normY) {
        if (mControlLayout.getBridge() == null) return;

        if (mData.joystickMode == ControlJoystickData.JOYSTICK_MODE_WASD) {
            float deadzone = 0.25f;
            boolean wantW = normY < -deadzone;
            boolean wantS = normY > deadzone;
            boolean wantA = normX < -deadzone;
            boolean wantD = normX > deadzone;

            updateDirectionKey(KeyEvent.KEYCODE_W, mPressedW, wantW);
            updateDirectionKey(KeyEvent.KEYCODE_S, mPressedS, wantS);
            updateDirectionKey(KeyEvent.KEYCODE_A, mPressedA, wantA);
            updateDirectionKey(KeyEvent.KEYCODE_D, mPressedD, wantD);

            mPressedW = wantW;
            mPressedS = wantS;
            mPressedA = wantA;
            mPressedD = wantD;
        } else if (mData.joystickMode == ControlJoystickData.JOYSTICK_MODE_GAMEPAD_LEFT) {
            short sx = (short) (normX * 0x7FFE);
            short sy = (short) (-normY * 0x7FFE);
            mControlLayout.getBridge().setLeftStick(sx, sy);
        } else if (mData.joystickMode == ControlJoystickData.JOYSTICK_MODE_GAMEPAD_RIGHT) {
            short sx = (short) (normX * 0x7FFE);
            short sy = (short) (-normY * 0x7FFE);
            mControlLayout.getBridge().setRightStick(sx, sy);
        }
    }

    private void updateDirectionKey(int keyCode, boolean currentPressed, boolean newPressed) {
        if (currentPressed != newPressed) {
            mControlLayout.getBridge().dispatchAction(keyCode, newPressed);
        }
    }

    private void resetInput() {
        if (mControlLayout.getBridge() == null) return;

        if (mData.joystickMode == ControlJoystickData.JOYSTICK_MODE_WASD) {
            if (mPressedW) { mControlLayout.getBridge().dispatchAction(KeyEvent.KEYCODE_W, false); mPressedW = false; }
            if (mPressedS) { mControlLayout.getBridge().dispatchAction(KeyEvent.KEYCODE_S, false); mPressedS = false; }
            if (mPressedA) { mControlLayout.getBridge().dispatchAction(KeyEvent.KEYCODE_A, false); mPressedA = false; }
            if (mPressedD) { mControlLayout.getBridge().dispatchAction(KeyEvent.KEYCODE_D, false); mPressedD = false; }
        } else if (mData.joystickMode == ControlJoystickData.JOYSTICK_MODE_GAMEPAD_LEFT) {
            mControlLayout.getBridge().setLeftStick((short) 0, (short) 0);
        } else if (mData.joystickMode == ControlJoystickData.JOYSTICK_MODE_GAMEPAD_RIGHT) {
            mControlLayout.getBridge().setRightStick((short) 0, (short) 0);
        }
    }
}
