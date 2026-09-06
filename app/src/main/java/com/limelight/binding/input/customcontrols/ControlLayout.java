package com.limelight.binding.input.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.limelight.binding.input.customcontrols.bridge.MoonlightControlBridge;
import com.limelight.binding.input.customcontrols.editor.ActionRow;
import com.limelight.binding.input.customcontrols.editor.ControlMenuDialog;

import java.util.ArrayList;
import java.util.List;

public class ControlLayout extends FrameLayout {
    private CustomControls mLayout;
    private View mStreamSurface = null;
    private MoonlightControlBridge mBridge = null;

    private boolean mModifiable = false;
    private boolean mControlVisible = true;

    private ControlMenuDialog mMenuDialog = null;
    private ActionRow mActionRow = null;

    public ControlLayout(Context ctx) {
        super(ctx);
        init();
    }

    public ControlLayout(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(android.graphics.Color.TRANSPARENT);
        setClipChildren(false);
        setClipToPadding(false);
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
            if (mMenuDialog != null) mMenuDialog.hide();
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
        // editing is handled by ControlProfileEditorActivity
    }

    public void setActionRow(ActionRow actionRow) {
        this.mActionRow = actionRow;
    }

    public ActionRow getActionRow() {
        return mActionRow;
    }

    public void adaptPanelPosition() {}

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
