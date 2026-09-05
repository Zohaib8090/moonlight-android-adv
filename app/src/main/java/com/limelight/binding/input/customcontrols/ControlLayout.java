package com.limelight.binding.input.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.limelight.R;
import com.limelight.binding.input.customcontrols.bridge.MoonlightControlBridge;
import com.limelight.binding.input.customcontrols.editor.ActionRow;
import com.limelight.binding.input.customcontrols.editor.ControlHandleView;
import com.limelight.binding.input.customcontrols.editor.ControlMenuDialog;
import com.limelight.binding.input.customcontrols.editor.EditControlPopup;

import java.util.ArrayList;
import java.util.List;

public class ControlLayout extends FrameLayout {
    private CustomControls mLayout;
    private View mStreamSurface = null;
    private MoonlightControlBridge mBridge = null;

    private boolean mModifiable = false;
    private boolean mControlVisible = true;

    private EditControlPopup mControlPopup = null;
    private ControlHandleView mHandleView = null;
    public ActionRow mActionRow = null;
    private ControlMenuDialog mMenuDialog = null;
    private ImageView mBtnToggleEditor = null;

    public ControlLayout(Context ctx) {
        super(ctx);
        init();
    }

    public ControlLayout(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init();
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);

        // Edit Mode Toggle Button (small gear at top left)
        mBtnToggleEditor = new ImageView(getContext());
        int size = (int) (32 * getResources().getDisplayMetrics().density);
        int pad = (int) (6 * getResources().getDisplayMetrics().density);
        LayoutParams lp = new LayoutParams(size, size);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.setMargins(16, 16, 0, 0);
        mBtnToggleEditor.setLayoutParams(lp);
        mBtnToggleEditor.setPadding(pad, pad, pad, pad);
        mBtnToggleEditor.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings));
        mBtnToggleEditor.setBackgroundResource(R.drawable.background_control_editor);
        mBtnToggleEditor.setAlpha(0.35f);
        mBtnToggleEditor.setTranslationZ(30f);
        mBtnToggleEditor.setOnClickListener(v -> {
            if (!mModifiable) {
                setModifiable(true);
                if (mMenuDialog != null) mMenuDialog.show();
            } else {
                if (mMenuDialog != null) {
                    if (mMenuDialog.isShowing()) mMenuDialog.hide();
                    else mMenuDialog.show();
                }
            }
        });
        addView(mBtnToggleEditor);

        // Editor components
        mActionRow = new ActionRow(getContext());
        addView(mActionRow);

        mHandleView = new ControlHandleView(getContext());
        addView(mHandleView);

        mControlPopup = new EditControlPopup(getContext(), this);
        mMenuDialog = new ControlMenuDialog(this, this);
    }

    public void setBridge(MoonlightControlBridge bridge) {
        this.mBridge = bridge;
    }

    public MoonlightControlBridge getBridge() {
        return mBridge;
    }

    public void setStreamSurface(View streamSurface) {
        this.mStreamSurface = streamSurface;
    }

    public View getStreamSurface() {
        return mStreamSurface;
    }

    public CustomControls getLayout() {
        return mLayout;
    }

    public void loadLayoutFromPreferences() {
        loadLayout(CustomControls.loadFromPreferences(getContext()));
    }

    public void loadDefaultLayout() {
        loadLayout(CustomControls.createDefaultLayout());
    }

    public void loadLayout(CustomControls controlLayout) {
        removeAllButtons();

        if (controlLayout == null) {
            controlLayout = CustomControls.createDefaultLayout();
        }
        mLayout = controlLayout;

        // 1. Joysticks
        for (ControlJoystickData joystick : mLayout.mJoystickDataList) {
            addJoystickView(joystick);
        }

        // 2. Buttons
        for (ControlData button : mLayout.mControlDataList) {
            addControlView(button);
        }

        // 3. Drawers
        for (ControlDrawerData drawer : mLayout.mDrawerDataList) {
            addDrawerView(drawer);
        }

        mBtnToggleEditor.bringToFront();
    }

    private void removeAllButtons() {
        List<View> toRemove = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ControlInterface) {
                toRemove.add(child);
            }
        }
        for (View v : toRemove) {
            removeView(v);
        }
    }

    public void addControlButton(ControlData data) {
        if (mLayout == null) mLayout = new CustomControls();
        mLayout.mControlDataList.add(data);
        addControlView(data);
    }

    private void addControlView(ControlData data) {
        ControlButton btn = new ControlButton(this, data);
        addView(btn);
        btn.setVisibility(mControlVisible ? View.VISIBLE : View.GONE);
    }

    public void addDrawer(ControlDrawerData drawerData) {
        if (mLayout == null) mLayout = new CustomControls();
        mLayout.mDrawerDataList.add(drawerData);
        addDrawerView(drawerData);
    }

    private void addDrawerView(ControlDrawerData drawerData) {
        ControlDrawer drawer = new ControlDrawer(this, drawerData);
        addView(drawer);
        drawer.setVisibility(mControlVisible ? View.VISIBLE : View.GONE);

        for (ControlData sub : drawerData.buttonProperties) {
            ControlSubButton subBtn = new ControlSubButton(this, sub, drawer);
            addView(subBtn);
            drawer.addSubButton(subBtn);
        }
    }

    public void addSubButton(ControlDrawer drawer, ControlData subData) {
        drawer.drawerData.buttonProperties.add(subData);
        ControlSubButton subBtn = new ControlSubButton(this, subData, drawer);
        addView(subBtn);
        drawer.addSubButton(subBtn);
    }

    public void addJoystickButton(ControlJoystickData data) {
        if (mLayout == null) mLayout = new CustomControls();
        mLayout.mJoystickDataList.add(data);
        addJoystickView(data);
    }

    private void addJoystickView(ControlJoystickData data) {
        ControlJoystick stick = new ControlJoystick(this, data);
        addView(stick);
        stick.setVisibility(mControlVisible ? View.VISIBLE : View.GONE);
    }

    public void saveLayout() {
        if (mLayout != null) {
            mLayout.saveToPreferences(getContext());
        }
    }

    public boolean getModifiable() {
        return mModifiable;
    }

    public void setModifiable(boolean isModifiable) {
        this.mModifiable = isModifiable;
        if (!isModifiable) {
            if (mControlPopup != null) mControlPopup.hide();
            if (mActionRow != null) mActionRow.hide();
            if (mHandleView != null) mHandleView.hide();
            if (mMenuDialog != null) mMenuDialog.hide();
            mBtnToggleEditor.setAlpha(0.35f);
        } else {
            mBtnToggleEditor.setAlpha(1.0f);
            setControlVisible(true);
        }
    }

    public void toggleControlVisible() {
        mControlVisible = !mControlVisible;
        setControlVisible(mControlVisible);
    }

    public void setControlVisible(boolean isVisible) {
        if (mModifiable) return;
        this.mControlVisible = isVisible;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ControlInterface) {
                child.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void editControlButton(ControlInterface button) {
        if (mControlPopup != null) {
            mControlPopup.setCurrentlyEditedButton(button);
            mControlPopup.appear(true);
            button.loadEditValues(mControlPopup);
        }

        if (mHandleView != null) {
            mHandleView.setControlButton(button);
        }
    }

    public void adaptPanelPosition() {
        if (mControlPopup != null) {
            mControlPopup.adaptPanelPosition();
        }
    }

    public List<ControlInterface> getButtonChildren() {
        List<ControlInterface> list = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ControlInterface) {
                list.add((ControlInterface) child);
            }
        }
        return list;
    }
}
