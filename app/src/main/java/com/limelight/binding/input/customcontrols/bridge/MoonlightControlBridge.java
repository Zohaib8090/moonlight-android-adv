package com.limelight.binding.input.customcontrols.bridge;

import android.content.Context;
import android.view.KeyEvent;

import com.limelight.Game;
import com.limelight.binding.input.ControllerHandler;
import com.limelight.binding.input.KeyboardTranslator;
import com.limelight.binding.input.customcontrols.ControlData;
import com.limelight.nvstream.NvConnection;
import com.limelight.nvstream.input.ControllerPacket;
import com.limelight.nvstream.input.KeyboardPacket;
import com.limelight.nvstream.input.MouseButtonPacket;

public class MoonlightControlBridge {
    private final Game game;
    private final NvConnection conn;
    private final ControllerHandler controllerHandler;
    private final KeyboardTranslator keyboardTranslator;

    // Track active gamepad button flags from on-screen controls
    private int oscGamepadMap = 0;
    private short oscLeftStickX = 0;
    private short oscLeftStickY = 0;
    private short oscRightStickX = 0;
    private short oscRightStickY = 0;
    private byte oscLeftTrigger = 0;
    private byte oscRightTrigger = 0;

    public MoonlightControlBridge(Game game, NvConnection conn, ControllerHandler controllerHandler) {
        this.game = game;
        this.conn = conn;
        this.controllerHandler = controllerHandler;
        this.keyboardTranslator = new KeyboardTranslator();
    }

    public void dispatchAction(int actionCode, boolean isDown) {
        if (actionCode == ControlData.KEYCODE_NONE) {
            return;
        }

        // 1. Special Actions
        if (actionCode < 0 && actionCode >= -9) {
            handleSpecialAction(actionCode, isDown);
            return;
        }

        // 2. Gamepad Actions
        if (actionCode <= -20 && actionCode >= -35) {
            handleGamepadAction(actionCode, isDown);
            return;
        }

        // 3. Keyboard Keys (actionCode is standard Android KeyEvent.KEYCODE_*)
        handleKeyboardAction(actionCode, isDown);
    }

    private void handleSpecialAction(int code, boolean isDown) {
        switch (code) {
            case ControlData.SPECIALBTN_MOUSEPRI:
                if (conn != null) {
                    if (isDown) conn.sendMouseButtonDown(MouseButtonPacket.BUTTON_LEFT);
                    else conn.sendMouseButtonUp(MouseButtonPacket.BUTTON_LEFT);
                }
                break;
            case ControlData.SPECIALBTN_MOUSESEC:
                if (conn != null) {
                    if (isDown) conn.sendMouseButtonDown(MouseButtonPacket.BUTTON_RIGHT);
                    else conn.sendMouseButtonUp(MouseButtonPacket.BUTTON_RIGHT);
                }
                break;
            case ControlData.SPECIALBTN_MOUSEMID:
                if (conn != null) {
                    if (isDown) conn.sendMouseButtonDown(MouseButtonPacket.BUTTON_MIDDLE);
                    else conn.sendMouseButtonUp(MouseButtonPacket.BUTTON_MIDDLE);
                }
                break;
            case ControlData.SPECIALBTN_SCROLLUP:
                if (conn != null && isDown) {
                    conn.sendMouseHighResScroll((short) 120);
                }
                break;
            case ControlData.SPECIALBTN_SCROLLDOWN:
                if (conn != null && isDown) {
                    conn.sendMouseHighResScroll((short) -120);
                }
                break;
            case ControlData.SPECIALBTN_KEYBOARD:
                if (isDown && game != null) {
                    game.toggleKeyboard();
                }
                break;
            case ControlData.SPECIALBTN_TOGGLECTRL:
                // Handled in ControlLayout
                break;
            case ControlData.SPECIALBTN_VIRTUALMOUSE:
                if (isDown && game != null) {
                    game.toggleMouseMode();
                }
                break;
            case ControlData.SPECIALBTN_MENU:
                if (isDown && game != null) {
                    game.openStreamMenu();
                }
                break;
        }
    }

    private void handleGamepadAction(int code, boolean isDown) {
        int flag = 0;
        switch (code) {
            case ControlData.SPECIALBTN_GAMEPAD_A: flag = ControllerPacket.A_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_B: flag = ControllerPacket.B_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_X: flag = ControllerPacket.X_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_Y: flag = ControllerPacket.Y_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_UP: flag = ControllerPacket.UP_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_DOWN: flag = ControllerPacket.DOWN_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_LEFT: flag = ControllerPacket.LEFT_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_DPAD_RIGHT: flag = ControllerPacket.RIGHT_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_LB: flag = ControllerPacket.LB_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_RB: flag = ControllerPacket.RB_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_LS: flag = ControllerPacket.LS_CLK_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_RS: flag = ControllerPacket.RS_CLK_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_START: flag = ControllerPacket.PLAY_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_SELECT: flag = ControllerPacket.BACK_FLAG; break;
            case ControlData.SPECIALBTN_GAMEPAD_LT:
                oscLeftTrigger = (byte) (isDown ? 0xFF : 0);
                reportGamepadState();
                return;
            case ControlData.SPECIALBTN_GAMEPAD_RT:
                oscRightTrigger = (byte) (isDown ? 0xFF : 0);
                reportGamepadState();
                return;
        }

        if (flag != 0) {
            if (isDown) oscGamepadMap |= flag;
            else oscGamepadMap &= ~flag;
            reportGamepadState();
        }
    }

    public void setLeftStick(short x, short y) {
        oscLeftStickX = x;
        oscLeftStickY = y;
        reportGamepadState();
    }

    public void setRightStick(short x, short y) {
        oscRightStickX = x;
        oscRightStickY = y;
        reportGamepadState();
    }

    private void reportGamepadState() {
        if (controllerHandler != null) {
            controllerHandler.reportOscState(
                    oscGamepadMap,
                    oscLeftStickX,
                    oscLeftStickY,
                    oscRightStickX,
                    oscRightStickY,
                    oscLeftTrigger,
                    oscRightTrigger
            );
        }
    }

    private void handleKeyboardAction(int androidKeyCode, boolean isDown) {
        if (conn == null) return;
        short translated = keyboardTranslator.translate(androidKeyCode, -1);
        if (translated == 0) return;

        byte keyState = isDown ? KeyboardPacket.KEY_DOWN : KeyboardPacket.KEY_UP;
        byte modifierFlags = 0;
        if (androidKeyCode == KeyEvent.KEYCODE_SHIFT_LEFT || androidKeyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            if (isDown) modifierFlags |= KeyboardPacket.MODIFIER_SHIFT;
        } else if (androidKeyCode == KeyEvent.KEYCODE_CTRL_LEFT || androidKeyCode == KeyEvent.KEYCODE_CTRL_RIGHT) {
            if (isDown) modifierFlags |= KeyboardPacket.MODIFIER_CTRL;
        } else if (androidKeyCode == KeyEvent.KEYCODE_ALT_LEFT || androidKeyCode == KeyEvent.KEYCODE_ALT_RIGHT) {
            if (isDown) modifierFlags |= KeyboardPacket.MODIFIER_ALT;
        }

        conn.sendKeyboardInput(translated, keyState, modifierFlags, (byte) 0);
    }
}
