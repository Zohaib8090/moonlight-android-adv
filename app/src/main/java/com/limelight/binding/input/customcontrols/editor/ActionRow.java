package com.limelight.binding.input.customcontrols.editor;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.limelight.R;
import com.limelight.binding.input.customcontrols.ControlDrawer;
import com.limelight.binding.input.customcontrols.ControlInterface;
import com.limelight.binding.input.customcontrols.ControlSubButton;

public class ActionRow extends LinearLayout {
    private ControlInterface mFollowedInterface;
    private View mFollowedView;

    private final ViewTreeObserver.OnPreDrawListener mFollowedViewListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (mFollowedView == null || !mFollowedView.isShown()) {
                hide();
                return true;
            }
            updatePosition();
            return true;
        }
    };

    private ImageView btnSettings;
    private ImageView btnClone;
    private ImageView btnDelete;
    private ImageView btnAddSub;

    public ActionRow(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.background_control_editor);
        int pad = (int) (4 * getResources().getDisplayMetrics().density);
        setPadding(pad, pad, pad, pad);
        setTranslationZ(25f);
        setVisibility(GONE);

        btnSettings = createActionButton(R.drawable.ic_settings, v -> {
            if (mFollowedInterface != null) {
                mFollowedInterface.getControlLayoutParent().editControlButton(mFollowedInterface);
            }
        });

        btnClone = createActionButton(R.drawable.ic_copy, v -> {
            if (mFollowedInterface != null) {
                mFollowedInterface.cloneButton();
            }
        });

        btnAddSub = createActionButton(R.drawable.ic_add, v -> {
            if (mFollowedInterface instanceof ControlDrawer) {
                ControlDrawer drawer = (ControlDrawer) mFollowedInterface;
                mFollowedInterface.getControlLayoutParent().addSubButton(drawer, new com.limelight.binding.input.customcontrols.ControlData("SUB"));
            }
        });

        btnDelete = createActionButton(R.drawable.ic_trash, v -> {
            if (mFollowedInterface != null) {
                mFollowedInterface.removeButton();
                hide();
            }
        });

        addView(btnSettings);
        addView(btnClone);
        addView(btnAddSub);
        addView(btnDelete);
    }

    private ImageView createActionButton(int resId, OnClickListener listener) {
        ImageView iv = new ImageView(getContext());
        int size = (int) (24 * getResources().getDisplayMetrics().density);
        int pad = (int) (3 * getResources().getDisplayMetrics().density);
        LayoutParams lp = new LayoutParams(size, size);
        lp.setMargins(2, 0, 2, 0);
        iv.setLayoutParams(lp);
        iv.setPadding(pad, pad, pad, pad);
        iv.setImageDrawable(getResources().getDrawable(resId));
        iv.setBackgroundResource(R.drawable.background_item);
        iv.setOnClickListener(listener);
        return iv;
    }

    public void setFollowedButton(ControlInterface controlInterface) {
        if (mFollowedView != null) {
            mFollowedView.getViewTreeObserver().removeOnPreDrawListener(mFollowedViewListener);
        }

        mFollowedInterface = controlInterface;
        if (controlInterface == null) {
            hide();
            return;
        }

        mFollowedView = controlInterface.getControlView();
        mFollowedView.getViewTreeObserver().addOnPreDrawListener(mFollowedViewListener);

        btnAddSub.setVisibility(controlInterface instanceof ControlDrawer ? VISIBLE : GONE);
        setVisibility(VISIBLE);
        updatePosition();
    }

    private void updatePosition() {
        if (mFollowedView == null) return;
        float x = mFollowedView.getX() + mFollowedView.getWidth() / 2f - getWidth() / 2f;
        float y = mFollowedView.getY() - getHeight() - 10;
        if (y < 0) {
            y = mFollowedView.getY() + mFollowedView.getHeight() + 10;
        }
        setX(Math.max(0, x));
        setY(Math.max(0, y));
    }

    public void hide() {
        if (mFollowedView != null) {
            mFollowedView.getViewTreeObserver().removeOnPreDrawListener(mFollowedViewListener);
        }
        setVisibility(GONE);
        mFollowedView = null;
        mFollowedInterface = null;
    }
}
