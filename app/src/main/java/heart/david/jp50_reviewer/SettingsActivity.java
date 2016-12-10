package heart.david.jp50_reviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

// TODO: 2016/6/27 allow to play sound when press showPronunciation Button
public class SettingsActivity extends AppCompatActivity {
    SettingsActivity mySelf;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setup two switches and two buttons
        mySelf = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        editor = sharedPreferences.edit();
        setupSwitchShowStarOnly();
        setupSwitchHidePronunciationFirst();
        setupThemeSelectButton();
        setupConfirmAndCancelButtons();
    }
    /*two switches*/
    private void setupSwitchShowStarOnly(){
        final Switch switchShowStarOnly = (Switch)findViewById(R.id.settings_switch_show_star_only);
        boolean showStarOnly = sharedPreferences.getBoolean(getString(R.string.key_boolean_show_star_only), false);
        switchShowStarOnly.setChecked(showStarOnly);
        switchShowStarOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int staredNumber = sharedPreferences.getInt(getString(R.string.key_int_stared_character_count), 0);
                    if (staredNumber == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        String dialogTitle = mySelf.getResources().getString(R.string.settings_no_stared_warning_title);
                        String dialogMessage = mySelf.getResources().getString(R.string.settings_no_stared_warning_message);
                        String dialogButtonText = mySelf.getResources().getString(R.string.settings_no_stared_warning_button);
                        builder.setTitle(dialogTitle);
                        builder.setMessage(dialogMessage);
                        builder.setPositiveButton(dialogButtonText, null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        switchShowStarOnly.setChecked(false);
                    }
                }
            }
        });
    }
    private void setupSwitchHidePronunciationFirst(){
        Switch switchHidePronunciationFirst = (Switch)findViewById(R.id.settings_switch_hide_pronunciation_first);
        boolean hidePronunciationFirst = sharedPreferences.getBoolean(getString(R.string.key_boolean_hide_pronunciation_first), false);
        switchHidePronunciationFirst.setChecked(hidePronunciationFirst);
    }
    /*setup theme select button*/
    private int previousTheme;//used when user choose cancel after select a theme
    private void setupThemeSelectButton(){
        final Button buttonThemeSelect = (Button)findViewById(R.id.settings_button_theme_select);
        previousTheme = sharedPreferences.getInt(getString(R.string.key_int_current_theme_index), 0);
        updateThemeSelectButtonAndPreviousTheme(buttonThemeSelect);
        buttonThemeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(mySelf);
                colorPickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateThemeSelectButtonAndPreviousTheme(buttonThemeSelect);
                    }
                });
                colorPickerDialog.show();
            }
        });
    }
    private void updateThemeSelectButtonAndPreviousTheme(Button buttonThemeSelect){
        int currentTheme = sharedPreferences.getInt(getString(R.string.key_int_current_theme_index), 0);
        if(currentTheme == 0){
            buttonThemeSelect.setBackgroundColor(ContextCompat.getColor(mySelf.getApplicationContext(), R.color.color_widget_default_background));
            buttonThemeSelect.setTextColor(ContextCompat.getColor(mySelf.getApplicationContext(), R.color.color_widget_default_textView_text));
        }else if(currentTheme == 1){
            buttonThemeSelect.setBackgroundColor(ContextCompat.getColor(mySelf.getApplicationContext(), R.color.color_widget_theme1_background));
            buttonThemeSelect.setTextColor(ContextCompat.getColor(mySelf.getApplicationContext(), R.color.color_widget_theme1_textView_text));
        }else if(currentTheme == 2){
            buttonThemeSelect.setBackgroundColor(ContextCompat.getColor(mySelf.getApplicationContext(), R.color.color_widget_theme2_background));
            buttonThemeSelect.setTextColor(ContextCompat.getColor(mySelf.getApplicationContext(), R.color.color_widget_theme2_textView_text));
        }
        editor.putInt(getString(R.string.key_int_current_theme_index), currentTheme);
        editor.commit();
    }
    /*confirm and cancel buttons*/
    private void setupConfirmAndCancelButtons(){
        final Button configButtonOK = (Button)findViewById(R.id.settings_button_confirm);
        configButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultValue = new Intent();
                resultValue.setAction(Intent.ACTION_MAIN);
                resultValue.addCategory(Intent.CATEGORY_LAUNCHER);
                setResult(RESULT_OK, resultValue);

                //important line!
                final Switch switchShowStarOnly = (Switch)findViewById(R.id.settings_switch_show_star_only);
                if(switchShowStarOnly.isChecked()){
                    editor.putBoolean(getString(R.string.key_boolean_show_star_only), true);
                }else{
                    editor.putBoolean(getString(R.string.key_boolean_show_star_only), false);
                }
                Switch switchHidePronunciationFirst = (Switch)findViewById(R.id.settings_switch_hide_pronunciation_first);
                if(switchHidePronunciationFirst.isChecked()){
                    editor.putBoolean(getString(R.string.key_boolean_hide_pronunciation_first), true);
                }else{
                    editor.putBoolean(getString(R.string.key_boolean_hide_pronunciation_first), false);
                }

                editor.commit();
                mySelf.finish();
            }
        });
        Button configButtonCancel = (Button)findViewById(R.id.settings_button_cancel);
        configButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change to previous theme if canceled
                int currentTheme = sharedPreferences.getInt(getString(R.string.key_int_current_theme_index), 0);
                if(previousTheme != currentTheme){
                    editor.putInt(getString(R.string.key_int_current_theme_index), previousTheme);
                }
                editor.commit();
                setResult(RESULT_CANCELED);
                mySelf.finish();
            }
        });
    }
}
