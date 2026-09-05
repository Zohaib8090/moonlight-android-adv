package com.limelight.binding.input.customcontrols;

import android.content.Context;
import com.limelight.R;

import java.util.ArrayList;
import java.util.List;

public class ControlDrawerData {
    public enum Orientation {
        RIGHT,
        LEFT,
        UP,
        DOWN,
        FREE
    }

    public Orientation orientation = Orientation.RIGHT;
    public ControlData properties;
    public List<ControlData> buttonProperties = new ArrayList<>();

    public ControlDrawerData() {
        this(new ControlData("DRAWER"));
    }

    public ControlDrawerData(ControlData properties) {
        this.properties = properties;
        this.properties.isToggle = true; // Drawers are toggled open/closed
    }

    public static List<String> getOrientations(Context ctx) {
        List<String> list = new ArrayList<>();
        list.add(ctx.getString(R.string.orientation_right));
        list.add(ctx.getString(R.string.orientation_left));
        list.add(ctx.getString(R.string.orientation_up));
        list.add(ctx.getString(R.string.orientation_down));
        list.add(ctx.getString(R.string.orientation_free));
        return list;
    }
}
