package com.limelight.binding.input.customcontrols;

import android.content.Context;
import android.content.SharedPreferences;

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
        layout.layoutName = "Default";
        // Empty — user adds buttons from the Settings editor
        return layout;
    }

    public static CustomControls loadFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_CONTROLS_FILE, Context.MODE_PRIVATE);
        // Discard layouts saved by a previous build — they reference buttons/types
        // that may no longer exist. Fresh install = empty layout.
        int savedVersion = prefs.getInt("saved_version", 0);
        if (savedVersion != CURRENT_VERSION) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putInt("saved_version", CURRENT_VERSION);
            editor.apply();
            return createDefaultLayout();
        }
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
