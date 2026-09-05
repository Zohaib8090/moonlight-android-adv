package com.limelight.binding.input.customcontrols;

import android.annotation.SuppressLint;
import android.view.View;

import com.limelight.binding.input.customcontrols.editor.EditControlPopup;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class ControlDrawer extends ControlButton {
    public final List<ControlSubButton> buttons = new ArrayList<>();
    public final ControlDrawerData drawerData;
    public boolean areButtonsVisible = false;

    public ControlDrawer(ControlLayout layout, ControlDrawerData drawerData) {
        super(layout, drawerData.properties);
        this.drawerData = drawerData;
        this.areButtonsVisible = layout.getModifiable();
    }

    public void addSubButton(ControlSubButton button) {
        buttons.add(button);
        button.setVisibility(areButtonsVisible ? View.VISIBLE : View.GONE);
        alignButtons();
    }

    public void toggleDrawer() {
        areButtonsVisible = !areButtonsVisible;
        int visibility = areButtonsVisible ? View.VISIBLE : View.GONE;
        for (ControlSubButton sub : buttons) {
            sub.setVisibility(visibility);
        }
        alignButtons();
    }

    public void alignButtons() {
        if (buttons.isEmpty()) return;
        float margin = 8f;

        for (int i = 0; i < buttons.size(); i++) {
            ControlSubButton sub = buttons.get(i);
            switch (drawerData.orientation) {
                case RIGHT:
                    sub.setX(getX() + (getWidth() + margin) * (i + 1));
                    sub.setY(getY());
                    break;
                case LEFT:
                    sub.setX(getX() - (sub.getWidth() + margin) * (i + 1));
                    sub.setY(getY());
                    break;
                case UP:
                    sub.setX(getX());
                    sub.setY(getY() - (sub.getHeight() + margin) * (i + 1));
                    break;
                case DOWN:
                    sub.setX(getX());
                    sub.setY(getY() + (getHeight() + margin) * (i + 1));
                    break;
                case FREE:
                    break;
            }
            sub.regenerateDynamicCoordinates();
        }
    }

    @Override
    public boolean triggerToggle() {
        toggleDrawer();
        return true;
    }

    @Override
    public void loadEditValues(EditControlPopup editControlPopup) {
        super.loadEditValues(editControlPopup);
        editControlPopup.showOrientation(true);
    }
}
