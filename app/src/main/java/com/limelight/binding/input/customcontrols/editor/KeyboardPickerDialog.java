package com.limelight.binding.input.customcontrols.editor;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.limelight.R;
import com.limelight.binding.input.customcontrols.ControlData;
import com.limelight.binding.input.customcontrols.utils.ControlTools;

import java.util.HashMap;
import java.util.Map;

public class KeyboardPickerDialog extends Dialog {

    public interface OnKeyPickedListener {
        void onKeyPicked(int keycode, String label);
    }

    private OnKeyPickedListener onKeyPickedListener;
    private final Map<Integer, KeyInfo> keyMap = new HashMap<>();

    public static class KeyInfo {
        public final int keycode;
        public final String label;

        public KeyInfo(int keycode, String label) {
            this.keycode = keycode;
            this.label = label;
        }
    }

    public KeyboardPickerDialog(Context context) {
        super(context, R.style.CustomDialogStyle);
    }

    public KeyboardPickerDialog setOnKeyPickedListener(OnKeyPickedListener listener) {
        this.onKeyPickedListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_keyboard);

        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.getAttributes().layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );

            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, dm);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = dm.widthPixels - (margin * 2);
            params.height = dm.heightPixels - (margin * 2);
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }

        View btnCloseX = findViewById(R.id.close);
        if (btnCloseX != null) {
            btnCloseX.setOnClickListener(v -> dismiss());
        }

        View btnCloseBottom = findViewById(R.id.close1);
        if (btnCloseBottom != null) {
            btnCloseBottom.setOnClickListener(v -> dismiss());
        }

        View btnSend = findViewById(R.id.send);
        if (btnSend != null) {
            btnSend.setVisibility(View.GONE);
        }

        buildKeyMap();
        bindButtons();
        setupSpecialButtons();
    }

    private void buildKeyMap() {
        // Esc and Function Keys
        keyMap.put(R.id.keyboard_esc, new KeyInfo(KeyEvent.KEYCODE_ESCAPE, "ESC"));
        keyMap.put(R.id.keyboard_f1, new KeyInfo(KeyEvent.KEYCODE_F1, "F1"));
        keyMap.put(R.id.keyboard_f2, new KeyInfo(KeyEvent.KEYCODE_F2, "F2"));
        keyMap.put(R.id.keyboard_f3, new KeyInfo(KeyEvent.KEYCODE_F3, "F3"));
        keyMap.put(R.id.keyboard_f4, new KeyInfo(KeyEvent.KEYCODE_F4, "F4"));
        keyMap.put(R.id.keyboard_f5, new KeyInfo(KeyEvent.KEYCODE_F5, "F5"));
        keyMap.put(R.id.keyboard_f6, new KeyInfo(KeyEvent.KEYCODE_F6, "F6"));
        keyMap.put(R.id.keyboard_f7, new KeyInfo(KeyEvent.KEYCODE_F7, "F7"));
        keyMap.put(R.id.keyboard_f8, new KeyInfo(KeyEvent.KEYCODE_F8, "F8"));
        keyMap.put(R.id.keyboard_f9, new KeyInfo(KeyEvent.KEYCODE_F9, "F9"));
        keyMap.put(R.id.keyboard_f10, new KeyInfo(KeyEvent.KEYCODE_F10, "F10"));
        keyMap.put(R.id.keyboard_f11, new KeyInfo(KeyEvent.KEYCODE_F11, "F11"));
        keyMap.put(R.id.keyboard_f12, new KeyInfo(KeyEvent.KEYCODE_F12, "F12"));

        // Number row
        keyMap.put(R.id.keyboard_grave, new KeyInfo(KeyEvent.KEYCODE_GRAVE, "`"));
        keyMap.put(R.id.keyboard_1, new KeyInfo(KeyEvent.KEYCODE_1, "1"));
        keyMap.put(R.id.keyboard_2, new KeyInfo(KeyEvent.KEYCODE_2, "2"));
        keyMap.put(R.id.keyboard_3, new KeyInfo(KeyEvent.KEYCODE_3, "3"));
        keyMap.put(R.id.keyboard_4, new KeyInfo(KeyEvent.KEYCODE_4, "4"));
        keyMap.put(R.id.keyboard_5, new KeyInfo(KeyEvent.KEYCODE_5, "5"));
        keyMap.put(R.id.keyboard_6, new KeyInfo(KeyEvent.KEYCODE_6, "6"));
        keyMap.put(R.id.keyboard_7, new KeyInfo(KeyEvent.KEYCODE_7, "7"));
        keyMap.put(R.id.keyboard_8, new KeyInfo(KeyEvent.KEYCODE_8, "8"));
        keyMap.put(R.id.keyboard_9, new KeyInfo(KeyEvent.KEYCODE_9, "9"));
        keyMap.put(R.id.keyboard_0, new KeyInfo(KeyEvent.KEYCODE_0, "0"));
        keyMap.put(R.id.keyboard_minus, new KeyInfo(KeyEvent.KEYCODE_MINUS, "-"));
        keyMap.put(R.id.keyboard_equals, new KeyInfo(KeyEvent.KEYCODE_EQUALS, "="));
        keyMap.put(R.id.keyboard_backspace, new KeyInfo(KeyEvent.KEYCODE_DEL, "Backspace"));

        // QWERTY row
        keyMap.put(R.id.keyboard_tab, new KeyInfo(KeyEvent.KEYCODE_TAB, "Tab"));
        keyMap.put(R.id.keyboard_q, new KeyInfo(KeyEvent.KEYCODE_Q, "Q"));
        keyMap.put(R.id.keyboard_w, new KeyInfo(KeyEvent.KEYCODE_W, "W"));
        keyMap.put(R.id.keyboard_e, new KeyInfo(KeyEvent.KEYCODE_E, "E"));
        keyMap.put(R.id.keyboard_r, new KeyInfo(KeyEvent.KEYCODE_R, "R"));
        keyMap.put(R.id.keyboard_t, new KeyInfo(KeyEvent.KEYCODE_T, "T"));
        keyMap.put(R.id.keyboard_y, new KeyInfo(KeyEvent.KEYCODE_Y, "Y"));
        keyMap.put(R.id.keyboard_u, new KeyInfo(KeyEvent.KEYCODE_U, "U"));
        keyMap.put(R.id.keyboard_i, new KeyInfo(KeyEvent.KEYCODE_I, "I"));
        keyMap.put(R.id.keyboard_o, new KeyInfo(KeyEvent.KEYCODE_O, "O"));
        keyMap.put(R.id.keyboard_p, new KeyInfo(KeyEvent.KEYCODE_P, "P"));
        keyMap.put(R.id.keyboard_left_bracket, new KeyInfo(KeyEvent.KEYCODE_LEFT_BRACKET, "["));
        keyMap.put(R.id.keyboard_right_bracket, new KeyInfo(KeyEvent.KEYCODE_RIGHT_BRACKET, "]"));
        keyMap.put(R.id.keyboard_backslash, new KeyInfo(KeyEvent.KEYCODE_BACKSLASH, "\\"));

        // ASDF row
        keyMap.put(R.id.keyboard_capslock, new KeyInfo(KeyEvent.KEYCODE_CAPS_LOCK, "CAPS"));
        keyMap.put(R.id.keyboard_a, new KeyInfo(KeyEvent.KEYCODE_A, "A"));
        keyMap.put(R.id.keyboard_s, new KeyInfo(KeyEvent.KEYCODE_S, "S"));
        keyMap.put(R.id.keyboard_d, new KeyInfo(KeyEvent.KEYCODE_D, "D"));
        keyMap.put(R.id.keyboard_f, new KeyInfo(KeyEvent.KEYCODE_F, "F"));
        keyMap.put(R.id.keyboard_g, new KeyInfo(KeyEvent.KEYCODE_G, "G"));
        keyMap.put(R.id.keyboard_h, new KeyInfo(KeyEvent.KEYCODE_H, "H"));
        keyMap.put(R.id.keyboard_j, new KeyInfo(KeyEvent.KEYCODE_J, "J"));
        keyMap.put(R.id.keyboard_k, new KeyInfo(KeyEvent.KEYCODE_K, "K"));
        keyMap.put(R.id.keyboard_l, new KeyInfo(KeyEvent.KEYCODE_L, "L"));
        keyMap.put(R.id.keyboard_semicolon, new KeyInfo(KeyEvent.KEYCODE_SEMICOLON, ";"));
        keyMap.put(R.id.keyboard_apostrophe, new KeyInfo(KeyEvent.KEYCODE_APOSTROPHE, "'"));
        keyMap.put(R.id.keyboard_enter, new KeyInfo(KeyEvent.KEYCODE_ENTER, "Enter"));

        // ZXCV row
        keyMap.put(R.id.keyboard_left_shift, new KeyInfo(KeyEvent.KEYCODE_SHIFT_LEFT, "LShift"));
        keyMap.put(R.id.keyboard_z, new KeyInfo(KeyEvent.KEYCODE_Z, "Z"));
        keyMap.put(R.id.keyboard_x, new KeyInfo(KeyEvent.KEYCODE_X, "X"));
        keyMap.put(R.id.keyboard_c, new KeyInfo(KeyEvent.KEYCODE_C, "C"));
        keyMap.put(R.id.keyboard_v, new KeyInfo(KeyEvent.KEYCODE_V, "V"));
        keyMap.put(R.id.keyboard_b, new KeyInfo(KeyEvent.KEYCODE_B, "B"));
        keyMap.put(R.id.keyboard_n, new KeyInfo(KeyEvent.KEYCODE_N, "N"));
        keyMap.put(R.id.keyboard_m, new KeyInfo(KeyEvent.KEYCODE_M, "M"));
        keyMap.put(R.id.keyboard_comma, new KeyInfo(KeyEvent.KEYCODE_COMMA, ","));
        keyMap.put(R.id.keyboard_period, new KeyInfo(KeyEvent.KEYCODE_PERIOD, "."));
        keyMap.put(R.id.keyboard_slash, new KeyInfo(KeyEvent.KEYCODE_SLASH, "/"));
        keyMap.put(R.id.keyboard_right_shift, new KeyInfo(KeyEvent.KEYCODE_SHIFT_RIGHT, "RShift"));

        // Bottom row
        keyMap.put(R.id.keyboard_left_ctrl, new KeyInfo(KeyEvent.KEYCODE_CTRL_LEFT, "LCtrl"));
        keyMap.put(R.id.keyboard_left_alt, new KeyInfo(KeyEvent.KEYCODE_ALT_LEFT, "LAlt"));
        keyMap.put(R.id.keyboard_space, new KeyInfo(KeyEvent.KEYCODE_SPACE, "Space"));
        keyMap.put(R.id.keyboard_right_alt, new KeyInfo(KeyEvent.KEYCODE_ALT_RIGHT, "RAlt"));
        keyMap.put(R.id.keyboard_right_ctrl, new KeyInfo(KeyEvent.KEYCODE_CTRL_RIGHT, "RCtrl"));

        // Navigation cluster
        keyMap.put(R.id.keyboard_pause, new KeyInfo(KeyEvent.KEYCODE_BREAK, "Pause"));
        keyMap.put(R.id.keyboard_insert, new KeyInfo(KeyEvent.KEYCODE_INSERT, "Insert"));
        keyMap.put(R.id.keyboard_home, new KeyInfo(KeyEvent.KEYCODE_MOVE_HOME, "Home"));
        keyMap.put(R.id.keyboard_end, new KeyInfo(KeyEvent.KEYCODE_MOVE_END, "End"));
        keyMap.put(R.id.keyboard_page_up, new KeyInfo(KeyEvent.KEYCODE_PAGE_UP, "PgUp"));
        keyMap.put(R.id.keyboard_page_down, new KeyInfo(KeyEvent.KEYCODE_PAGE_DOWN, "PgDn"));

        // Arrow keys
        keyMap.put(R.id.keyboard_up, new KeyInfo(KeyEvent.KEYCODE_DPAD_UP, "▲"));
        keyMap.put(R.id.keyboard_left, new KeyInfo(KeyEvent.KEYCODE_DPAD_LEFT, "◀"));
        keyMap.put(R.id.keyboard_down, new KeyInfo(KeyEvent.KEYCODE_DPAD_DOWN, "▼"));
        keyMap.put(R.id.keyboard_right, new KeyInfo(KeyEvent.KEYCODE_DPAD_RIGHT, "▶"));

        // Numpad
        keyMap.put(R.id.keyboard_num_lock, new KeyInfo(KeyEvent.KEYCODE_NUM_LOCK, "NumLock"));
        keyMap.put(R.id.keyboard_kp_divide, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_DIVIDE, "KP /"));
        keyMap.put(R.id.keyboard_kp_multiply, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, "KP *"));
        keyMap.put(R.id.keyboard_kp_subract, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, "KP -"));
        keyMap.put(R.id.keyboard_kp_add, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_ADD, "KP +"));
        keyMap.put(R.id.keyboard_kp_enter, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_ENTER, "KP Enter"));
        keyMap.put(R.id.keyboard_kp_decimal, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_DOT, "KP ."));
        keyMap.put(R.id.keyboard_kp_0, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_0, "KP 0"));
        keyMap.put(R.id.keyboard_kp_1, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_1, "KP 1"));
        keyMap.put(R.id.keyboard_kp_2, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_2, "KP 2"));
        keyMap.put(R.id.keyboard_kp_3, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_3, "KP 3"));
        keyMap.put(R.id.keyboard_kp_4, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_4, "KP 4"));
        keyMap.put(R.id.keyboard_kp_5, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_5, "KP 5"));
        keyMap.put(R.id.keyboard_kp_6, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_6, "KP 6"));
        keyMap.put(R.id.keyboard_kp_7, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_7, "KP 7"));
        keyMap.put(R.id.keyboard_kp_8, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_8, "KP 8"));
        keyMap.put(R.id.keyboard_kp_9, new KeyInfo(KeyEvent.KEYCODE_NUMPAD_9, "KP 9"));
    }

    private void bindButtons() {
        for (Map.Entry<Integer, KeyInfo> entry : keyMap.entrySet()) {
            View view = findViewById(entry.getKey());
            if (view != null) {
                KeyInfo info = entry.getValue();
                view.setOnClickListener(v -> selectKey(info.keycode, info.label));
            }
        }
    }

    private void setupSpecialButtons() {
        LinearLayout specialContainer = findViewById(R.id.special_key);
        if (specialContainer == null) return;
        specialContainer.removeAllViews();

        KeyInfo[][] rows = new KeyInfo[][]{
                // Row 1: General & Mouse Actions
                new KeyInfo[]{
                        new KeyInfo(ControlData.KEYCODE_NONE, "Unspecified (None)"),
                        new KeyInfo(ControlData.SPECIALBTN_MOUSEPRI, "Left Click"),
                        new KeyInfo(ControlData.SPECIALBTN_MOUSESEC, "Right Click"),
                        new KeyInfo(ControlData.SPECIALBTN_MOUSEMID, "Middle Click"),
                        new KeyInfo(ControlData.SPECIALBTN_SCROLLUP, "Scroll Up"),
                        new KeyInfo(ControlData.SPECIALBTN_SCROLLDOWN, "Scroll Down"),
                        new KeyInfo(ControlData.SPECIALBTN_VIRTUALMOUSE, "Mouse Mode"),
                        new KeyInfo(ControlData.SPECIALBTN_KEYBOARD, "Keyboard"),
                        new KeyInfo(ControlData.SPECIALBTN_MENU, "Menu")
                },
                // Row 2: Gamepad Controls
                new KeyInfo[]{
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_A, "GP: A"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_B, "GP: B"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_X, "GP: X"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_Y, "GP: Y"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_LB, "GP: LB"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_RB, "GP: RB"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_LT, "GP: LT"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_RT, "GP: RT"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_LS, "GP: L3"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_RS, "GP: R3"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_START, "GP: Start"),
                        new KeyInfo(ControlData.SPECIALBTN_GAMEPAD_SELECT, "GP: Select")
                }
        };

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
        int padH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
        int padV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
        int btnHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, dm);

        for (KeyInfo[] row : rows) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            for (KeyInfo key : row) {
                Button btn = new Button(getContext());
                btn.setText(key.label);
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                btn.setTextColor(0xFFFFFFFF);
                btn.setBackgroundResource(R.drawable.keyboard_key_background);
                btn.setPadding(padH, padV, padH, padV);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        btnHeight
                );
                lp.setMargins(margin, margin, margin, margin);
                btn.setLayoutParams(lp);

                btn.setOnClickListener(v -> selectKey(key.keycode, key.label));
                rowLayout.addView(btn);
            }
            specialContainer.addView(rowLayout);
        }
    }

    private void selectKey(int keycode, String label) {
        if (onKeyPickedListener != null) {
            onKeyPickedListener.onKeyPicked(keycode, label);
        }
        dismiss();
    }

    public static String getKeyLabel(int keycode) {
        switch (keycode) {
            case ControlData.KEYCODE_NONE: return "(None)";
            case ControlData.SPECIALBTN_KEYBOARD: return "Keyboard";
            case ControlData.SPECIALBTN_TOGGLECTRL: return "Toggle Ctrl";
            case ControlData.SPECIALBTN_MOUSEPRI: return "Left Click";
            case ControlData.SPECIALBTN_MOUSESEC: return "Right Click";
            case ControlData.SPECIALBTN_MOUSEMID: return "Middle Click";
            case ControlData.SPECIALBTN_SCROLLUP: return "Scroll Up";
            case ControlData.SPECIALBTN_SCROLLDOWN: return "Scroll Down";
            case ControlData.SPECIALBTN_VIRTUALMOUSE: return "Mouse Mode";
            case ControlData.SPECIALBTN_MENU: return "Menu";
            case ControlData.SPECIALBTN_GAMEPAD_A: return "GP: A";
            case ControlData.SPECIALBTN_GAMEPAD_B: return "GP: B";
            case ControlData.SPECIALBTN_GAMEPAD_X: return "GP: X";
            case ControlData.SPECIALBTN_GAMEPAD_Y: return "GP: Y";
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_UP: return "GP: Up";
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_DOWN: return "GP: Down";
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_LEFT: return "GP: Left";
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_RIGHT: return "GP: Right";
            case ControlData.SPECIALBTN_GAMEPAD_LB: return "GP: LB";
            case ControlData.SPECIALBTN_GAMEPAD_RB: return "GP: RB";
            case ControlData.SPECIALBTN_GAMEPAD_LT: return "GP: LT";
            case ControlData.SPECIALBTN_GAMEPAD_RT: return "GP: RT";
            case ControlData.SPECIALBTN_GAMEPAD_LS: return "GP: L3";
            case ControlData.SPECIALBTN_GAMEPAD_RS: return "GP: R3";
            case ControlData.SPECIALBTN_GAMEPAD_START: return "GP: Start";
            case ControlData.SPECIALBTN_GAMEPAD_SELECT: return "GP: Select";
            case KeyEvent.KEYCODE_ESCAPE: return "ESC";
            case KeyEvent.KEYCODE_SPACE: return "SPACE";
            case KeyEvent.KEYCODE_ENTER: return "ENTER";
            case KeyEvent.KEYCODE_TAB: return "TAB";
            case KeyEvent.KEYCODE_DEL: return "BACKSPACE";
            case KeyEvent.KEYCODE_SHIFT_LEFT: return "L-SHIFT";
            case KeyEvent.KEYCODE_SHIFT_RIGHT: return "R-SHIFT";
            case KeyEvent.KEYCODE_CTRL_LEFT: return "L-CTRL";
            case KeyEvent.KEYCODE_CTRL_RIGHT: return "R-CTRL";
            case KeyEvent.KEYCODE_ALT_LEFT: return "L-ALT";
            case KeyEvent.KEYCODE_ALT_RIGHT: return "R-ALT";
            case KeyEvent.KEYCODE_DPAD_UP: return "▲ UP";
            case KeyEvent.KEYCODE_DPAD_DOWN: return "▼ DOWN";
            case KeyEvent.KEYCODE_DPAD_LEFT: return "◀ LEFT";
            case KeyEvent.KEYCODE_DPAD_RIGHT: return "▶ RIGHT";
            default:
                if (keycode >= KeyEvent.KEYCODE_A && keycode <= KeyEvent.KEYCODE_Z) {
                    return String.valueOf((char) ('A' + (keycode - KeyEvent.KEYCODE_A)));
                }
                if (keycode >= KeyEvent.KEYCODE_0 && keycode <= KeyEvent.KEYCODE_9) {
                    return String.valueOf((char) ('0' + (keycode - KeyEvent.KEYCODE_0)));
                }
                if (keycode >= KeyEvent.KEYCODE_F1 && keycode <= KeyEvent.KEYCODE_F12) {
                    return "F" + (keycode - KeyEvent.KEYCODE_F1 + 1);
                }
                return "Key: " + keycode;
        }
    }
}
