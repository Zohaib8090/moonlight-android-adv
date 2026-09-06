package com.limelight.preferences;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limelight.binding.input.customcontrols.ControlData;
import com.limelight.binding.input.customcontrols.ControlInterface;
import com.limelight.binding.input.customcontrols.ControlJoystickData;
import com.limelight.binding.input.customcontrols.ControlLayout;
import com.limelight.binding.input.customcontrols.editor.ActionRow;
import com.limelight.binding.input.customcontrols.editor.ControlHandleView;
import com.limelight.binding.input.customcontrols.editor.EditControlPopup;

public class ControlProfileEditorActivity extends Activity {

    private ControlLayout controlLayout;
    private EditControlPopup editPopup;
    private ControlHandleView handleView;
    private ActionRow actionRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Root layout: dark background
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF1A1A1A);
        setContentView(root);

        // ControlLayout fills entire screen
        controlLayout = new ControlLayout(this);
        controlLayout.setModifiable(true);
        controlLayout.loadLayoutFromPreferences();
        FrameLayout.LayoutParams clLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        root.addView(controlLayout, clLp);

        // Add editor sub-components into root (not controlLayout)
        actionRow = new ActionRow(this);
        root.addView(actionRow);
        controlLayout.setActionRow(actionRow);

        handleView = new ControlHandleView(this);
        root.addView(handleView);

        editPopup = new EditControlPopup(this, root);

        // Wire long-click for all loaded buttons
        patchButtonLongClicks();

        // Toolbar at the top right
        buildToolbar(root);
    }

    private void patchButtonLongClicks() {
        controlLayout.post(this::patchAllLongClicks);
    }

    public void editButton(ControlInterface button) {
        if (editPopup != null) {
            editPopup.setCurrentlyEditedButton(button);
            editPopup.appear(true);
            button.loadEditValues(editPopup);
        }
        if (handleView != null) {
            handleView.setControlButton(button);
        }
    }

    private void buildToolbar(FrameLayout root) {
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setBackgroundColor(0xCC000000);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        int padPx = dp(8);
        toolbar.setPadding(padPx, padPx, padPx, padPx);

        FrameLayout.LayoutParams tbLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
        tbLp.gravity = Gravity.TOP;
        root.addView(toolbar, tbLp);

        // Title
        TextView title = new TextView(this);
        title.setText("Edit Controls");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        toolbar.addView(title, titleLp);

        // Add Button
        Button btnAdd = makeToolBtn("+ Button");
        btnAdd.setOnClickListener(v -> {
            ControlData newBtn = new ControlData("BTN");
            newBtn.dynamicX = "${screen_width} / 2 - ${width} / 2";
            newBtn.dynamicY = "${screen_height} / 2 - ${height} / 2";
            controlLayout.addControlButton(newBtn);
            controlLayout.post(this::patchAllLongClicks);
            Toast.makeText(this, "Button added — tap & hold to edit", Toast.LENGTH_SHORT).show();
        });
        toolbar.addView(btnAdd);

        // Add Joystick
        Button btnJoy = makeToolBtn("+ Joystick");
        btnJoy.setOnClickListener(v -> {
            ControlJoystickData stick = new ControlJoystickData(ControlJoystickData.JOYSTICK_MODE_WASD);
            stick.dynamicX = "${screen_width} / 2 - ${width} / 2";
            stick.dynamicY = "${screen_height} / 2 - ${height} / 2";
            controlLayout.addJoystickButton(stick);
            controlLayout.post(this::patchAllLongClicks);
            Toast.makeText(this, "Joystick added", Toast.LENGTH_SHORT).show();
        });
        toolbar.addView(btnJoy);

        // Save
        Button btnSave = makeToolBtn("Save");
        btnSave.setOnClickListener(v -> {
            controlLayout.saveLayout();
            Toast.makeText(this, "Layout saved!", Toast.LENGTH_SHORT).show();
        });
        toolbar.addView(btnSave);

        // Reset
        Button btnReset = makeToolBtn("Reset");
        btnReset.setOnClickListener(v -> {
            controlLayout.loadDefaultLayout();
            patchAllLongClicks();
            Toast.makeText(this, "Reset to empty layout", Toast.LENGTH_SHORT).show();
        });
        toolbar.addView(btnReset);

        // Done (save & close)
        Button btnDone = makeToolBtn("Done ✓");
        btnDone.setOnClickListener(v -> {
            controlLayout.saveLayout();
            finish();
        });
        toolbar.addView(btnDone);
    }

    private void patchAllLongClicks() {
        for (ControlInterface btn : controlLayout.getButtonChildren()) {
            btn.getControlView().setOnLongClickListener(v -> {
                editButton(btn);
                if (actionRow != null) actionRow.setFollowedButton(btn);
                return true;
            });
        }
    }

    private Button makeToolBtn(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(12);
        btn.setBackgroundColor(0xFF2A2A2A);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(dp(4), 0, dp(4), 0);
        btn.setLayoutParams(lp);
        return btn;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
