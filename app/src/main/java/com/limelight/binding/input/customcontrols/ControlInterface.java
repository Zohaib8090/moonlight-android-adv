package com.limelight.binding.input.customcontrols;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.limelight.binding.input.customcontrols.editor.EditControlPopup;
import com.limelight.binding.input.customcontrols.utils.ControlTools;

public interface ControlInterface extends View.OnLongClickListener {
    View getControlView();

    ControlData getProperties();

    default void setProperties(ControlData properties) {
        setProperties(properties, true);
    }

    void removeButton();

    void cloneButton();

    default void setVisible(boolean isVisible) {
        if (getProperties().isHideable) {
            getControlView().setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    void sendKeyPresses(boolean isDown);

    void loadEditValues(EditControlPopup editControlPopup);

    default ControlLayout getControlLayoutParent() {
        return (ControlLayout) getControlView().getParent();
    }

    default void updateProperties() {
        setProperties(getProperties());
    }

    default void setProperties(ControlData properties, boolean changePos) {
        if (changePos) {
            getControlView().setX(properties.insertDynamicPos(properties.dynamicX));
            getControlView().setY(properties.insertDynamicPos(properties.dynamicY));
        }

        ViewGroup.LayoutParams params = getControlView().getLayoutParams();
        if (params == null) {
            params = new FrameLayout.LayoutParams((int) properties.getWidth(), (int) properties.getHeight());
        }
        params.width = (int) properties.getWidth();
        params.height = (int) properties.getHeight();
        getControlView().setLayoutParams(params);
        setBackground();
    }

    default void setBackground() {
        GradientDrawable gd = getControlView().getBackground() instanceof GradientDrawable
                ? (GradientDrawable) getControlView().getBackground()
                : new GradientDrawable();
        gd.setColor(getProperties().bgColor);
        gd.setStroke((int) ControlTools.dpToPx(getProperties().strokeWidth), getProperties().strokeColor);
        gd.setCornerRadius(computeCornerRadius(getProperties().cornerRadius));
        getControlView().setBackground(gd);
    }

    default void setDynamicX(String dynamicX) {
        getProperties().dynamicX = dynamicX;
        getControlView().setX(getProperties().insertDynamicPos(dynamicX));
    }

    default void setDynamicY(String dynamicY) {
        getProperties().dynamicY = dynamicY;
        getControlView().setY(getProperties().insertDynamicPos(dynamicY));
    }

    default String generateDynamicX(float x) {
        int screenW = ControlTools.getScreenWidth();
        if (x + (getProperties().getWidth() / 2f) > screenW / 2f) {
            return (x + getProperties().getWidth()) / screenW + " * ${screen_width} - ${width}";
        } else {
            return (x / screenW) + " * ${screen_width}";
        }
    }

    default String generateDynamicY(float y) {
        int screenH = ControlTools.getScreenHeight();
        if (y + (getProperties().getHeight() / 2f) > screenH / 2f) {
            return (y + getProperties().getHeight()) / screenH + " * ${screen_height} - ${height}";
        } else {
            return (y / screenH) + " * ${screen_height}";
        }
    }

    default void regenerateDynamicCoordinates() {
        getProperties().dynamicX = generateDynamicX(getControlView().getX());
        getProperties().dynamicY = generateDynamicY(getControlView().getY());
        updateProperties();
    }

    default float computeCornerRadius(float radiusInPercent) {
        float minSize = Math.min(getProperties().getWidth(), getProperties().getHeight());
        return (minSize / 2f) * (radiusInPercent / 100f);
    }

    default void snapAndAlign(float x, float y) {
        getControlView().setX(x);
        getControlView().setY(y);
        setDynamicX(generateDynamicX(x));
        setDynamicY(generateDynamicY(y));
    }

    default void injectBehaviors() {
        injectProperties();
        injectTouchEventBehavior();
    }

    default void injectProperties() {
        getControlView().post(() -> getControlView().setTranslationZ(10));
    }

    default void injectTouchEventBehavior() {
        getControlView().setOnTouchListener(new View.OnTouchListener() {
            private boolean mCanTriggerLongClick = true;
            private float downX, downY;
            private float downRawX, downRawY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                ControlLayout parent = getControlLayoutParent();
                if (parent == null) return false;

                if (!parent.getModifiable()) {
                    view.onTouchEvent(event);
                    return true;
                }

                if (event.getActionMasked() == MotionEvent.ACTION_UP && mCanTriggerLongClick) {
                    onLongClick(view);
                }

                int screenW = ControlTools.getScreenWidth();
                int screenH = ControlTools.getScreenHeight();

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mCanTriggerLongClick = true;
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        downX = downRawX - view.getX();
                        downY = downRawY - view.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getRawX() - downRawX) > 8 || Math.abs(event.getRawY() - downRawY) > 8) {
                            mCanTriggerLongClick = false;
                        }
                        parent.adaptPanelPosition();
                        float newX = Math.max(0, Math.min(event.getRawX() - downX, screenW - view.getWidth()));
                        float newY = Math.max(0, Math.min(event.getRawY() - downY, screenH - view.getHeight()));
                        snapAndAlign(newX, newY);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    default boolean onLongClick(View v) {
        ControlLayout parent = getControlLayoutParent();
        if (parent != null && parent.getModifiable()) {
            parent.editControlButton(this);
            if (parent.getActionRow() != null) {
                parent.getActionRow().setFollowedButton(this);
            }
        }
        return true;
    }
}
