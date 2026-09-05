package com.limelight.binding.input.customcontrols.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.limelight.R;
import com.limelight.binding.input.customcontrols.ControlData;
import com.limelight.binding.input.customcontrols.ControlDrawerData;
import com.limelight.binding.input.customcontrols.ControlJoystickData;
import com.limelight.binding.input.customcontrols.ControlLayout;
import com.limelight.binding.input.customcontrols.CustomControls;

public class ControlMenuDialog {
    private final ControlLayout layout;
    private final View menuView;

    public ControlMenuDialog(ControlLayout layout, ViewGroup parent) {
        this.layout = layout;
        Context context = layout.getContext();
        this.menuView = LayoutInflater.from(context).inflate(R.layout.view_control_menu, parent, false);
        parent.addView(menuView);

        setupButtons();
        hide();
    }

    public View getMenuView() {
        return menuView;
    }

    private void setupButtons() {
        Button btnAddButton = menuView.findViewById(R.id.menu_add_button);
        Button btnAddDrawer = menuView.findViewById(R.id.menu_add_drawer);
        Button btnAddJoystick = menuView.findViewById(R.id.menu_add_joystick);
        Button btnSave = menuView.findViewById(R.id.menu_save);
        Button btnLoad = menuView.findViewById(R.id.menu_load);
        Button btnReset = menuView.findViewById(R.id.menu_reset_default);
        Button btnSaveAndExit = menuView.findViewById(R.id.menu_save_and_exit);
        Button btnExit = menuView.findViewById(R.id.menu_exit_without_saving);

        btnAddButton.setOnClickListener(v -> {
            ControlData newBtn = new ControlData("BTN");
            newBtn.dynamicX = "${screen_width} / 2 - ${width} / 2";
            newBtn.dynamicY = "${screen_height} / 2 - ${height} / 2";
            layout.addControlButton(newBtn);
            hide();
            Toast.makeText(layout.getContext(), "Button added at center of screen", Toast.LENGTH_SHORT).show();
        });

        btnAddDrawer.setOnClickListener(v -> {
            ControlData drawerBtn = new ControlData("DRAWER");
            drawerBtn.dynamicX = "${screen_width} / 2 - ${width} / 2";
            drawerBtn.dynamicY = "${screen_height} / 2 - ${height} / 2";
            ControlDrawerData drawerData = new ControlDrawerData(drawerBtn);
            layout.addDrawer(drawerData);
            hide();
            Toast.makeText(layout.getContext(), "Drawer added at center of screen", Toast.LENGTH_SHORT).show();
        });

        btnAddJoystick.setOnClickListener(v -> {
            ControlJoystickData stick = new ControlJoystickData(ControlJoystickData.JOYSTICK_MODE_WASD);
            stick.dynamicX = "${screen_width} / 2 - ${width} / 2";
            stick.dynamicY = "${screen_height} / 2 - ${height} / 2";
            layout.addJoystickButton(stick);
            hide();
            Toast.makeText(layout.getContext(), "Joystick added at center of screen", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> {
            layout.saveLayout();
            Toast.makeText(layout.getContext(), "Layout saved!", Toast.LENGTH_SHORT).show();
        });

        btnLoad.setOnClickListener(v -> {
            layout.loadLayoutFromPreferences();
            hide();
            Toast.makeText(layout.getContext(), "Layout loaded!", Toast.LENGTH_SHORT).show();
        });

        btnReset.setOnClickListener(v -> {
            layout.loadDefaultLayout();
            hide();
            Toast.makeText(layout.getContext(), "Reset to default PC gaming controls!", Toast.LENGTH_SHORT).show();
        });

        btnSaveAndExit.setOnClickListener(v -> {
            layout.saveLayout();
            layout.setModifiable(false);
            hide();
            Toast.makeText(layout.getContext(), "Controls saved & exited editor", Toast.LENGTH_SHORT).show();
        });

        btnExit.setOnClickListener(v -> {
            layout.loadLayoutFromPreferences();
            layout.setModifiable(false);
            hide();
            Toast.makeText(layout.getContext(), "Exited editor without saving", Toast.LENGTH_SHORT).show();
        });
    }

    public void show() {
        menuView.setVisibility(View.VISIBLE);
        menuView.bringToFront();
    }

    public void hide() {
        menuView.setVisibility(View.GONE);
    }

    public boolean isShowing() {
        return menuView.getVisibility() == View.VISIBLE;
    }
}
