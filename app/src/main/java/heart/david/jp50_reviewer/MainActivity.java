package heart.david.jp50_reviewer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    MainActivity myself;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myself = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_my_email_address)});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.email_application_choosing)));
                }catch(android.content.ActivityNotFoundException ex){
                    AlertDialog.Builder builder = new AlertDialog.Builder(myself);
                    String dialogTitle = getString(R.string.email_there_is_no_email_application_dialog_title);
                    String dialogMessage = getString(R.string.email_there_is_no_email_application_dialog_message);
                    String dialogButtonText = getString(R.string.email_there_is_not_email_application_dialog_ok);
                    builder.setTitle(dialogTitle);
                    builder.setMessage(dialogMessage);
                    builder.setPositiveButton(dialogButtonText, null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {//update main text, to show stared character
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String staredCharacter = sharedPreferences.getString(getString(R.string.key_String_stared_character), getString(R.string.default_value_String_stared_character));
        TextView mainTextView = (TextView)findViewById(R.id.main_text_view);
        if(staredCharacter.equals(getString(R.string.default_value_String_stared_character))){
            //reset to default main text.
            mainTextView.setText(getString(R.string.main_text));
        }else{

            mainTextView.setText(getString(R.string.main_text_current_stared_character));//reset text
            for(int i=0;i<staredCharacter.length();i++){
                if(staredCharacter.charAt(i) == '1'){
                    String cp = getResources().getStringArray(R.array.string_character_array)[i] + " " + getResources().getStringArray(R.array.string_pronunciation_array)[i] + "\n";
                    mainTextView.append(cp);
                }
            }
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent configActivity = new Intent(this, SettingsActivity.class);
                startActivity(configActivity);
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
