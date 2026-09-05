package com.limelight.binding.input.customcontrols;

public class ControlJoystickData extends ControlData {
    public static final int JOYSTICK_MODE_WASD = 0;
    public static final int JOYSTICK_MODE_GAMEPAD_LEFT = 1;
    public static final int JOYSTICK_MODE_GAMEPAD_RIGHT = 2;

    public int joystickMode = JOYSTICK_MODE_WASD;
    public boolean forwardLock = false;

    public ControlJoystickData() {
        super("JOYSTICK", new int[]{0}, "${margin} * 4", "${screen_height} - ${height} - ${margin} * 4", 120f, 120f);
        this.opacity = 0.75f;
    }

    public ControlJoystickData(int mode) {
        this();
        this.joystickMode = mode;
        this.name = mode == JOYSTICK_MODE_WASD ? "WASD" : (mode == JOYSTICK_MODE_GAMEPAD_LEFT ? "LS" : "RS");
    }
}
