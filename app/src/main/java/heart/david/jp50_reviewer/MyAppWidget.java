package heart.david.jp50_reviewer;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MyAppWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
        views.setOnClickPendingIntent(R.id.widget_button_next_character, constructNextCharacterPendingIntent(context, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_button_toggle_star, constructToggleStarPendingIntent(context, appWidgetId));
        views.setOnClickPendingIntent(R.id.widget_button_hide_pronunciation, constructHidePronunciationPendingIntent(context, appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    static private PendingIntent constructNextCharacterPendingIntent(Context context, int appWidgetId){
        Intent intent = new Intent(context, MyAppWidget.class);
        intent.setAction(context.getString(R.string.action_next_character));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    static private PendingIntent constructToggleStarPendingIntent(Context context, int appWidgetId){
        Intent intent = new Intent(context, MyAppWidget.class);
        intent.setAction(context.getString(R.string.action_toggle_star));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    static private PendingIntent constructHidePronunciationPendingIntent(Context context, int appWidgetId){
        Intent intent = new Intent(context, MyAppWidget.class);
        intent.setAction(context.getString(R.string.action_hide_pronunciation));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    static private void applyTheme(Context context, RemoteViews views){
        String key = context.getString(R.string.key_int_current_theme_index);
        int currentTheme = PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
        int backgroundColor = 0, textColor = 0, nextCharacterButtonBackground = 0;
        if(currentTheme == 0){
            backgroundColor = ContextCompat.getColor(context, R.color.color_widget_default_background);
            textColor = ContextCompat.getColor(context, R.color.color_widget_default_textView_text);
            nextCharacterButtonBackground = ContextCompat.getColor(context, R.color.color_widget_default_button_next_character_background);
        }else if(currentTheme == 1){
            backgroundColor = ContextCompat.getColor(context, R.color.color_widget_theme1_background);
            textColor = ContextCompat.getColor(context, R.color.color_widget_theme1_textView_text);
            nextCharacterButtonBackground = ContextCompat.getColor(context, R.color.color_widget_theme1_button_next_character_background);
        }else if(currentTheme == 2){
            backgroundColor = ContextCompat.getColor(context, R.color.color_widget_theme2_background);
            textColor = ContextCompat.getColor(context, R.color.color_widget_theme2_textView_text);
            nextCharacterButtonBackground = ContextCompat.getColor(context, R.color.color_widget_theme2_button_next_character_background);
        }
        views.setInt(R.id.widget_textView_character, "setBackgroundColor", backgroundColor);
        views.setInt(R.id.widget_textView_character, "setTextColor", textColor);
        views.setInt(R.id.widget_button_next_character, "setBackgroundColor", nextCharacterButtonBackground);
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
        applyTheme(context, views);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(intent.getAction().equals(context.getString(R.string.action_next_character))){
            boolean showStarOnly = sharedPreferences.getBoolean(context.getString(R.string.key_boolean_show_star_only), false);
            int currentCharacterIndex = sharedPreferences.getInt(context.getString(R.string.key_int_current_character_index), 0);
            String staredCharacter = sharedPreferences.getString(context.getString(R.string.key_String_stared_character), context.getString(R.string.default_value_String_stared_character));
            if(showStarOnly){//Sequential find next stared index
                int staredCharacterCount = sharedPreferences.getInt(context.getString(R.string.key_int_stared_character_count), 0);
                if(staredCharacterCount > 0){
                    ArrayList<Integer> selectCharacterIndex = new ArrayList<>();
                    for(int i=0;i<staredCharacter.length();i++){
                        if( (staredCharacter.charAt(i)=='1') && (i!=currentCharacterIndex) ){
                            selectCharacterIndex.add(i);
                        }
                    }
                    if(selectCharacterIndex.size() == 0){
                        //current index is the only stared, hint user
                        String thisIsTheOnlyOneStaredCharacter = context.getString(R.string.message_this_is_the_only_one_stared_character);
                        Toast.makeText(context, thisIsTheOnlyOneStaredCharacter, Toast.LENGTH_SHORT).show();
                    }else{
                        currentCharacterIndex = selectCharacterIndex.get(new Random().nextInt(selectCharacterIndex.size()));
                    }
                }else{
                    //error !
                }
            }else{
                //Find which index to select
                String allCharacterSelectedCount = sharedPreferences.getString(context.getString(R.string.key_String_all_character_selected_count), context.getString(R.string.default_value_String_all_character_selected_count));
                ArrayList<Integer> characterSelectIndex = new ArrayList<>();
                for(int i=0;i<allCharacterSelectedCount.length();i++){
                    if(allCharacterSelectedCount.charAt(i)=='0'){
                        characterSelectIndex.add(i);
                    }
                }
                StringBuilder stringBuilder = null;
                if(characterSelectIndex.size() == 0){//all character selected once
                    //all is '1'
                    currentCharacterIndex = new Random().nextInt(allCharacterSelectedCount.length());
                    //reset count string, and add 1 to select count string
                    stringBuilder = new StringBuilder(context.getString(R.string.default_value_String_all_character_selected_count));
                }else{
                    currentCharacterIndex = characterSelectIndex.get(new Random().nextInt(characterSelectIndex.size()));
                    stringBuilder = new StringBuilder(allCharacterSelectedCount);
                }
                //update string count and save back
                stringBuilder.setCharAt(currentCharacterIndex, '1');
                editor.putString(context.getString(R.string.key_String_all_character_selected_count), stringBuilder.toString());
            }
            //update text
            views.setTextViewText(R.id.widget_textView_character, context.getResources().getStringArray(R.array.string_character_array)[currentCharacterIndex]);
            //stared?
            if(staredCharacter.charAt(currentCharacterIndex) == '0'){//not star '☆'
                views.setTextViewText(R.id.widget_button_toggle_star, context.getResources().getString(R.string.string_star_array_first_element));
            }else{//star '★'
                views.setTextViewText(R.id.widget_button_toggle_star, context.getResources().getStringArray(R.array.string_star_array)[1]);
            }
            //hide pronunciation first?
            boolean hidePronunciationFirst = sharedPreferences.getBoolean(context.getString(R.string.key_boolean_hide_pronunciation_first), false);
            if(hidePronunciationFirst){
                views.setTextViewText(R.id.widget_button_hide_pronunciation, context.getString(R.string.string_hide_pronunciation));
            }else{
                views.setTextViewText(R.id.widget_button_hide_pronunciation, context.getResources().getStringArray(R.array.string_pronunciation_array)[currentCharacterIndex]);
            }
            editor.putInt(context.getString(R.string.key_int_current_character_index), currentCharacterIndex);
        }else if(intent.getAction().equals(context.getString(R.string.action_toggle_star))){
            int currentCharacterIndex = sharedPreferences.getInt(context.getString(R.string.key_int_current_character_index), 0);
            // zero based !
            String staredCharacter = sharedPreferences.getString(context.getString(R.string.key_String_stared_character), context.getString(R.string.default_value_String_stared_character));
            int staredCharacterCount = sharedPreferences.getInt(context.getString(R.string.key_int_stared_character_count), 0);
            StringBuilder stringBuilder = new StringBuilder(staredCharacter);
            if(staredCharacter.charAt(currentCharacterIndex) == '1'){
                //toggle off to 0
                stringBuilder.setCharAt(currentCharacterIndex, '0');
                staredCharacterCount--;
                boolean showStarOnly = sharedPreferences.getBoolean(context.getString(R.string.key_boolean_show_star_only), false);
                if(staredCharacterCount == 0 && showStarOnly){
                    String autoSetShowStarOnlyToFalse = context.getApplicationContext().getResources().getString(R.string.message_auto_set_show_star_only_to_false);
                    Toast.makeText(context, autoSetShowStarOnlyToFalse, Toast.LENGTH_SHORT).show();
                    editor.putBoolean(context.getString(R.string.key_boolean_show_star_only), false);
                }
                //set '☆'
                views.setTextViewText(R.id.widget_button_toggle_star, context.getResources().getStringArray(R.array.string_star_array)[0]);
            }else{
                //toggle on to 1
                stringBuilder.setCharAt(currentCharacterIndex, '1');
                staredCharacterCount++;
                //set '★'
                views.setTextViewText(R.id.widget_button_toggle_star, context.getResources().getStringArray(R.array.string_star_array)[1]);
            }
            staredCharacter = stringBuilder.toString();
            editor.putString(context.getString(R.string.key_String_stared_character), staredCharacter);
            editor.putInt(context.getString(R.string.key_int_stared_character_count), staredCharacterCount);
        }else if(intent.getAction().equals(context.getString(R.string.action_hide_pronunciation))){
            int currentCharacterIndex = sharedPreferences.getInt(context.getString(R.string.key_int_current_character_index), 0);
            String currentCharacterPronunciation = context.getResources().getStringArray(R.array.string_pronunciation_array)[currentCharacterIndex];
            views.setTextViewText(R.id.widget_button_hide_pronunciation, currentCharacterPronunciation);
        }
        //again important line
        editor.commit();
        ComponentName componentName = new ComponentName(context, MyAppWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(componentName,views);
        super.onReceive(context, intent);
    }
}
/*
http://www.androidauthority.com/create-simple-android-widget-608975/
import java.util.*;
import java.lang.*;
import java.io.*;
class Ideone
{
    public static void main (String[] args) throws java.lang.Exception
    {
        // your code goes here
        Scanner scanner = new Scanner(System.in);
        String c, p;
        ArrayList<String> arrayListC = new ArrayList<>();
        ArrayList<String> arrayListP = new ArrayList<>();
        while(scanner.hasNext()){
            c = scanner.next();
            arrayListC.add(c);
            p = scanner.next();
            arrayListP.add(p);
            System.out.println(c+" "+p);
        }
        for(String s:arrayListC){
            System.out.println("<item>"+s+"</item>");
        }
        for(String s:arrayListP){
            System.out.println("<item>"+s+"</item>");
        }
    }
}
あ a
い i
う u
え e
お o

か ka
き ki
く ku
け ke
こ ko

さ sa
し shi
す su
せ se
そ so

た ta
ち chi
つ tsu
て te
と to

な na
に ni
ぬ nu
ね ne
の no

は ha
ひ hi
ふ fu
へ he
ほ ho

ま ma
み mi
む mu
め me
も mo

や ya
ゆ yu
よ yo

ら ra
り ri
る ru
れ re
ろ ro

わ wa
を o
ん n

ア a
イ i
ウ u
エ e
オ o

カ ka
キ ki
ク ku
ケ ke
コ ko

サ sa
シ shi
ス su
セ se
ソ so

タ ta
チ chi
ツ tsu
テ te
ト to

ナ na
ニ ni
ヌ nu
ネ ne
ノ no

ハ ha
ヒ hi
フ fu
ヘ he
ホ ho

マ ma
ミ mi
ム mu
メ me
モ mo

ヤ ya
ユ yu
ヨ yo

ラ ra
リ ri
ル ru
レ re
ロ ro

ワ wa
ヲ o
ン n
 */
