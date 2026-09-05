package com.limelight.binding.input.customcontrols;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomControls {
    public static final int CURRENT_VERSION = 1;
    public static final String PREF_CUSTOM_CONTROLS_JSON = "custom_controls_layout_json";
    public static final String PREF_CONTROLS_FILE = "moonlight_controls";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int version = CURRENT_VERSION;
    public String layoutName = "Default";
    public List<ControlData> mControlDataList = new ArrayList<>();
    public List<ControlDrawerData> mDrawerDataList = new ArrayList<>();
    public List<ControlJoystickData> mJoystickDataList = new ArrayList<>();

    public CustomControls() {
    }

    public static CustomControls createDefaultLayout() {
        CustomControls layout = new CustomControls();
        layout.layoutName = "Default PC Gaming";

        // Virtual Joystick for WASD (bottom left)
        ControlJoystickData wasdStick = new ControlJoystickData(ControlJoystickData.JOYSTICK_MODE_WASD);
        wasdStick.dynamicX = "${margin} * 4";
        wasdStick.dynamicY = "${screen_height} - ${height} - ${margin} * 4";
        wasdStick.setWidthDp(130f);
        wasdStick.setHeightDp(130f);
        layout.mJoystickDataList.add(wasdStick);

        // Escape Button (top left)
        ControlData escBtn = new ControlData("ESC", new int[]{KeyEvent.KEYCODE_ESCAPE});
        escBtn.dynamicX = "${margin} * 2";
        escBtn.dynamicY = "${margin} * 2";
        escBtn.setWidthDp(55f);
        escBtn.setHeightDp(35f);
        layout.mControlDataList.add(escBtn);

        // Tab Button (below ESC)
        ControlData tabBtn = new ControlData("TAB", new int[]{KeyEvent.KEYCODE_TAB});
        tabBtn.dynamicX = "${margin} * 2";
        tabBtn.dynamicY = "${margin} * 4 + 35";
        tabBtn.setWidthDp(55f);
        tabBtn.setHeightDp(35f);
        layout.mControlDataList.add(tabBtn);

        // Toggle Keyboard button (top center-left)
        ControlData kbBtn = new ControlData("KEYBOARD", new int[]{ControlData.SPECIALBTN_KEYBOARD});
        kbBtn.dynamicX = "${screen_width} * 0.3 - ${width} / 2";
        kbBtn.dynamicY = "${margin} * 2";
        kbBtn.setWidthDp(75f);
        kbBtn.setHeightDp(35f);
        layout.mControlDataList.add(kbBtn);

        // Toggle Controls visibility (top center-right)
        ControlData guiBtn = new ControlData("HIDE UI", new int[]{ControlData.SPECIALBTN_TOGGLECTRL});
        guiBtn.dynamicX = "${screen_width} * 0.7 - ${width} / 2";
        guiBtn.dynamicY = "${margin} * 2";
        guiBtn.setWidthDp(75f);
        guiBtn.setHeightDp(35f);
        layout.mControlDataList.add(guiBtn);

        // Primary Mouse (Left Click) (bottom right)
        ControlData leftClick = new ControlData("L-CLICK", new int[]{ControlData.SPECIALBTN_MOUSEPRI});
        leftClick.dynamicX = "${right} - ${margin} * 4";
        leftClick.dynamicY = "${screen_height} - ${height} - ${margin} * 4";
        leftClick.setWidthDp(75f);
        leftClick.setHeightDp(65f);
        leftClick.bgColor = 0x882A303C;
        leftClick.strokeColor = 0xFF4D90FE;
        leftClick.strokeWidth = 2f;
        layout.mControlDataList.add(leftClick);

        // Secondary Mouse (Right Click) (above left click)
        ControlData rightClick = new ControlData("R-CLICK", new int[]{ControlData.SPECIALBTN_MOUSESEC});
        rightClick.dynamicX = "${right} - ${margin} * 4";
        rightClick.dynamicY = "${screen_height} - ${height} * 2 - ${margin} * 8";
        rightClick.setWidthDp(75f);
        rightClick.setHeightDp(60f);
        rightClick.bgColor = 0x882A303C;
        rightClick.strokeColor = 0xFF4D90FE;
        layout.mControlDataList.add(rightClick);

        // Space / Jump (to the left of left click)
        ControlData spaceBtn = new ControlData("SPACE", new int[]{KeyEvent.KEYCODE_SPACE});
        spaceBtn.dynamicX = "${right} - ${width} - 85 - ${margin} * 4";
        spaceBtn.dynamicY = "${screen_height} - ${height} - ${margin} * 4";
        spaceBtn.setWidthDp(75f);
        spaceBtn.setHeightDp(60f);
        layout.mControlDataList.add(spaceBtn);

        // Shift / Sneak (Toggle mode) (above space)
        ControlData shiftBtn = new ControlData("SHIFT", new int[]{KeyEvent.KEYCODE_SHIFT_LEFT});
        shiftBtn.dynamicX = "${right} - ${width} - 85 - ${margin} * 4";
        shiftBtn.dynamicY = "${screen_height} - ${height} * 2 - ${margin} * 8";
        shiftBtn.setWidthDp(75f);
        shiftBtn.setHeightDp(50f);
        shiftBtn.isToggle = true;
        layout.mControlDataList.add(shiftBtn);

        // E / Inventory (above shift)
        ControlData eBtn = new ControlData("E", new int[]{KeyEvent.KEYCODE_E});
        eBtn.dynamicX = "${right} - ${width} - 85 - ${margin} * 4";
        eBtn.dynamicY = "${screen_height} - ${height} * 3 - ${margin} * 12";
        eBtn.setWidthDp(55f);
        eBtn.setHeightDp(45f);
        layout.mControlDataList.add(eBtn);

        // F5 button (top right)
        ControlData f5Btn = new ControlData("F5", new int[]{KeyEvent.KEYCODE_F5});
        f5Btn.dynamicX = "${right} - ${margin} * 2";
        f5Btn.dynamicY = "${margin} * 2";
        f5Btn.setWidthDp(55f);
        f5Btn.setHeightDp(35f);
        layout.mControlDataList.add(f5Btn);

        // F3 button (next to F5)
        ControlData f3Btn = new ControlData("F3", new int[]{KeyEvent.KEYCODE_F3});
        f3Btn.dynamicX = "${right} - ${width} - 60 - ${margin} * 2";
        f3Btn.dynamicY = "${margin} * 2";
        f3Btn.setWidthDp(55f);
        f3Btn.setHeightDp(35f);
        layout.mControlDataList.add(f3Btn);

        // Hotbar Drawer (1-9 number keys) at bottom center
        ControlDrawerData hotbarDrawer = new ControlDrawerData(
                new ControlData("HOTBAR", new int[]{ControlData.KEYCODE_NONE}, "${screen_width} / 2 - ${width} / 2", "${bottom} - ${margin} * 2", 70f, 35f)
        );
        hotbarDrawer.orientation = ControlDrawerData.Orientation.UP;
        int[] hotbarKeys = new int[]{
                KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9
        };
        for (int i = 0; i < hotbarKeys.length; i++) {
            ControlData numBtn = new ControlData(Integer.toString(i + 1), new int[]{hotbarKeys[i]});
            numBtn.setWidthDp(40f);
            numBtn.setHeightDp(35f);
            hotbarDrawer.buttonProperties.add(numBtn);
        }
        layout.mDrawerDataList.add(hotbarDrawer);

        return layout;
    }

    public static CustomControls loadFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_CONTROLS_FILE, Context.MODE_PRIVATE);
        String json = prefs.getString(PREF_CUSTOM_CONTROLS_JSON, null);
        if (json != null && !json.trim().isEmpty()) {
            try {
                CustomControls controls = GSON.fromJson(json, CustomControls.class);
                if (controls != null && controls.mControlDataList != null) {
                    return controls;
                }
            } catch (Exception ignored) {
            }
        }
        return createDefaultLayout();
    }

    public void saveToPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_CONTROLS_FILE, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_CUSTOM_CONTROLS_JSON, GSON.toJson(this)).apply();
    }

    public static CustomControls loadFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, CustomControls.class);
        }
    }

    public void saveToFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        }
    }
}
