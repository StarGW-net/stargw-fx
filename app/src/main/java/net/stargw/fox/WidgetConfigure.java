package net.stargw.fox;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class WidgetConfigure extends Activity {

    Context myContext;

    private FOXCountryListAdapter adapterAdd;

    private int appWidgetId;
    private int opacity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myContext = this;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Global.Log("Widget Configuring = " + appWidgetId,3);

        setContentView(R.layout.widget_configure);

        // info.setTitle("Set Rate Alert");
        final TextView title = (TextView) findViewById(R.id.title);
        title.setText("Configure Widget");

        ImageView im0 = (ImageView) findViewById(R.id.delete);
        im0.setVisibility(View.INVISIBLE);

        final TextView code1 = (TextView) findViewById(R.id.countryCode1);
        code1.setText(Global.getBaseCurrency());

        final ImageView im1 = (ImageView) findViewById(R.id.countryFlag1);
        im1.setImageResource(Global.getFlag(Global.getBaseCurrency()));

        final TextView code2 = (TextView) findViewById(R.id.countryCode2);
        String code = Global.FOXCurrencyAll.firstKey();
        if ( (Global.FOXCurrencyMy != null) && (Global.FOXCurrencyMy.size() != 0) ) {
            code = Global.FOXCurrencyMy.firstKey();
        }
        code2.setText(code);

        final ImageView im2 = (ImageView) findViewById(R.id.countryFlag2);
        im2.setImageResource(Global.getFlag(code));


        im1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code2 = (TextView) findViewById(R.id.countryCode2);
                String other = (String) code2.getText();
                pickCurrency(im1,code1,other); // need to pass in other values to stop picking duplicate...
            }
        });

        code1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code2 = (TextView) findViewById(R.id.countryCode2);
                String other = (String) code2.getText();
                pickCurrency(im1,code1, other);
            }
        });

        im2.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code1 = (TextView) findViewById(R.id.countryCode1);
                String other = (String) code1.getText();
                pickCurrency(im2,code2,other); // need to pass in other values to stop picking duplicate...
            }
        });

        code2.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code1 = (TextView) findViewById(R.id.countryCode1);
                String other = (String) code1.getText();
                pickCurrency(im2,code2, other);
            }
        });

        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                opacity = progress;
                // Global.Log("Progress Opacity = " + progress,3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        };

        seekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        Button noButton = (Button) findViewById(R.id.noButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                // info.cancel();
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_CANCELED, resultValue);
                finish();

            }
        });

        Button yesButton = (Button) findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code1 = (TextView) findViewById(R.id.countryCode1);
                String c1 = code1.getText().toString();
                TextView code2 = (TextView) findViewById(R.id.countryCode2);
                String c2 = code2.getText().toString();
                updateWidget(c1,c2);
            }
        });

    }


    private void pickCurrency(final ImageView im1, final TextView code1, String other)
    {

        final Dialog info = new Dialog(myContext);

        info.setContentView(R.layout.dialog_add);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        info.setTitle("Pick Currency");

        info.getWindow().setGravity(Gravity.TOP);


        EditText myFilter = (EditText) info.findViewById(R.id.activity_main_filter_text);

        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Global.Log("Filter on text: " + s , 3);
                adapterAdd.getFilter().filter(s.toString());
            }
        });


        // adapterAdd = new FOXRecordAdapter(myContext, (TreeMap<String, Float>) Global.FOXCurrencyAll);

        TreeMap<String, Float> tempCurrency = new TreeMap<String, Float>();

        tempCurrency.putAll(Global.FOXCurrencyAll);

        // remove currencies we already have
        tempCurrency.remove(other);

        String[] mapKeys = tempCurrency.keySet().toArray(new String[tempCurrency.size()]);

        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(mapKeys)); //new ArrayList is only needed if you absolutely need an ArrayList

        adapterAdd = new FOXCountryListAdapter(myContext, stringList);

        ListView listViewAdd = (ListView) info.findViewById(R.id.listAddFOX);

        listViewAdd.setAdapter(adapterAdd);

        listViewAdd.setClickable(true);

        listViewAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = adapterAdd.getItem(position);
                Global.Log("Widget got item = " + code,3);
                im1.setImageResource(Global.getFlag(code));
                code1.setText(code);
                info.dismiss();
            }
        });


        info.show();


    }


    public void updateWidget(String code1,String code2)
    {

        Global.Log("Configuring Widget = " + appWidgetId,3);

        /*
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(myContext);

        RemoteViews views = new RemoteViews(myContext.getPackageName(),
                R.layout.widget1_layout);

        appWidgetManager.updateAppWidget(appWidgetId, );
*/
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(myContext);

        p.edit().putString("C1-" + appWidgetId, code1).apply();
        p.edit().putString("C2-" + appWidgetId, code2).apply();
        p.edit().putInt("O1-" + appWidgetId, opacity).apply();
        // Global.Log("Save Opacity = " + opacity,3);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);

        Global.updateMyWidgets();

        finish();

    }


}
