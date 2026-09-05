package com.limelight.binding.input.customcontrols;

import com.limelight.R;
import com.limelight.binding.input.customcontrols.utils.ControlTools;
import com.limelight.binding.input.customcontrols.utils.JSONUtils;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlData {
    public static final int KEYCODE_NONE = 0;

    // Special Actions
    public static final int SPECIALBTN_KEYBOARD = -1;
    public static final int SPECIALBTN_TOGGLECTRL = -2;
    public static final int SPECIALBTN_MOUSEPRI = -3;
    public static final int SPECIALBTN_MOUSESEC = -4;
    public static final int SPECIALBTN_VIRTUALMOUSE = -5;
    public static final int SPECIALBTN_MOUSEMID = -6;
    public static final int SPECIALBTN_SCROLLUP = -7;
    public static final int SPECIALBTN_SCROLLDOWN = -8;
    public static final int SPECIALBTN_MENU = -9;

    // Gamepad Buttons
    public static final int SPECIALBTN_GAMEPAD_A = -20;
    public static final int SPECIALBTN_GAMEPAD_B = -21;
    public static final int SPECIALBTN_GAMEPAD_X = -22;
    public static final int SPECIALBTN_GAMEPAD_Y = -23;
    public static final int SPECIALBTN_GAMEPAD_DPAD_UP = -24;
    public static final int SPECIALBTN_GAMEPAD_DPAD_DOWN = -25;
    public static final int SPECIALBTN_GAMEPAD_DPAD_LEFT = -26;
    public static final int SPECIALBTN_GAMEPAD_DPAD_RIGHT = -27;
    public static final int SPECIALBTN_GAMEPAD_LB = -28;
    public static final int SPECIALBTN_GAMEPAD_RB = -29;
    public static final int SPECIALBTN_GAMEPAD_LT = -30;
    public static final int SPECIALBTN_GAMEPAD_RT = -31;
    public static final int SPECIALBTN_GAMEPAD_LS = -32;
    public static final int SPECIALBTN_GAMEPAD_RS = -33;
    public static final int SPECIALBTN_GAMEPAD_START = -34;
    public static final int SPECIALBTN_GAMEPAD_SELECT = -35;

    public transient boolean isHideable = true;
    public String dynamicX, dynamicY;
    public boolean isToggle;
    public boolean passThruEnabled;
    public String name;
    public int[] keycodes;
    public float opacity = 0.9f;
    public int bgColor = 0x66000000;
    public int strokeColor = 0xFFFFFFFF;
    public float strokeWidth = 1f; // in dp
    public float cornerRadius = 25f; // 0-100%
    public boolean isSwipeable = false;
    private float width = 60f; // in dp
    private float height = 45f; // in dp

    public ControlData() {
        this("BTN", new int[]{KEYCODE_NONE});
    }

    public ControlData(String name) {
        this(name, new int[]{KEYCODE_NONE});
    }

    public ControlData(String name, int[] keycodes) {
        this(name, keycodes, "${screen_width} / 2 - ${width} / 2", "${screen_height} / 2 - ${height} / 2");
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY) {
        this(name, keycodes, dynamicX, dynamicY, 60f, 45f);
    }

    public ControlData(String name, int[] keycodes, String dynamicX, String dynamicY, float width, float height) {
        this.name = name;
        this.keycodes = inflateKeycodes(keycodes);
        this.dynamicX = dynamicX;
        this.dynamicY = dynamicY;
        this.width = width;
        this.height = height;
    }

    // Copy constructor
    public ControlData(ControlData other) {
        this.name = other.name;
        this.keycodes = other.keycodes != null ? other.keycodes.clone() : new int[]{0, 0, 0, 0};
        this.dynamicX = other.dynamicX;
        this.dynamicY = other.dynamicY;
        this.width = other.width;
        this.height = other.height;
        this.isToggle = other.isToggle;
        this.passThruEnabled = other.passThruEnabled;
        this.isSwipeable = other.isSwipeable;
        this.opacity = other.opacity;
        this.bgColor = other.bgColor;
        this.strokeColor = other.strokeColor;
        this.strokeWidth = other.strokeWidth;
        this.cornerRadius = other.cornerRadius;
    }

    private static int[] inflateKeycodes(int[] in) {
        int[] out = new int[]{0, 0, 0, 0};
        if (in != null) {
            System.arraycopy(in, 0, out, 0, Math.min(in.length, 4));
        }
        return out;
    }

    public float getWidth() {
        return ControlTools.dpToPx(width);
    }

    public void setWidth(float px) {
        this.width = Math.max(20f, ControlTools.pxToDp(px));
    }

    public float getHeight() {
        return ControlTools.dpToPx(height);
    }

    public void setHeight(float px) {
        this.height = Math.max(20f, ControlTools.pxToDp(px));
    }

    public float getWidthDp() {
        return width;
    }

    public float getHeightDp() {
        return height;
    }

    public void setWidthDp(float dp) {
        this.width = Math.max(20f, dp);
    }

    public void setHeightDp(float dp) {
        this.height = Math.max(20f, dp);
    }

    public boolean containsKeycode(int code) {
        if (keycodes == null) return false;
        for (int k : keycodes) {
            if (k == code) return true;
        }
        return false;
    }

    public float insertDynamicPos(String dynamicPos) {
        if (dynamicPos == null || dynamicPos.isEmpty()) {
            return 0;
        }
        int screenW = ControlTools.getScreenWidth();
        int screenH = ControlTools.getScreenHeight();

        Map<String, String> map = new HashMap<>();
        map.put("screen_width", Integer.toString(screenW));
        map.put("screen_height", Integer.toString(screenH));
        map.put("width", Float.toString(getWidth()));
        map.put("height", Float.toString(getHeight()));
        map.put("margin", Float.toString(ControlTools.dpToPx(4)));
        map.put("right", Float.toString(screenW - getWidth()));
        map.put("bottom", Float.toString(screenH - getHeight()));
        map.put("left", "0");
        map.put("top", "0");

        String inserted = JSONUtils.insertSingleJSONValue(dynamicPos, map);
        try {
            return (float) new ExpressionBuilder(inserted)
                    .function(new Function("dp", 1) {
                        @Override
                        public double apply(double... args) {
                            return ControlTools.dpToPx((float) args[0]);
                        }
                    })
                    .function(new Function("px", 1) {
                        @Override
                        public double apply(double... args) {
                            return ControlTools.pxToDp((float) args[0]);
                        }
                    })
                    .build()
                    .evaluate();
        } catch (Exception e) {
            try {
                return Float.parseFloat(inserted);
            } catch (Exception ignored) {
                return 0f;
            }
        }
    }
}
