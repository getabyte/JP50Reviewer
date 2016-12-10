package heart.david.jp50_reviewer;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by David on 2016/7/5.
 */
public class ColorPickerDialog extends Dialog{

    public ColorPickerDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_dialog_color_picker);
        setup();
    }
    SharedPreferences sharedPreferences;
    String key;
    private void setup(){
        //get buttons
        Button buttonDefaultTheme = (Button)findViewById(R.id.settings_color_dialog_button_default_theme);
        Button buttonTheme1 = (Button)findViewById(R.id.settings_color_dialog_button_theme1);
        Button buttonTheme2 = (Button)findViewById(R.id.settings_color_dialog_button_theme2);
        //setup dialog title
        this.setTitle(this.getContext().getString(R.string.settings_string_color_select_dialog_title));
        //set check mark
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        key = getContext().getString(R.string.key_int_current_theme_index);
        int currentTheme = sharedPreferences.getInt(key, 0);
        if(currentTheme == 0){
            buttonDefaultTheme.setText(this.getContext().getString(R.string.settings_string_color_select_checked));
        }else if(currentTheme == 1){
            buttonTheme1.setText(this.getContext().getString(R.string.settings_string_color_select_checked));
        }else if(currentTheme == 2){
            buttonTheme2.setText(this.getContext().getString(R.string.settings_string_color_select_checked));
        }
        //set on click
        buttonDefaultTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(0);
            }
        });
        buttonTheme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(1);
            }
        });
        buttonTheme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme(2);
            }
        });
    }
    private void selectTheme(int themeIndex){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, themeIndex);
        editor.commit();
        this.dismiss();
    }
}
