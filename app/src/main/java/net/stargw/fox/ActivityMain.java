package net.stargw.fox;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import static android.view.View.GONE;

public class ActivityMain extends Activity implements ActivityMainListener {

    private FOXCurrencyAdapter adapterCurrency;
    private FOXCountryListAdapter adapterAdd;
    private FOXRateAlertAdapter adapterRateAlerts;

    private BroadcastListener mReceiver;

    private Context myContext;

    // private ListAdapter adapter;
    ListView gLogListview;
    LogAdapter gLogAdapter;
    ArrayList<String> gLogBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this;

        Global.Log("onCreate - ActivityMain",2);


        ImageView btn = (ImageView) findViewById(R.id.activity_main_menu_options);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showOptionsMenu(v);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //now getIntent() should always return the last received intent
    }

    @Override
    protected void onPause() {
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();

        if (intent != null) {
            String action = intent.getAction();
            Global.Log("App Resume Action: " + action, 2);
            if (action != null) {
                if (action.equals(Global.FOX_OPEN_ERROR)) {
                    showErrorLogs(Global.FILE_LOG_ERRORS, "Error Log", null);
                }
                if (action.equals(Global.FOX_OPEN_ALERTS)) {
                    showErrorLogs(Global.FILE_LOG_ALERTS,"Fired Rate Alerts", null);
                }
            }
        } else {
            Global.checklastUpdate();
        }

        // register receiver
        mReceiver = new BroadcastListener();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Global.SCREEN_REFRESH_INTENT);
        registerReceiver(mReceiver, mIntentFilter);

        updateGUI();

    }




    private class BroadcastListener extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            // Logs.myLog("App Received intent", 2);

            // Log.w("FWMain", "Got Action = " + intent.getAction());

            if (Global.SCREEN_REFRESH_INTENT.equals(intent.getAction())) {
                Global.Log("App Received intent to update screen", 2);
                updateGUI();

            }
        }
    }

    private void updateGUI()
    {

        Global.FOXRateAlerts = Global.readRateAlertFile(Global.FILE_FOX_ALERTS);

        if ( (Global.FOXRateAlerts == null) || (Global.FOXRateAlerts.size() == 0) ) {
            Global.Log("FOXRateAlerts empty",2);
        }

        Global.FOXCurrencyAll = Global.readFOXFile(Global.FILE_FOX_CACHE);
        if ( (Global.FOXCurrencyAll == null) || (Global.FOXCurrencyAll.size() == 0) ) {
            Global.Log("FOXCurrencyAll empty",2);
            Global.FOXCurrencyAll = new TreeMap<String, Float>();
            // Global.FOXCurrencyAll = new TreeMap<String, Float> (new FOXRecordSort());
            Global.FOXCurrencyAll.put("GBP", (float) 1);
        }

        // This tells it to sort
        // Global.FOXCurrencyMy = new TreeMap<String, Float> (new FOXRecordSort());
        // Global.FOXCurrencyMy = new TreeMap<String, Float>();
        Global.FOXCurrencyMy = Global.readFOXFile(Global.FILE_FOX_MY);

        /*
        Global.FOXCurrencyMy = new TreeMap<String, Float>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.toLowerCase().compareTo(o1.toLowerCase());
            }
        });
        */

        TextView text4 = (TextView) findViewById(R.id.rowText2);
        if (Global.CodeToCurrency.containsKey(Global.getBaseCurrency()))
        {
            text4.setText(getString(Global.CodeToCurrency.get(Global.getBaseCurrency())));
        } else {
            text4.setText("unknown");
        }

        Global.Log("Initial Currency Array Size = " + Global.FOXCurrencyMy.size(),3);

        if (Global.FOXCurrencyMy.size() == 0 )
        {
            // Global.FOXCurrencyMy.put("GBP", (float) 1);
            Global.FOXCurrencyMy.put("THB", (float) 0);
            Global.FOXCurrencyMy.put("USD", (float) 0);
            Global.FOXCurrencyMy.put("EUR", (float) 0);
            Global.FOXCurrencyMy.put("SGD", (float) 0);
        }

        // Global.FOXCurrencyMy.remove(Global.getBaseCurrency());

        TextView text1 = findViewById(R.id.rowText1);
        text1.setText(Global.getBaseCurrency());

        TextView text3 = findViewById(R.id.rowTextValue);
        text3.setText(String.format(java.util.Locale.US,"%.3f",  Global.getBaseCurrencyAmount())  );


        RelativeLayout row = findViewById(R.id.fullrow);
        row.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                enterAmount();
            }
        });


        ImageView im = (ImageView) findViewById(R.id.flag);
        im.setImageResource(Global.getFlag(Global.getBaseCurrency()));

        long t1 = Global.getUpdateTime();;

        // Date timeNow = new Date();

        Calendar newDate = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

        String humanDate = format.format(newDate.getTime());
        newDate.setTime(new Date(t1));
        humanDate = format.format(newDate.getTime());

        TextView text5 = findViewById(R.id.widgit_update_date);
        text5.setText(humanDate + " [" + Global.getUpdateSource() + "]");

        text5.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                // display when next update is
                // getAlarm();
                Global.NextAlarmTime nextAlarmTime = new Global.NextAlarmTime();
                Toast.makeText(myContext.getApplicationContext(), "Next Update Scheduled\n\n" + nextAlarmTime.humanDate, Toast.LENGTH_SHORT).show();
            }
        });

        // Display it!
        adapterCurrency = new FOXCurrencyAdapter(myContext, (TreeMap<String, Float>) Global.FOXCurrencyMy);

        ListView listView = (ListView) findViewById(R.id.listFOX);

        listView.setAdapter(adapterCurrency);

        listView.setClickable(true);
/*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String code = adapterCurrency.getItem(position);
                Global.Log("Got item = " + code,2);
                displayOptions(code);
            }
        });
*/
    }


    // change base currency amount
    private void enterAmount()
    {

        final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_amount_generic);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        // info.setTitle("Enter Amount");
        final TextView title = (TextView) info.findViewById(R.id.title);
        title.setText("Enter Amount");

        ImageView im0 = (ImageView) info.findViewById(R.id.delete);
        im0.setVisibility(View.INVISIBLE);

        im0 = (ImageView) info.findViewById(R.id.countryFlag2);
        im0.setVisibility(GONE);

        im0 = (ImageView) info.findViewById(R.id.arrowIcon);
        im0.setVisibility(GONE);

        TextView text0 = (TextView) info.findViewById(R.id.countryCode2);
        text0.setVisibility(GONE);

        TextView text1 = (TextView) info.findViewById(R.id.countryCode1);
        text1.setText(Global.getBaseCurrency());

        ImageView im = (ImageView) info.findViewById(R.id.countryFlag1);
        im.setImageResource(Global.getFlag(Global.getBaseCurrency()));

        ImageView option1 = (ImageView) info.findViewById(R.id.resetAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("");
            }
        });

        option1 = (ImageView) info.findViewById(R.id.oneAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("1.00");
            }
        });

        EditText text = (EditText) info.findViewById(R.id.enterAmount);
        text.setText(String.format(java.util.Locale.US,"%.2f", Global.getBaseCurrencyAmount())  );
        text.setCursorVisible(true);
        // text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.requestFocus();

        // Show keyboard
        info.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // listen for enter and close keyboard
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Global.Log("Got key = " + keyCode, 2);
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    info.dismiss();
                    EditText text = (EditText) info.findViewById(R.id.enterAmount);
                    String w = text.getText().toString();
                    Global.Log("Edit text changed = " + w, 3);
                    try {
                        Global.setBaseCurrencyAmount(Float.parseFloat(w));
                    } catch (NumberFormatException e){
                        Log.w(Global.TAG, e);
                    }
                    updateGUI();
                }
                return false;
            }
        });


        Button noButton = (Button) info.findViewById(R.id.noButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.cancel();
            }
        });

        Button yesButton = (Button) info.findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.dismiss();
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                String w = text.getText().toString();
                Global.Log("Edit text changed = " + w, 3);
                try {
                    Global.setBaseCurrencyAmount(Float.parseFloat(w));
                } catch (NumberFormatException e){
                    Log.w(Global.TAG, e);
                    return;
                }
                updateGUI();
            }
        });

        info.getWindow().setGravity(Gravity.TOP);

        info.show();



    }

    // change base currency amount
    public void enterAmount(final String code)
    {

        final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_amount_generic);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        // info.setTitle("Enter Amount");
        final TextView title = (TextView) info.findViewById(R.id.title);
        title.setText("Enter Amount");

        ImageView im0 = (ImageView) info.findViewById(R.id.delete);
        im0.setVisibility(View.INVISIBLE);

        im0 = (ImageView) info.findViewById(R.id.countryFlag2);
        im0.setVisibility(GONE);

        im0 = (ImageView) info.findViewById(R.id.arrowIcon);
        im0.setVisibility(GONE);

        TextView text0 = (TextView) info.findViewById(R.id.countryCode2);
        text0.setVisibility(GONE);

        TextView text1 = (TextView) info.findViewById(R.id.countryCode1);
        text1.setText(code);

        ImageView im = (ImageView) info.findViewById(R.id.countryFlag1);
        im.setImageResource(Global.getFlag(code));

        ImageView option1 = (ImageView) info.findViewById(R.id.resetAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("");
            }
        });

        option1 = (ImageView) info.findViewById(R.id.oneAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("1.00");
            }
        });

        EditText text = (EditText) info.findViewById(R.id.enterAmount);
        float f1 = Global.FOXCurrencyAll.get(code) / Global.FOXCurrencyAll.get(Global.getBaseCurrency())  * Global.getBaseCurrencyAmount();
        text.setText(String.format(java.util.Locale.US,"%.2f", f1)  );
        text.setCursorVisible(true);
        // text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.requestFocus();

        // Show keyboard
        info.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // listen for enter and close keyboard
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Global.Log("Got key = " + keyCode, 2);
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    info.dismiss();
                    EditText text = (EditText) info.findViewById(R.id.enterAmount);
                    String w = text.getText().toString();
                    Global.Log("Edit text changed = " + w, 3);
                    try {
                        float f1 = Float.parseFloat(w) * Global.FOXCurrencyAll.get(Global.getBaseCurrency()) / Global.FOXCurrencyAll.get(code);
                        Global.setBaseCurrencyAmount(f1);
                    } catch (NumberFormatException e){
                        Log.w(Global.TAG, e);
                    }
                    updateGUI();
                }
                return false;
            }
        });


        Button noButton = (Button) info.findViewById(R.id.noButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.cancel();
            }
        });

        Button yesButton = (Button) info.findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.dismiss();
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                String w = text.getText().toString();
                Global.Log("Edit text changed = " + w, 3);
                try {
                    float f1 = Float.parseFloat(w) * Global.FOXCurrencyAll.get(Global.getBaseCurrency()) / Global.FOXCurrencyAll.get(code);
                    Global.setBaseCurrencyAmount(f1);
                } catch (NumberFormatException e){
                    Log.w(Global.TAG, e);
                    return;
                }
                updateGUI();
            }
        });

        info.getWindow().setGravity(Gravity.TOP);

        info.show();



    }
    // change base currency amount
    public void displayOptions(String code)
    {

        final Dialog info = new Dialog(myContext);

        info.setContentView(R.layout.dialog_options);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        info.setTitle("Currency Actions");

        TextView text1 = (TextView) info.findViewById(R.id.countryCode);
        text1.setText(code.toUpperCase());

        ImageView im = (ImageView) info.findViewById(R.id.countryFlag);
        im.setImageResource(Global.getFlag(code));

        final String newCode = code;

        TextView option1 = (TextView) info.findViewById(R.id.option1);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                Global.FOXCurrencyMy.remove(newCode);
                Global.FOXCurrencyMy.put(Global.getBaseCurrency(), Global.getBaseCurrencyAmount());
                Float f1 = Global.FOXCurrencyAll.get(newCode) / Global.FOXCurrencyAll.get(Global.getBaseCurrency())  * Global.getBaseCurrencyAmount();
                Global.setBaseCurrencyAmount(f1);
                // Global.setBaseCurrencyAmount(Global.FOXCurrencyAll.get(newCode) );
                Global.setBaseCurrency(newCode);
                Global.writeFOXFile(Global.FOXCurrencyMy,Global.FILE_FOX_MY);
                info.cancel();
                updateGUI();
            }
        });

        SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(myContext);

        // Launch external website to show currency history etc
        TextView option2 = (TextView) info.findViewById(R.id.option2);

        boolean ui_history = (SP.getBoolean("ui_history",true));
        if (ui_history == true) {
            option2.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    // String url = "https://uk.finance.yahoo.com/quote/" + Global.getBaseCurrency() + newCode + "%3DX/chart?p=" + Global.getBaseCurrency() + newCode;

                    String url =  "https://markets.ft.com/data/currencies/tearsheet/summary?s=" + Global.getBaseCurrency() + newCode;

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        } else {
            option2.setVisibility(GONE);
        }


        TextView option3 = (TextView) info.findViewById(R.id.option3);
        option3.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                Global.FOXCurrencyMy.remove(newCode.toUpperCase());
                Global.writeFOXFile(Global.FOXCurrencyMy,Global.FILE_FOX_MY);
                info.cancel();
                updateGUI();
            }
        });

        TextView option4 = (TextView) info.findViewById(R.id.option4);
        option4.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                rateAlerts();
                setAlert(newCode);
                info.dismiss();
            }
        });

        TextView option5 = (TextView) info.findViewById(R.id.option5);
        boolean ui_transfer = (SP.getBoolean("ui_transfer",true));
        if (ui_transfer == true) {
            option5.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    showTransfer(newCode);
                    info.dismiss();
                }
            });
        } else {
            option5.setVisibility(GONE);
        }

        info.getWindow().setGravity(Gravity.TOP);

        info.show();



    }


    private void addCurrency()
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

        info.setTitle("Add Currency");

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
        tempCurrency.remove(Global.getBaseCurrency());

        for(TreeMap.Entry<String,Float> entry : Global.FOXCurrencyMy.entrySet()) {
            String key = entry.getKey();
            tempCurrency.remove(key);
        }

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
                Global.Log("Got item = " + code,2);
                // displayOptions(code);
                Global.FOXCurrencyMy.put(code.toUpperCase(), (float) 0);
                Global.writeFOXFile(Global.FOXCurrencyMy,Global.FILE_FOX_MY);
                updateGUI();
                info.dismiss();
            }
        });


        info.show();


    }



    private void showLogs(final String fileName, String title, final Dialog d)
    {

        final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_logs);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        // info.setTitle(title);
        final TextView text0 = (TextView) info.findViewById(R.id.title);
        text0.setText(title);

        text0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Do something here
                shareLog(fileName);
                return false;
            }
        });

        info.getWindow().setGravity(Gravity.TOP);

        ImageView im0 = (ImageView) info.findViewById(R.id.delete);
        im0.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                File file = new File(Global.getContext().getFilesDir(), fileName);
                file.delete();
                if (d != null) // delete the icon
                {
                    ImageView im0 = (ImageView) d.findViewById(R.id.delete);
                    im0.setVisibility(View.INVISIBLE);
                    TextView text0 = (TextView) d.findViewById(R.id.alerts_num);
                    text0.setVisibility(View.GONE);
                }
                info.cancel();
            }
        });

        if (fileName.equals(Global.FILE_LOG_ERRORS))
        {
            Global.cancelNotify("error".hashCode());

            TextView help = (TextView) info.findViewById(R.id.error_help);
            help.setVisibility(View.VISIBLE);

            help.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                }
            });
        } else {
            Global.cancelNotifyAlerts();
        }

        ArrayList<String> logBuffer = new ArrayList<String>();

        File file = new File(Global.getContext().getFilesDir(), fileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                logBuffer.add(line) ;
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Global.Log( "Error opening log file: " + e,2);
        }

        // Global.Log("Log Buffer: " + logBuffer.toString(),3);
        Collections.reverse(logBuffer);
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, logBuffer);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, R.layout.dialog_logs_row, logBuffer);

        ListView listView = (ListView) info.findViewById(R.id.listLogs);

        listView.setAdapter(adapter);

        listView.setClickable(true);

        /*
        Button button = (Button) info.findViewById(R.id.logsClose);
        button.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.cancel();
            }
        });
        */

        EditText myFilter = (EditText) info.findViewById(R.id.activity_main_filter_text);

        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Global.Log("Filter on text: " + s , 3);
                adapter.getFilter().filter(s.toString());
            }
        });

        info.show();

    }

    private void showErrorLogs(final String fileName, final String title, final Dialog d)
    {

        final Dialog info = new Dialog(myContext,R.style.SWFull3);
        // final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_logs);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position

        // Style3 full screen - not working - why the fuck?
        // maybe need relative layout and something at bottom of screen?
        wmlp.y = 0;   //y position

        int height = (int)(getResources().getDisplayMetrics().heightPixels*1);
        wmlp.height = height;

        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
        wmlp.width = width;

        // info.setTitle(title);
        final TextView text0 = (TextView) info.findViewById(R.id.title);
        text0.setText(title);

        text0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Do something here
                shareLog(fileName);
                return false;
            }
        });

        info.getWindow().setGravity(Gravity.TOP);

        gLogBuffer = new ArrayList<String>();



        ImageView im0 = (ImageView) info.findViewById(R.id.delete);
        im0.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                showLogMenu(v, fileName, d);
            }
        });

        if (fileName.equals(Global.FILE_LOG_ERRORS))
        {
            Global.cancelNotify("error".hashCode());

            TextView help = (TextView) info.findViewById(R.id.error_help);
            help.setVisibility(View.VISIBLE);

            help.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                }
            });
        } else {
            Global.cancelNotifyAlerts();
        }



        /*
        File file = new File(Global.getContext().getFilesDir(), fileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                logBuffer.add(line) ;
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Global.Log( "Error opening log file: " + e,2);
        }

        // Global.Log("Log Buffer: " + logBuffer.toString(),3);
        Collections.reverse(logBuffer);
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, logBuffer);
        // final ArrayAdapter<String> adapter = new ArrayAdapter<String>(myContext, R.layout.dialog_logs_row2, logBuffer);
        */

        // logAdapter and logBuffer need to be gloab;!

        if ( (fileName.equals(Global.FILE_LOG_ERRORS)) && (Global.getLogLevel() == 4) )
        {
            getLogFromADB(fileName);
        } else {
            getLogFromFile(fileName);
        }

        gLogAdapter = new LogAdapter(myContext, gLogBuffer);

        gLogListview = (ListView) info.findViewById(R.id.listLogs);

        gLogListview.setAdapter(gLogAdapter);

        gLogListview.setClickable(true);

        gLogListview.setSelection(gLogListview.getAdapter().getCount() - 1);

        /*
        Button button = (Button) info.findViewById(R.id.logsClose);
        button.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.cancel();
            }
        });
        */

        EditText myFilter = (EditText) info.findViewById(R.id.activity_main_filter_text);

        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Global.Log("Filter on text: " + s , 3);
                gLogAdapter.getFilter().filter(s.toString());
            }
        });


        info.show();

    }

    private void getLogFromADB(String fileName)
    {
        gLogBuffer.clear();

        if (gLogAdapter != null) {
            gLogAdapter.notifyDataSetInvalidated();
        }

        try {
            Process process = Runtime.getRuntime().exec("logcat -v time -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            // StringBuilder log=new StringBuilder();
            String line = "";
            // ArrayList<String> theLog = new ArrayList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                gLogBuffer.add(line);
            }

            /*
            if (gLogBuffer != null) {
                Collections.reverse(gLogBuffer);
            }
            */

            if (gLogListview != null)
            {
                gLogListview.setSelection(gLogListview.getAdapter().getCount() - 1);

            }
        } catch (IOException e) {
            Global.Log("Error getting ADB log" + e.toString(),2);
            // Toast.makeText(Global.getContext(), "Error getting ADB log" + e.toString(), Toast.LENGTH_SHORT).show();
            Global.infoMessage(myContext,"ADB Log","Error getting ADB log!",1);
            Global.setLogLevel(3);
            getLogFromFile(fileName);
        }


    }

    private void getLogFromFile(String fileName)
    {
        gLogBuffer.clear();

        if (gLogAdapter != null) {
            gLogAdapter.notifyDataSetInvalidated();
        }

        File file = new File(Global.getContext().getFilesDir(), fileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                gLogBuffer.add(line) ;
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Global.Log( "Error opening log file: " + e,2);
        }

        // Global.Log("Log Buffer: " + logBuffer.toString(),3);
        /*
        if (gLogBuffer != null) {
            Collections.reverse(gLogBuffer);
        }
        */

        if (gLogAdapter != null) {
            gLogAdapter.notifyDataSetChanged();
        }

        if (gLogListview != null)
        {
            gLogListview.setSelection(gLogListview.getAdapter().getCount() - 1);

        }
    }


    public void showOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.menu_main);
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        Menu m = popup.getMenu();
        MenuItem item;

        item = m.findItem(R.id.action_rate_alerts);
        File file = new File(getFilesDir() + "/" + Global.FILE_LOG_ALERTS);
        if(!file.exists()) {
            item.setTitle("Rate Alerts");
        } else {
            int n = 0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    n++;
                }
                br.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Global.Log( "Error opening log file: " + e,2);
            }
            item.setTitle("Rate Alerts (" + n +")");
        }

        // Always show - cos need to change log level
        /*
        item = m.findItem(R.id.action_view_logs);
        file = new File(getFilesDir() + "/" + Global.FILE_LOG_ERRORS);
        if(file.exists()) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        */

        item = m.findItem(R.id.action_force);

        if(Global.getErrorCount() > 0) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        invalidateOptionsMenu();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_help:
                        showHelp();
                        return true;
                    case R.id.action_add_currency:
                        addCurrency();
                        return true;
                    case R.id.action_rate_alerts:
                        rateAlerts();
                        return true;
                    case R.id.action_view_logs:
                        // Display error file...and then delete...
                        // Global.cancelNotify(0);
                        showErrorLogs(Global.FILE_LOG_ERRORS,"View Logs", null);
                        return true;
                    case R.id.action_force:
                        // Display error file...and then delete...
                        Global.updateCurrency();
                        return true;
                    case R.id.action_settings:
                        Intent i = new Intent(myContext, MySettingsActivity.class);
                        startActivity(i);
                        return true;
                    default:
                        return false;
                }
            }

        });
        popup.show();
    }

    //
    // Display the help screen
    //
    private void showTransfer(String code)
    {
        // https://transferwise.com/gb/currency-converter/gbp-to-usd-rate

        String url = "https://transferwise.com/gb/currency-converter/" + Global.getBaseCurrency().toLowerCase() + "-to-" + code.toLowerCase() + "-rate" + "?ref=fox@stargw.net";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    //
    // Display the help screen
    //
    private void showHelp()
    {

        String verName = "latest";
        try {

            PackageInfo pInfo = myContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            verName = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            Global.Log("Could not get version number", 3);
        }

        String url = "https://www.stargw.net/apps/fox/help.html?ver=" + verName;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }



    private void rateAlerts()
    {

        final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_ratealerts);

        // View v = info.findViewById(R.layout.dialog_ratealerts);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        // showLogs(Global.FILE_LOG_ALERTS,"Alert Log");

        final TextView title = (TextView) info.findViewById(R.id.title);
        title.setText("Rate Alerts");

        TextView text0 = (TextView) info.findViewById(R.id.alerts_num);
        File file = new File(getFilesDir() + "/" + Global.FILE_LOG_ALERTS);
        if(file.exists()) {
            int n = 0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    n++;
                }
                br.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Global.Log( "Error opening log file: " + e,2);
            }
            text0.setVisibility(View.VISIBLE);
            text0.setText(String.valueOf(n));
        } else {
            text0.setVisibility(GONE);
        }


        ImageView im0 = (ImageView) info.findViewById(R.id.delete);



        file = new File(getFilesDir() + "/" + Global.FILE_LOG_ALERTS);
        if(file.exists()) {
            im0.setImageResource(R.drawable.emoji_u1f514);
            im0.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    // show add rate dialog
                    showErrorLogs(Global.FILE_LOG_ALERTS,"Fired Rate Alerts", info);
                }
            });
            text0.setOnClickListener(new View.OnClickListener() {
                // @Override
                public void onClick(View v) {
                    // show add rate dialog
                    showErrorLogs(Global.FILE_LOG_ALERTS,"Fired Rate Alerts", info);
                }
            });
        } else {
            im0.setVisibility(View.INVISIBLE);
        }

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Global.Log("Detected view change",3);
            }
        });

        info.getWindow().setGravity(Gravity.TOP);

        // Need to take a copy cos adapter filter changes the object passed
        // BUT I DO NOT see this with any other arrayadapter filters? Why?
        // ArrayList<FOXRateAlertRecord> copy = new ArrayList<FOXRateAlertRecord>();
        // copy.addAll(Global.FOXRateAlerts);
        //
        // Need to clear the filter when dialog is dismissed to prevent adapter data loss
        // See GPX app - onclose:
        // gInternalGPXAdapter.getFilter().filter("");
        //
        adapterRateAlerts = new FOXRateAlertAdapter(myContext, Global.FOXRateAlerts);


        ListView listViewAdd = (ListView) info.findViewById(R.id.listRateAlerts);

        listViewAdd.setAdapter(adapterRateAlerts);

        listViewAdd.setClickable(true);

        listViewAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // FOXRateAlertRecord rateAlert = adapterRateAlerts.getItem(position);
                // Global.Log("Got item = " + code,2);
                // show an edit dialog...
                // how do we handle update GUI of the underlying dialog box?
                rateOptions(position);

            }
        });

        Button button = (Button) info.findViewById(R.id.addRateAlert);
        button.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                // show add rate dialog
                setAlert(null);
            }
        });

        /*
        button = (Button) info.findViewById(R.id.closeRateAlert);
        button.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                // show add rate dialog
                info.dismiss();
            }
        });
        */

        EditText myFilter = (EditText) info.findViewById(R.id.activity_main_filter_text);

        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Global.Log("Filter on text: " + s , 3);
                adapterRateAlerts.getFilter().filter(s.toString());
            }
        });

        info.show();


    }



    public void showLogMenu(View v, final String fileName, final Dialog d) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.menu_logs);
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        final Menu m = popup.getMenu();
        MenuItem item;

        if (fileName.equals(Global.FILE_LOG_ALERTS))
        {
            // m.removeItem(m.findItem(R.id.menu_logging_level));
            m.removeItem(R.id.menu_logging_level);
        } else {
            // m.removeItem(m.findItem(R.id.add_queue));

            // item = m.findItem(R.id.action_view);

            switch (Global.getLogLevel()) {
                case 0:
                    item = m.findItem(R.id.action_none);
                    item.setChecked(true);
                    break;
                case 1:
                    item = m.findItem(R.id.action_normal);
                    item.setChecked(true);
                    break;
                case 2:
                    item = m.findItem(R.id.action_detailed);
                    item.setChecked(true);
                    break;
                case 3:
                    item = m.findItem(R.id.action_debug);
                    item.setChecked(true);
                    break;
                case 4:
                    item = m.findItem(R.id.action_adb);
                    item.setChecked(true);
                    break;
                default:
                    break;
            }
        }


        // item.setChecked(Global.settingsDetailedLogs);

        // popup.getMenu().findItem(R.id.menu_name).setTitle(text);
        invalidateOptionsMenu();


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_refresh:
                        // d.dismiss();
                        // showErrorLogs(fileName,title, null);
                        if ( (fileName.equals(Global.FILE_LOG_ERRORS)) && (Global.getLogLevel() == 4) )
                        {
                            getLogFromADB(fileName);
                        } else {
                            getLogFromFile(fileName);
                        }
                        return true;

                    case R.id.menu_share:
                        // Logs.emailLog();
                        if ( (fileName.equals(Global.FILE_LOG_ERRORS)) && (Global.getLogLevel() == 4) )
                        {
                            shareLogADB();
                        } else {
                            shareLog(fileName);
                        }
                        return true;


                    case R.id.menu_delete:
                        if ( (fileName.equals(Global.FILE_LOG_ERRORS)) && (Global.getLogLevel() == 4) )
                        {
                            Global.infoMessage(myContext,"Delete Log","Cannot Delete ADB Log from App",1);
                        } else {
                            File file = new File(Global.getContext().getFilesDir(), fileName);
                            file.delete();
                            getLogFromFile(fileName);
                        }
                        if ( (fileName.equals(Global.FILE_LOG_ALERTS)) && (d != null) ) // delete the icon
                        {
                            ImageView im0 = (ImageView) d.findViewById(R.id.delete);
                            im0.setVisibility(View.INVISIBLE);
                            TextView text0 = (TextView) d.findViewById(R.id.alerts_num);
                            text0.setVisibility(View.GONE);
                        }

                        return true;
                    case R.id.action_none:
                        logWarn();
                        Global.setLogLevel(0);
                        getLogFromFile(fileName);
                        return true;
                    case R.id.action_normal:
                        logWarn();
                        Global.setLogLevel(1);
                        getLogFromFile(fileName);
                        return true;
                    case R.id.action_detailed:
                        logWarn();
                        Global.setLogLevel(2);
                        getLogFromFile(fileName);
                        return true;
                    case R.id.action_debug:
                        logWarn();
                        Global.setLogLevel(3);
                        getLogFromFile(fileName);
                        return true;
                    case R.id.action_adb:
                        logWarn();
                        Global.setLogLevel(4);
                        getLogFromADB(fileName);
                        return true;
                    default:
                        return false;
                }
            }

        });
        popup.show();
    }

    private void logWarn()
    {
        // String s = myContext.getString(R.string.activity_logs_menu_log_level);
        Global.infoMessage(myContext,"Log Level","New log level will take affect from now.",1);
    }

    // change base currency amount
    private void setAlert(String code)
    {

        final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_amount_generic);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        // info.setTitle("Set Rate Alert");
        final TextView title = (TextView) info.findViewById(R.id.title);
        title.setText("Set Rate Alert");

        ImageView im0 = (ImageView) info.findViewById(R.id.delete);
        im0.setVisibility(View.INVISIBLE);

        final TextView code1 = (TextView) info.findViewById(R.id.countryCode1);
        code1.setText(Global.getBaseCurrency());

        final ImageView im1 = (ImageView) info.findViewById(R.id.countryFlag1);
        im1.setImageResource(Global.getFlag(Global.getBaseCurrency()));

        final TextView code2 = (TextView) info.findViewById(R.id.countryCode2);
        if (code == null) {
            code = Global.FOXCurrencyMy.firstKey();
        }
        code2.setText(code);

        final ImageView im2 = (ImageView) info.findViewById(R.id.countryFlag2);
        im2.setImageResource(Global.getFlag(code));


        im1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code2 = (TextView) info.findViewById(R.id.countryCode2);
                String other = (String) code2.getText();
                pickCurrency(im1,code1,other); // need to pass in other values to stop picking duplicate...
            }
        });

        code1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code2 = (TextView) info.findViewById(R.id.countryCode2);
                String other = (String) code2.getText();
                pickCurrency(im1,code1, other);
            }
        });

        im2.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code1 = (TextView) info.findViewById(R.id.countryCode1);
                String other = (String) code1.getText();
                pickCurrency(im2,code2,other); // need to pass in other values to stop picking duplicate...
            }
        });

        code2.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                TextView code1 = (TextView) info.findViewById(R.id.countryCode1);
                String other = (String) code1.getText();
                pickCurrency(im2,code2, other);
            }
        });

        ImageView option1 = (ImageView) info.findViewById(R.id.resetAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("");
            }
        });


        final String otherCode = code;
        option1 = (ImageView) info.findViewById(R.id.oneAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("1.00");
            }
        });

        EditText text = (EditText) info.findViewById(R.id.enterAmount);
        float fl = Global.FOXCurrencyAll.get(otherCode) / Global.FOXCurrencyAll.get(Global.getBaseCurrency())  * Global.getBaseCurrencyAmount();
        text.setText(String.format(java.util.Locale.US,"%.3f",fl));
        text.setCursorVisible(true);
        // text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.requestFocus();

        // Show keyboard
        info.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        /*
        // listen for enter and close keyboard
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Global.Log("Got key = " + keyCode, 2);
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    info.dismiss();
                    EditText text = (EditText) info.findViewById(R.id.enterAmount);
                    String w = text.getText().toString();
                    Global.Log("Edit text changed = " + w, 3);
                }
                return false;
            }
        });
        */

        Button noButton = (Button) info.findViewById(R.id.noButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.cancel();
            }
        });

        Button yesButton = (Button) info.findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.dismiss();
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                String w = text.getText().toString();
                Global.Log("Edit text changed = " + w, 3);
                float f1 = 0f;
                try {
                    f1 = Float.parseFloat(w);
                } catch (NumberFormatException e){
                    Log.w(Global.TAG, e);
                    return;
                }
                FOXRateAlertRecord rateRecord = new FOXRateAlertRecord();
                TextView t = (TextView) info.findViewById(R.id.countryCode1);
                rateRecord.code1 = (String) t.getText();
                t = (TextView) info.findViewById(R.id.countryCode2);
                rateRecord.code2 = (String) t.getText();
                rateRecord.value = f1;
                rateRecord.oldValue = Global.FOXCurrencyAll.get(rateRecord.code2) / Global.FOXCurrencyAll.get(rateRecord.code1); // need to store the converted rate
                // how do we get the other values on this screen? Get from
                Global.FOXRateAlerts.add(rateRecord);
                adapterRateAlerts.reset(Global.FOXRateAlerts); // because of filter applied
                Global.writeRateAlertFile(Global.FOXRateAlerts,Global.FILE_FOX_ALERTS);
                // copy.add(rateRecord);
            }
        });

        info.getWindow().setGravity(Gravity.TOP);

        info.show();

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

        info.setTitle("Add Currency");

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
                Global.Log("Got item = " + code,2);
                // displayOptions(code);
                im1.setImageResource(Global.getFlag(code));
                code1.setText(code);
                info.dismiss();
            }
        });


        info.show();


    }

    private void rateOptions(final int k)
    {

        final Dialog info = new Dialog(myContext);
        info.requestWindowFeature(Window.FEATURE_NO_TITLE);

        info.setContentView(R.layout.dialog_amount_generic);

        Window window = info.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Show dialog in upper part of screen to stop it jumping when keyboard displayed
        WindowManager.LayoutParams wmlp = window.getAttributes();
        // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // wmlp.x = 100;   //x position
        wmlp.y = 200;   //y position

        final TextView title = (TextView) info.findViewById(R.id.title);
        title.setText("Set Rate Alert");


        ImageView im0 = (ImageView) info.findViewById(R.id.delete);

        Resources res = myContext.getResources();
        int resID = res.getIdentifier("ic_menu_delete" , "drawable", myContext.getPackageName());
        im0.setImageResource(resID);

        im0.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                Global.FOXRateAlerts.remove(k);
                adapterRateAlerts.reset(Global.FOXRateAlerts);
                Global.writeRateAlertFile(Global.FOXRateAlerts,Global.FILE_FOX_ALERTS);
                info.cancel();
            }
        });

        FOXRateAlertRecord rateAlert = Global.FOXRateAlerts.get(k);

        final TextView code1 = (TextView) info.findViewById(R.id.countryCode1);
        code1.setText(rateAlert.code1);

        final ImageView im1 = (ImageView) info.findViewById(R.id.countryFlag1);
        im1.setImageResource(Global.getFlag(rateAlert.code1));

        final TextView code2 = (TextView) info.findViewById(R.id.countryCode2);
        code2.setText(rateAlert.code2);

        final ImageView im2 = (ImageView) info.findViewById(R.id.countryFlag2);
        im2.setImageResource(Global.getFlag(rateAlert.code2));

        final TextView amount = (TextView) info.findViewById(R.id.enterAmount);
        amount.setText(String.format(java.util.Locale.US,"%.3f",rateAlert.value));

        info.getWindow().setGravity(Gravity.TOP);

        // Buttons
        ImageView option1 = (ImageView) info.findViewById(R.id.resetAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("");
            }
        });

        option1 = (ImageView) info.findViewById(R.id.oneAmount);
        option1.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                text.setText("1.00");
            }
        });

        Button noButton = (Button) info.findViewById(R.id.noButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.cancel();
            }
        });

        Button yesButton = (Button) info.findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                info.dismiss();
                EditText text = (EditText) info.findViewById(R.id.enterAmount);
                String w = text.getText().toString();
                Global.Log("Edit text changed = " + w, 3);
                float f1 = 0f;
                try {
                    f1 = Float.parseFloat(w);
                } catch (NumberFormatException e){
                    Log.w(Global.TAG, e);
                    return;
                }
                Global.FOXRateAlerts.get(k).value = f1;
                adapterRateAlerts.reset(Global.FOXRateAlerts);
                Global.writeRateAlertFile(Global.FOXRateAlerts,Global.FILE_FOX_ALERTS);
            }
        });

        info.show();

    }

    // share log
    public static void shareLogOLD(String fn)
    {
        File file1 = new File(Global.getContext().getFilesDir(), fn);

        String iPath = Global.getContext().getExternalCacheDir() + "/" + fn + ".txt";
        File file2 = new File(iPath);

        try {
            Global.copy(file1,file2);
        } catch (IOException e) {
            Global.Log("Error while copying!", 2);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        // Uri bmpUri = Uri.fromFile(file);
        Uri bmpUri = Uri.fromFile(file2);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        // startActivity(Intent.createChooser(sharingIntent, "Share"));
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Intent chooserIntent = Intent.createChooser(sharingIntent, "Share");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Global.getContext().startActivity(Intent.createChooser(sharingIntent, "Share"));
        Global.getContext().startActivity(chooserIntent);

    }


    public static void shareLog(String fn)
    {

        File f = new File(Global.getContext().getFilesDir(),fn);

        Global.Log("READ PATH = " + f.toString(),3);

        // This provides a read only content:// for other apps
        Uri uri2 = FileProvider.getUriForFile(Global.getContext(),"net.stargw.fox.fileprovider",f);

        Global.Log("URI PATH = " + uri2.toString(),3);

        Intent intent2 = new Intent(Intent.ACTION_SEND);
            intent2.putExtra(Intent.EXTRA_STREAM, uri2);
            intent2.setType("text/plain");
            intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Global.getContext().startActivity(intent2);

    }

    public static void shareLogADB()
    {
        File myDirPath = new File(Global.getContext().getCacheDir(), "temp");
        myDirPath.mkdirs();

        File file = new File(myDirPath, Global.FILE_LOG_ERRORS);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (IOException e) {
            // Toast.makeText(Global.getContext(), "Error Exporting to file!", Toast.LENGTH_SHORT).show();
            Global.infoMessage(Global.getContext(),"ADB Log","Error getting ADB log!",1);
            Global.Log("Error getting ADB log" + e.toString(),3);
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec("logcat -v time -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                // write line to file
                line = line + "\n";
                fos.write(line.getBytes() );
            }
            fos.close();
        } catch (IOException e) {
            Global.Log("Error getting ADB log" + e.toString(),2);
            Global.infoMessage(Global.getContext(),"ADB Log","Error getting ADB log!",1);
            return;
        }

        // This provides a read only content:// for other apps
        Uri uri2 = FileProvider.getUriForFile(Global.getContext(),"net.stargw.fox.fileprovider",file);

        Intent intent2 = new Intent(Intent.ACTION_SEND);
        intent2.putExtra(Intent.EXTRA_STREAM, uri2);
        intent2.setType("text/plain"); // default!
        intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Global.getContext().startActivity(intent2);

    }

}