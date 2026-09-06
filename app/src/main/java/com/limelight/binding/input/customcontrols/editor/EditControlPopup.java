package com.limelight.binding.input.customcontrols.editor;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.limelight.R;
import com.limelight.binding.input.customcontrols.ControlData;
import com.limelight.binding.input.customcontrols.ControlDrawer;
import com.limelight.binding.input.customcontrols.ControlDrawerData;
import com.limelight.binding.input.customcontrols.ControlInterface;

public class EditControlPopup {
    private final Context context;
    private final View rootView;
    private ControlInterface currentButton;

    private EditText editName;
    private EditText editSizeX, editSizeY;
    private final TextView[] mappingTextViews = new TextView[4];
    private TextView txtOrientation;
    private Spinner spinnerOrientation;
    private Switch switchToggle, switchPassThrough, switchSwipeable;
    private Button btnBgColor, btnStrokeColor;
    private SeekBar seekStrokeWidth, seekCornerRadius, seekAlpha;
    private TextView txtStrokeWidthPercent, txtCornerRadiusPercent, txtAlphaPercent;
    private Button btnDone;

    private KeyboardPickerDialog keyboardDialog;
    private boolean isUpdatingUi = false;

    public EditControlPopup(Context context, ViewGroup parent) {
        this.context = context;
        this.rootView = LayoutInflater.from(context).inflate(R.layout.dialog_control_button_setting, parent, false);

        // Anchor the popup to the left side, sized to its content. Without
        // explicit LayoutParams the FrameLayout default (MATCH_PARENT x2) makes
        // it fill the whole screen and block the layout underneath.
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                (int) (320 * context.getResources().getDisplayMetrics().density),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        parent.addView(rootView, lp);

        keyboardDialog = new KeyboardPickerDialog(context);

        bindViews();
        setupListeners();
        hide();
    }

    private void bindViews() {
        editName = rootView.findViewById(R.id.editName_editText);
        editSizeX = rootView.findViewById(R.id.editSize_editTextX);
        editSizeY = rootView.findViewById(R.id.editSize_editTextY);

        mappingTextViews[0] = rootView.findViewById(R.id.mapping_1_textview);
        mappingTextViews[1] = rootView.findViewById(R.id.mapping_2_textview);
        mappingTextViews[2] = rootView.findViewById(R.id.mapping_3_textview);
        mappingTextViews[3] = rootView.findViewById(R.id.mapping_4_textview);

        txtOrientation = rootView.findViewById(R.id.editOrientation_textView);
        spinnerOrientation = rootView.findViewById(R.id.editOrientation_spinner);
        switchToggle = rootView.findViewById(R.id.checkboxToggle);
        switchPassThrough = rootView.findViewById(R.id.checkboxPassThrough);
        switchSwipeable = rootView.findViewById(R.id.checkboxSwipeable);
        btnBgColor = rootView.findViewById(R.id.btn_select_bg_color);
        btnStrokeColor = rootView.findViewById(R.id.btn_select_stroke_color);
        seekStrokeWidth = rootView.findViewById(R.id.editStrokeWidth_seekbar);
        seekCornerRadius = rootView.findViewById(R.id.editCornerRadius_seekbar);
        seekAlpha = rootView.findViewById(R.id.editAlpha_seekbar);
        txtStrokeWidthPercent = rootView.findViewById(R.id.editStrokeWidth_textView_percent);
        txtCornerRadiusPercent = rootView.findViewById(R.id.editCornerRadius_textView_percent);
        txtAlphaPercent = rootView.findViewById(R.id.editAlpha_textView_percent);
        btnDone = rootView.findViewById(R.id.btn_dialog_done);

        ArrayAdapter<String> orientationAdapter = new ArrayAdapter<>(context, R.layout.item_centered_textview, ControlDrawerData.getOrientations(context));
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrientation.setAdapter(orientationAdapter);
    }

    private void setupListeners() {
        btnDone.setOnClickListener(v -> hide());

        editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingUi || currentButton == null) return;
                currentButton.getProperties().name = s.toString();
                currentButton.updateProperties();
            }
        });

        TextWatcher sizeWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingUi || currentButton == null) return;
                try {
                    float w = currentButton.getProperties().getWidthDp();
                    float h = currentButton.getProperties().getHeightDp();
                    
                    try { w = Float.parseFloat(editSizeX.getText().toString()); } catch (NumberFormatException ignored) {}
                    try { h = Float.parseFloat(editSizeY.getText().toString()); } catch (NumberFormatException ignored) {}
                    
                    if (w > 10) currentButton.getProperties().setWidthDp(w);
                    if (h > 10) currentButton.getProperties().setHeightDp(h);
                    
                    currentButton.regenerateDynamicCoordinates();
                    currentButton.getControlView().requestLayout();
                    currentButton.getControlView().invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        editSizeX.addTextChangedListener(sizeWatcher);
        editSizeY.addTextChangedListener(sizeWatcher);

        // Connect each of the 4 mapping slots to KeyboardPickerDialog
        for (int i = 0; i < mappingTextViews.length; i++) {
            final int slotIndex = i;
            if (mappingTextViews[i] != null) {
                mappingTextViews[i].setOnClickListener(v -> {
                    keyboardDialog.setOnKeyPickedListener((keycode, label) -> {
                        if (currentButton != null) {
                            if (currentButton.getProperties().keycodes == null || currentButton.getProperties().keycodes.length < 4) {
                                currentButton.getProperties().keycodes = new int[4];
                            }
                            currentButton.getProperties().keycodes[slotIndex] = keycode;
                            mappingTextViews[slotIndex].setText(label);
                        }
                    });
                    keyboardDialog.show();
                });
            }
        }

        spinnerOrientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdatingUi || !(currentButton instanceof ControlDrawer)) return;
                ControlDrawer drawer = (ControlDrawer) currentButton;
                drawer.drawerData.orientation = ControlDrawerData.Orientation.values()[position];
                drawer.alignButtons();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        switchToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingUi || currentButton == null) return;
            currentButton.getProperties().isToggle = isChecked;
        });

        switchPassThrough.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingUi || currentButton == null) return;
            currentButton.getProperties().passThruEnabled = isChecked;
        });

        switchSwipeable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingUi || currentButton == null) return;
            currentButton.getProperties().isSwipeable = isChecked;
        });

        seekStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtStrokeWidthPercent.setText(progress + "dp");
                if (isUpdatingUi || currentButton == null) return;
                currentButton.getProperties().strokeWidth = progress;
                currentButton.updateProperties();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekCornerRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtCornerRadiusPercent.setText(progress + "%");
                if (isUpdatingUi || currentButton == null) return;
                currentButton.getProperties().cornerRadius = progress;
                currentButton.updateProperties();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtAlphaPercent.setText(progress + "%");
                if (isUpdatingUi || currentButton == null) return;
                currentButton.getProperties().opacity = progress / 100f;
                currentButton.updateProperties();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnBgColor.setOnClickListener(v -> showColorPicker(true));
        btnStrokeColor.setOnClickListener(v -> showColorPicker(false));
    }

    private void showColorPicker(boolean isBackground) {
        final int[] colors = new int[]{
                0x66000000, 0xAA000000, 0x4D4D90FE, 0x884D90FE,
                0x882A303C, 0xAA4CAF50, 0xAAE53935, 0xAAFF9800,
                0xFFFFFFFF, 0xFF000000
        };
        final String[] names = new String[]{
                "Semi-Transparent Black", "Dark Black", "Soft Blue", "Accent Blue",
                "Dark Slate", "Green", "Red", "Orange",
                "White", "Solid Black"
        };

        new AlertDialog.Builder(context)
                .setTitle(isBackground ? "Choose Background Color" : "Choose Border Color")
                .setItems(names, (dialog, which) -> {
                    if (currentButton != null) {
                        if (isBackground) {
                            currentButton.getProperties().bgColor = colors[which];
                        } else {
                            currentButton.getProperties().strokeColor = colors[which];
                        }
                        currentButton.updateProperties();
                    }
                })
                .show();
    }

    public void loadValues(ControlData data) {
        isUpdatingUi = true;
        editName.setText(data.name);
        editSizeX.setText(String.valueOf((int) data.getWidthDp()));
        editSizeY.setText(String.valueOf((int) data.getHeightDp()));

        // Populate the 4 mapping slot labels
        for (int i = 0; i < mappingTextViews.length; i++) {
            if (mappingTextViews[i] != null) {
                int code = (data.keycodes != null && i < data.keycodes.length) ? data.keycodes[i] : ControlData.KEYCODE_NONE;
                mappingTextViews[i].setText(KeyboardPickerDialog.getKeyLabel(code));
            }
        }

        switchToggle.setChecked(data.isToggle);
        switchPassThrough.setChecked(data.passThruEnabled);
        switchSwipeable.setChecked(data.isSwipeable);

        seekStrokeWidth.setProgress((int) data.strokeWidth);
        txtStrokeWidthPercent.setText((int) data.strokeWidth + "dp");

        seekCornerRadius.setProgress((int) data.cornerRadius);
        txtCornerRadiusPercent.setText((int) data.cornerRadius + "%");

        int alpha = (int) (data.opacity * 100);
        seekAlpha.setProgress(alpha);
        txtAlphaPercent.setText(alpha + "%");

        showOrientation(false);
        isUpdatingUi = false;
    }

    public void showOrientation(boolean show) {
        txtOrientation.setVisibility(show ? View.VISIBLE : View.GONE);
        spinnerOrientation.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setCurrentlyEditedButton(ControlInterface button) {
        this.currentButton = button;
    }

    public void appear(boolean fromRight) {
        rootView.setVisibility(View.VISIBLE);
        rootView.bringToFront();
    }

    public void hide() {
        rootView.setVisibility(View.GONE);
        currentButton = null;
    }

    public void adaptPanelPosition() {
        // Keeps popup visible without moving it frantically
    }
}
