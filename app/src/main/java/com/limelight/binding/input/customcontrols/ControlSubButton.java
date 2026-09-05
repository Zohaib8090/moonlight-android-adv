package com.limelight.binding.input.customcontrols;

import android.annotation.SuppressLint;

@SuppressLint("ViewConstructor")
public class ControlSubButton extends ControlButton {
    private final ControlDrawer parentDrawer;

    public ControlSubButton(ControlLayout layout, ControlData properties, ControlDrawer parentDrawer) {
        super(layout, properties);
        this.parentDrawer = parentDrawer;
    }

    public ControlDrawer getParentDrawer() {
        return parentDrawer;
    }

    @Override
    public void removeButton() {
        parentDrawer.drawerData.buttonProperties.remove(getProperties());
        parentDrawer.buttons.remove(this);
        getControlLayoutParent().removeView(this);
    }
}
