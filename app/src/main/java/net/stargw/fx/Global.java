package net.stargw.fx;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by swatts on 10/03/18.
 */

public class Global extends Application {

    private static Context mContext;

    // public static int errorCount = 0;

    // public static boolean DEBUG = false;

    public static String STARGW = "https://www.stargw.net/fox3.json";
    public static String ECB = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public static final String TAG = "FOX-Currency";

    public static final String FILE_FOX_CACHE = "fox_cache";
    public static final String FILE_FOX_MY = "fox_my";

    public static final String FILE_FOX_ALERTS = "fox_alerts";

    public static final String FILE_LOG_ERRORS = "fox_errors.txt";
    public static final String FILE_LOG_ALERTS = "fox_triggered_alerts.txt";

    // public static final int FOX_INTERVAL = 60; // minutes

    static final String UPDATEALARM = "net.stargw.fox.intent.action.UPDATEALARM";
    static final String SCREEN_REFRESH_INTENT = "net.stargw.fox.intent.action.REFRESH";
    static final String FOX_OPEN_ERROR = "net.stargw.fox.intent.action.open.error";
    static final String FOX_OPEN_ALERTS = "net.stargw.fox.intent.action.open.alerts";

    static final String UPDATEWIDGET1 = "net.stargw.fox.update.UPDATE.WIDGET1";

    public static TreeMap<String, Float> FOXCurrencyAll;
    public static TreeMap<String, Float> FOXCurrencyMy;
    public static TreeMap<String, Integer> CodeToCurrency;
    public static ArrayList<FOXRateAlertRecord> FOXRateAlerts;

    public static String FOXBase = "GBP";
    private static long FOXUpdateTime;

    private static NotificationManager notificationManager;
    private static NotificationChannel channel1;
    private static NotificationChannel channel2;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        /*
        if (BuildConfig.DEBUG) {
            DEBUG = true;
            DEBUG = false;
        } else {
            DEBUG = false;
        }
        */

        // Tidy up and delete Cache file
        File myDirPath = new File(Global.getContext().getCacheDir(), "temp");
        File file = new File(myDirPath, Global.FILE_LOG_ERRORS);
        file.delete();

        Global.Log("onCreate - Global",2);

        Global.CodeToCurrency = new TreeMap<String, Integer>();
        /*
        Global.CodeToCurrency.put("GBP", R.string.GBP);
        Global.CodeToCurrency.put("USD", R.string.USD);
        Global.CodeToCurrency.put("EUR", R.string.EUR);
        Global.CodeToCurrency.put("AED", R.string.AED);
        */
        Global.CodeToCurrency.put("AED", R.string.AED);
        Global.CodeToCurrency.put("AFN", R.string.AFN);
        Global.CodeToCurrency.put("ALL", R.string.ALL);
        Global.CodeToCurrency.put("AMD", R.string.AMD);
        Global.CodeToCurrency.put("ANG", R.string.ANG);
        Global.CodeToCurrency.put("AOA", R.string.AOA);
        Global.CodeToCurrency.put("ARS", R.string.ARS);
        Global.CodeToCurrency.put("AUD", R.string.AUD);
        Global.CodeToCurrency.put("AWG", R.string.AWG);
        Global.CodeToCurrency.put("AZN", R.string.AZN);
        Global.CodeToCurrency.put("BAM", R.string.BAM);
        Global.CodeToCurrency.put("BBD", R.string.BBD);
        Global.CodeToCurrency.put("BDT", R.string.BDT);
        Global.CodeToCurrency.put("BGN", R.string.BGN);
        Global.CodeToCurrency.put("BHD", R.string.BHD);
        Global.CodeToCurrency.put("BIF", R.string.BIF);
        Global.CodeToCurrency.put("BMD", R.string.BMD);
        Global.CodeToCurrency.put("BND", R.string.BND);
        Global.CodeToCurrency.put("BOB", R.string.BOB);
        Global.CodeToCurrency.put("BRL", R.string.BRL);
        Global.CodeToCurrency.put("BSD", R.string.BSD);
        Global.CodeToCurrency.put("BTN", R.string.BTN);
        Global.CodeToCurrency.put("BWP", R.string.BWP);
        Global.CodeToCurrency.put("BYN", R.string.BYN);
        Global.CodeToCurrency.put("BZD", R.string.BZD);
        Global.CodeToCurrency.put("CAD", R.string.CAD);
        Global.CodeToCurrency.put("CDF", R.string.CDF);
        Global.CodeToCurrency.put("CHF", R.string.CHF);
        Global.CodeToCurrency.put("CLP", R.string.CLP);
        Global.CodeToCurrency.put("CNY", R.string.CNY);
        Global.CodeToCurrency.put("COP", R.string.COP);
        Global.CodeToCurrency.put("CRC", R.string.CRC);
        Global.CodeToCurrency.put("CUC", R.string.CUC);
        Global.CodeToCurrency.put("CUP", R.string.CUP);
        Global.CodeToCurrency.put("CVE", R.string.CVE);
        Global.CodeToCurrency.put("CZK", R.string.CZK);
        Global.CodeToCurrency.put("DJF", R.string.DJF);
        Global.CodeToCurrency.put("DKK", R.string.DKK);
        Global.CodeToCurrency.put("DOP", R.string.DOP);
        Global.CodeToCurrency.put("DZD", R.string.DZD);
        Global.CodeToCurrency.put("EGP", R.string.EGP);
        Global.CodeToCurrency.put("ERN", R.string.ERN);
        Global.CodeToCurrency.put("ETB", R.string.ETB);
        Global.CodeToCurrency.put("EUR", R.string.EUR);
        Global.CodeToCurrency.put("FJD", R.string.FJD);
        Global.CodeToCurrency.put("FKP", R.string.FKP);
        Global.CodeToCurrency.put("GBP", R.string.GBP);
        Global.CodeToCurrency.put("GEL", R.string.GEL);
        Global.CodeToCurrency.put("GGP", R.string.GGP);
        Global.CodeToCurrency.put("GHS", R.string.GHS);
        Global.CodeToCurrency.put("GIP", R.string.GIP);
        Global.CodeToCurrency.put("GMD", R.string.GMD);
        Global.CodeToCurrency.put("GNF", R.string.GNF);
        Global.CodeToCurrency.put("GTQ", R.string.GTQ);
        Global.CodeToCurrency.put("GYD", R.string.GYD);
        Global.CodeToCurrency.put("HKD", R.string.HKD);
        Global.CodeToCurrency.put("HNL", R.string.HNL);
        Global.CodeToCurrency.put("HRK", R.string.HRK);
        Global.CodeToCurrency.put("HTG", R.string.HTG);
        Global.CodeToCurrency.put("HUF", R.string.HUF);
        Global.CodeToCurrency.put("IDR", R.string.IDR);
        Global.CodeToCurrency.put("ILS", R.string.ILS);
        Global.CodeToCurrency.put("IMP", R.string.IMP);
        Global.CodeToCurrency.put("INR", R.string.INR);
        Global.CodeToCurrency.put("IQD", R.string.IQD);
        Global.CodeToCurrency.put("IRR", R.string.IRR);
        Global.CodeToCurrency.put("ISK", R.string.ISK);
        Global.CodeToCurrency.put("JEP", R.string.JEP);
        Global.CodeToCurrency.put("JMD", R.string.JMD);
        Global.CodeToCurrency.put("JOD", R.string.JOD);
        Global.CodeToCurrency.put("JPY", R.string.JPY);
        Global.CodeToCurrency.put("KES", R.string.KES);
        Global.CodeToCurrency.put("KGS", R.string.KGS);
        Global.CodeToCurrency.put("KHR", R.string.KHR);
        Global.CodeToCurrency.put("KMF", R.string.KMF);
        Global.CodeToCurrency.put("KPW", R.string.KPW);
        Global.CodeToCurrency.put("KRW", R.string.KRW);
        Global.CodeToCurrency.put("KWD", R.string.KWD);
        Global.CodeToCurrency.put("KYD", R.string.KYD);
        Global.CodeToCurrency.put("KZT", R.string.KZT);
        Global.CodeToCurrency.put("LAK", R.string.LAK);
        Global.CodeToCurrency.put("LBP", R.string.LBP);
        Global.CodeToCurrency.put("LKR", R.string.LKR);
        Global.CodeToCurrency.put("LRD", R.string.LRD);
        Global.CodeToCurrency.put("LSL", R.string.LSL);
        Global.CodeToCurrency.put("LYD", R.string.LYD);
        Global.CodeToCurrency.put("MAD", R.string.MAD);
        Global.CodeToCurrency.put("MDL", R.string.MDL);
        Global.CodeToCurrency.put("MGA", R.string.MGA);
        Global.CodeToCurrency.put("MKD", R.string.MKD);
        Global.CodeToCurrency.put("MMK", R.string.MMK);
        Global.CodeToCurrency.put("MNT", R.string.MNT);
        Global.CodeToCurrency.put("MOP", R.string.MOP);
        Global.CodeToCurrency.put("MRU", R.string.MRU);
        Global.CodeToCurrency.put("MUR", R.string.MUR);
        Global.CodeToCurrency.put("MVR", R.string.MVR);
        Global.CodeToCurrency.put("MWK", R.string.MWK);
        Global.CodeToCurrency.put("MXN", R.string.MXN);
        Global.CodeToCurrency.put("MYR", R.string.MYR);
        Global.CodeToCurrency.put("MZN", R.string.MZN);
        Global.CodeToCurrency.put("NAD", R.string.NAD);
        Global.CodeToCurrency.put("NGN", R.string.NGN);
        Global.CodeToCurrency.put("NIO", R.string.NIO);
        Global.CodeToCurrency.put("NOK", R.string.NOK);
        Global.CodeToCurrency.put("NPR", R.string.NPR);
        Global.CodeToCurrency.put("NZD", R.string.NZD);
        Global.CodeToCurrency.put("OMR", R.string.OMR);
        Global.CodeToCurrency.put("PAB", R.string.PAB);
        Global.CodeToCurrency.put("PEN", R.string.PEN);
        Global.CodeToCurrency.put("PGK", R.string.PGK);
        Global.CodeToCurrency.put("PHP", R.string.PHP);
        Global.CodeToCurrency.put("PKR", R.string.PKR);
        Global.CodeToCurrency.put("PLN", R.string.PLN);
        Global.CodeToCurrency.put("PYG", R.string.PYG);
        Global.CodeToCurrency.put("QAR", R.string.QAR);
        Global.CodeToCurrency.put("RON", R.string.RON);
        Global.CodeToCurrency.put("RSD", R.string.RSD);
        Global.CodeToCurrency.put("RUB", R.string.RUB);
        Global.CodeToCurrency.put("RWF", R.string.RWF);
        Global.CodeToCurrency.put("SAR", R.string.SAR);
        Global.CodeToCurrency.put("SBD", R.string.SBD);
        Global.CodeToCurrency.put("SCR", R.string.SCR);
        Global.CodeToCurrency.put("SDG", R.string.SDG);
        Global.CodeToCurrency.put("SEK", R.string.SEK);
        Global.CodeToCurrency.put("SGD", R.string.SGD);
        Global.CodeToCurrency.put("SHP", R.string.SHP);
        Global.CodeToCurrency.put("SLL", R.string.SLL);
        Global.CodeToCurrency.put("SOS", R.string.SOS);
        Global.CodeToCurrency.put("SPL", R.string.SPL);
        Global.CodeToCurrency.put("SRD", R.string.SRD);
        Global.CodeToCurrency.put("STN", R.string.STN);
        Global.CodeToCurrency.put("SVC", R.string.SVC);
        Global.CodeToCurrency.put("SYP", R.string.SYP);
        Global.CodeToCurrency.put("SZL", R.string.SZL);
        Global.CodeToCurrency.put("THB", R.string.THB);
        Global.CodeToCurrency.put("TJS", R.string.TJS);
        Global.CodeToCurrency.put("TMT", R.string.TMT);
        Global.CodeToCurrency.put("TND", R.string.TND);
        Global.CodeToCurrency.put("TOP", R.string.TOP);
        Global.CodeToCurrency.put("TRY", R.string.TRY);
        Global.CodeToCurrency.put("TTD", R.string.TTD);
        Global.CodeToCurrency.put("TVD", R.string.TVD);
        Global.CodeToCurrency.put("TWD", R.string.TWD);
        Global.CodeToCurrency.put("TZS", R.string.TZS);
        Global.CodeToCurrency.put("UAH", R.string.UAH);
        Global.CodeToCurrency.put("UGX", R.string.UGX);
        Global.CodeToCurrency.put("USD", R.string.USD);
        Global.CodeToCurrency.put("UYU", R.string.UYU);
        Global.CodeToCurrency.put("UZS", R.string.UZS);
        Global.CodeToCurrency.put("VEF", R.string.VEF);
        Global.CodeToCurrency.put("VND", R.string.VND);
        Global.CodeToCurrency.put("VUV", R.string.VUV);
        Global.CodeToCurrency.put("WST", R.string.WST);
        Global.CodeToCurrency.put("XAF", R.string.XAF);
        Global.CodeToCurrency.put("XCD", R.string.XCD);
        Global.CodeToCurrency.put("XDR", R.string.XDR);
        Global.CodeToCurrency.put("XOF", R.string.XOF);
        Global.CodeToCurrency.put("XPF", R.string.XPF);
        Global.CodeToCurrency.put("YER", R.string.YER);
        Global.CodeToCurrency.put("ZAR", R.string.ZAR);
        Global.CodeToCurrency.put("ZMW", R.string.ZMW);
        Global.CodeToCurrency.put("ZWD", R.string.ZWD);

        Global.FOXRateAlerts = Global.readRateAlertFile(Global.FILE_FOX_ALERTS);

        Global.FOXCurrencyAll = Global.readFOXFile(Global.FILE_FOX_CACHE);

        if ( Global.FOXCurrencyAll.size() == 0) {
            Global.Log("FOXCurrencyAll empty",2);
            Global.FOXCurrencyAll = new TreeMap<String, Float>();
            // Global.FOXCurrencyAll = new TreeMap<String, Float> (new FOXRecordSort());
            Global.FOXCurrencyAll.put("GBP", (float) 1);
            Global.FOXCurrencyAll.put("USD", (float) 1);
            Global.FOXCurrencyAll.put("EUR", (float) 1);
        }

        Global.FOXCurrencyMy = Global.readFOXFile(Global.FILE_FOX_MY);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannels();

        setAlarm();  // move to global

        updateMyWidgets();
        checklastUpdate();

    }

    public static Context getContext() {
        return mContext;
    }


    public static void Log(String buf, int level) {
        /*
        if ( (DEBUG == false) && (level > 0) ) {
            // don't log
            return;
        }
        */

        if (getLogLevel() > 2) {
            Log.w(TAG, buf);
        }

        if (getLogLevel() >= level) {
            Date timeNow = new Date();
            Calendar newDate = new GregorianCalendar();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

            newDate.setTime(timeNow);
            String humanDate = format.format(newDate.getTime());

            String logBuffer = humanDate + " " + level + " " + buf;

            Global.fileAppend(FILE_LOG_ERRORS, logBuffer);
        }

    }

    public static int getErrorCount() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        int s = p.getInt("errorCount", 0);
        Log("Get Error Count: " + s,3);
        return s;
    }

    public static void setErrorCount(int s) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        p.edit().putInt("errorCount", s).apply();
        Log("Set Error Count: " + s,3);
    }

    public static int getLogLevel() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        int s = p.getInt("logLevel", 0);
        // Log("Get Log Level: " + s,3);
        return s;
    }

    public static void setLogLevel(int s) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        p.edit().putInt("logLevel", s).apply();
        Log("Set Log Level: " + s,3);
    }

    public static void increaseErrorCount() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        int s = p.getInt("errorCount", 0);
        s = s + 1;
        Log("Set Error Count: " + s,3);
        p.edit().putInt("errorCount", s).apply();
    }

    public static Float getBaseCurrencyAmount() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        Float s = p.getFloat("baseCurrencyAmount", 1f);

        return s;
    }

    public static void setBaseCurrencyAmount(Float s) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());

        p.edit().putFloat("baseCurrencyAmount", s).apply();
    }

    public static String getBaseCurrency() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        String s = p.getString("baseCurrency", "GBP");

        if(!Global.FOXCurrencyAll.containsKey(s))
        {
            s ="GBP"; // what happens if this is not there either??
            setBaseCurrency(s);
        }

        return s.toUpperCase();
    }

    public static void setBaseCurrency(String s) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());

        p.edit().putString("baseCurrency", s.toUpperCase()).apply();
    }

    public static long getUpdateTime() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        long t = p.getLong("updateTime", 0);

        return t;
    }


    public static void setUpdateTime() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());

        Date timeNow = new Date();
        long t = timeNow.getTime();

        p.edit().putLong("updateTime", t).apply();
    }

    public static String getUpdateSource() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());
        String source = p.getString("updateSource", "none");

        return source;
    }


    private static void setUpdateSource(String source) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Global.getContext());

        p.edit().putString("updateSource", source).apply();
    }

    public static void writeFOXFile(Map<String, Float> weights, String f) {
        File appDir = mContext.getFilesDir();
        File mypath = new File(appDir, f);
        try {
            FileOutputStream myFile = new FileOutputStream(mypath);
            // FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(myFile);
            os.writeObject(weights);
            os.close();
            myFile.close();
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    public static TreeMap<String, Float> readFOXFile(String f) {
        Properties properties = new Properties();
        File appDir = mContext.getFilesDir();
        File mypath = new File(appDir, f);

        TreeMap<String, Float> simpleClass = new TreeMap<String, Float>();

        // TreeMap<String, Float> simpleClass = new TreeMap<String, Float> (new FOXRecordSort());  // This is the comparitor because it needs to be searalisable as well

        /*
        TreeMap<String, Float> simpleClass = new TreeMap<String, Float>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        */

        try {
            FileInputStream myFile = new FileInputStream(mypath);
            ObjectInputStream is = new ObjectInputStream(myFile);
            try {
                simpleClass = (TreeMap<String, Float>) is.readObject();
            } catch (ClassNotFoundException e) {
                Log.w(TAG, e);
            }
            is.close();
            myFile.close();
        } catch (FileNotFoundException e) {
            // do stuff here..
            Global.Log("File not found: " + f, 1);
        } catch (IOException e) {
            Log.w(TAG, e);
        }

        return simpleClass;
    }


    public static void writeRateAlertFile(ArrayList<FOXRateAlertRecord> weights, String f) {
        File appDir = mContext.getFilesDir();
        File mypath = new File(appDir, f);
        try {
            FileOutputStream myFile = new FileOutputStream(mypath);
            // FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(myFile);
            os.writeObject(weights);
            os.close();
            myFile.close();
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    public static ArrayList<FOXRateAlertRecord> readRateAlertFile(String f) {
        Properties properties = new Properties();
        File appDir = mContext.getFilesDir();
        File mypath = new File(appDir, f);

        ArrayList<FOXRateAlertRecord> simpleClass = new ArrayList<FOXRateAlertRecord>();

        // TreeMap<String, Float> simpleClass = new TreeMap<String, Float> (new FOXRecordSort());  // This is the comparitor because it needs to be searalisable as well

        /*
        TreeMap<String, Float> simpleClass = new TreeMap<String, Float>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        */

        try {
            FileInputStream myFile = new FileInputStream(mypath);
            ObjectInputStream is = new ObjectInputStream(myFile);
            try {
                simpleClass = (ArrayList<FOXRateAlertRecord>) is.readObject();
            } catch (ClassNotFoundException e) {
                Log.w(TAG, e);
            } catch (Exception e) {
                Log.w(TAG, e);
            }
            is.close();
            myFile.close();
        } catch (FileNotFoundException e) {
            // do stuff here..
            Global.Log("File not found: " + f, 1);
        } catch (IOException e) {
            Log.w(TAG, e);
        }

        return simpleClass;
    }

    public static void updateCurrency() {

        Thread thread = new Thread() {
            @Override
            public void run() {

                Date timeNow = new Date();
                Calendar newDate = new GregorianCalendar();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

                newDate.setTime(timeNow);
                String humanDate = format.format(newDate.getTime());

                String urlRates = "";

                SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean ecb = !(SP.getBoolean("use_open",true));

                Global.Log("Use source ECB = " + ecb,2);

                if(ecb == true)
                {
                    urlRates = ECB;
                } else {
                    String api = SP.getString("openexchange","none");
                    Global.Log("API key = " + api,2);
                    if (api.equalsIgnoreCase("none"))
                    {
                        urlRates = ECB;
                        ecb = true;
                        // correct the settings
                        SP.edit().putBoolean("use_ecb",true).apply();
                        SP.edit().putBoolean("use_open",false).apply();
                    } else {
                        if (api.equalsIgnoreCase("steve"))
                        {
                            urlRates = STARGW + "?app_id=" + api;
                        } else {
                            urlRates = "https://openexchangerates.org/api/latest.json?app_id=" + api;
                        }
                    }
                }

                Global.Log("URL = " + urlRates,2);

                URL url = null;
                try {
                    url = new URL(urlRates);
                } catch (Exception e) {
                    String errorMessage = "Bad URL: \" + e.toString()";
                    Global.Log(errorMessage, 0);
                    notifyError(humanDate + " " + errorMessage);
                    // Global.fileAppend(FILE_LOG_ERRORS, errorMessage);
                }

                String message = "Trying URL: " + urlRates;
                Global.Log(message, 2);


                HttpURLConnection httpCon = null;
                try {
                    httpCon = (HttpURLConnection) url.openConnection();
                    // httpCon.setDoOutput(true);
                    httpCon.setRequestMethod("GET");
                    httpCon.setReadTimeout(10000);
                    httpCon.setConnectTimeout(15000);
                    // httpCon.setDoInput(true);

                    // OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
                    // out.write("https://www.stargw.net/fox2.json");
                    // out.close();
                } catch (Exception e) {
                    Global.increaseErrorCount();
                    String errorMessage = "Network Error: " + e.toString() + " [" + Global.getErrorCount() + "]";
                    Global.Log(errorMessage, 0);
                    // Global.fileAppend(FILE_LOG_ERRORS, errorMessage);
                    if (Global.getErrorCount() < 3) {
                        notifyError(humanDate + " " + errorMessage);
                    }
                    // Toast.makeText(mContext.getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }

                String response = "";

                try {
                    int responseCode = httpCon.getResponseCode();
                    // Log.d(TAG, "GOT RESPONSE: " + Integer.toString(responseCode) + " " + httpCon.getResponseMessage());

                    Global.Log("HTTP GOT RESPONSE: " + Integer.toString(responseCode), 2);

                    if (responseCode == HttpsURLConnection.HTTP_OK)
                    {

                        if (Global.FOXCurrencyAll != null)
                        {
                            // should we do some more checking before clearing?
                            Global.FOXCurrencyAll.clear();
                        } else {
                            Global.FOXCurrencyAll = new TreeMap<String, Float>();
                        }

                        // should do a new Treemap here??
                        if (ecb == true)
                        {
                            parseECB(httpCon.getInputStream());
                            setUpdateSource("ECB");
                            Global.Log("Received updates from ECB",1);
                        } else {
                            parseOpenExchangeRates(httpCon.getInputStream());
                            setUpdateSource("OER");
                            Global.Log("Received updates from OER",1);
                        }

                        Global.writeFOXFile(Global.FOXCurrencyAll, FILE_FOX_CACHE);

                        // Log monitored currencies
                        Global.FOXCurrencyMy = Global.readFOXFile(Global.FILE_FOX_MY);
                        if (Global.FOXCurrencyMy.size() == 0 )
                        {
                            // Global.FOXCurrencyMy.put("GBP", (float) 1);
                            Global.FOXCurrencyMy.put("THB", (float) 0);
                            Global.FOXCurrencyMy.put("USD", (float) 0);
                            Global.FOXCurrencyMy.put("EUR", (float) 0);
                            Global.FOXCurrencyMy.put("SGD", (float) 0);
                        }

                        for(TreeMap.Entry<String,Float> entry : Global.FOXCurrencyMy.entrySet()) {
                            String key = entry.getKey();
                            if (Global.FOXCurrencyAll.containsKey(Global.getBaseCurrency()) && Global.FOXCurrencyAll.containsKey(key)) {
                                float f1 = Global.FOXCurrencyAll.get(key) / Global.FOXCurrencyAll.get(Global.getBaseCurrency())  * Global.getBaseCurrencyAmount();
                                Global.Log(key + " = " + String.format(java.util.Locale.US,"%.3f", f1), 1);
                            }
                        }
                        /*
                        for(int i = 0, l = Global.FOXCurrencyMy.size(); i < l; i++) {
                            FOXCurrencyRecord k = Global.FOXCurrencyMy.get(i);
                            Global.Log(key + " = " + entry., 1);
                        }
                        */

                        setUpdateTime();
                        checkRateAlerts();

                        // send a broadcast
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Global.SCREEN_REFRESH_INTENT);
                        mContext.sendBroadcast(broadcastIntent);
                        cancelNotify("error".hashCode());
                        Global.setErrorCount(0);
                        updateMyWidgets();

                        httpCon.disconnect();
                    } else {
                        String errorMessage = "Got Reply: " + Integer.toString(responseCode) + " " + httpCon.getResponseMessage();
                        Global.Log(errorMessage, 0);
                        // Global.fileAppend(FILE_LOG_ERRORS, errorMessage);
                        Global.increaseErrorCount();
                        if (Global.getErrorCount() < 3) {
                            notifyError(humanDate + " " + errorMessage);
                        }
                    }

                    // in.close();
                } catch (Exception e) {
                    Global.increaseErrorCount();
                    String errorMessage = "Network Error: " + e.toString() + " [" + Global.getErrorCount() + "]";
                    Global.Log(errorMessage, 0);
                    // Global.fileAppend(FILE_LOG_ERRORS, errorMessage);
                    if (Global.getErrorCount() < 3) {
                        notifyError(humanDate + " " + errorMessage);
                    }
                }
            }
        };
        thread.start();

    }

    private static void parseOpenExchangeRates(InputStream inputStream)
    {
        Date timeNow = new Date();
        Calendar newDate = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

        newDate.setTime(timeNow);
        String humanDate = format.format(newDate.getTime());

        InputStream in = new BufferedInputStream(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                // Global.Log(line,3);
                String reg = "\\s*\\\"(.*?)\\\":\\s+(.*?),";

                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(line);
                if ((matcher.find()) && (matcher.groupCount() == 2)) {
                    // Global.Log("Match = " + matcher.group(1) + " ==> " +  matcher.group(2) ,3);
                    if (matcher.group(1).length() == 3) {
                        try {
                            float f1 = Float.parseFloat(matcher.group(2));
                            Global.FOXCurrencyAll.put(matcher.group(1), f1);
                        } catch (Exception e) {
                            String errorMessage = "Number Error: " + e.toString();
                            Global.Log(errorMessage, 1);
                        }
                    }

                }


            }
        } catch (Exception e) {
            Global.increaseErrorCount();
            String errorMessage = "Network Error: " + e.toString() + " [" + Global.getErrorCount() + "]";
            Global.Log(errorMessage, 0);
            // Global.fileAppend(FILE_LOG_ERRORS, errorMessage);
            if (Global.getErrorCount() < 3) {
                notifyError(humanDate + " " + errorMessage);
            }
        }
    }

    private static void parseECB(InputStream inputStream)
    {
        Date timeNow = new Date();
        Calendar newDate = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

        newDate.setTime(timeNow);
        String humanDate = format.format(newDate.getTime());

        // <Cube currency="USD" rate="1.1698"/>
        // push EUR first

        Global.FOXCurrencyAll.put("EUR", 1f);

        InputStream in = new BufferedInputStream(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                // Global.Log(line,3);
                String reg = ".*?=\\\'(.*?)\\\' .*rate=\\\'(.*?)\\\'";

                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(line);
                if ((matcher.find()) && (matcher.groupCount() == 2)) {
                    // Global.Log("Match = " + matcher.group(1) + " ==> " +  matcher.group(2) ,3);
                    if (matcher.group(1).length() == 3) {
                        try {
                            float f1 = Float.parseFloat(matcher.group(2));
                            Global.FOXCurrencyAll.put(matcher.group(1), f1);
                        } catch (Exception e) {
                            String errorMessage = "Number Error: " + e.toString();
                            Global.Log(errorMessage, 1);
                        }
                    }

                }


            }
        } catch (Exception e) {
            Global.increaseErrorCount();
            String errorMessage = "Network Error: " + e.toString() + " [" + Global.getErrorCount() + "]";
            Global.Log(errorMessage, 0);
            // Global.fileAppend(FILE_LOG_ERRORS, errorMessage);
            if (Global.getErrorCount() < 3) {
                notifyError(humanDate + " " + errorMessage);
            }
        }
    }

    public static void updateMyWidgets() {

        Global.Log("Updating widgets", 2);

        AppWidgetManager man = AppWidgetManager.getInstance(mContext);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(mContext, Widget1.class));
        for (int i = 0; i < ids.length; i++) {
            int appWidgetId = ids[i];
            Global.Log("Global Update Widget " + appWidgetId,2);
            Intent updateIntent = new Intent();
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            // updateIntent.setAction(UPDATEWIDGET1);

            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            updateIntent.putExtra("VIEW", R.layout.widget1_layout);
            // updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
            mContext.sendBroadcast(updateIntent);
        }

        ids = man.getAppWidgetIds(
                new ComponentName(mContext, Widget2.class));
        for (int i = 0; i < ids.length; i++) {
            int appWidgetId = ids[i];
            Global.Log("Global Update Widget " + appWidgetId,2);
            Intent updateIntent = new Intent();
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            updateIntent.putExtra("VIEW", R.layout.widget2_layout);
            // updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
            mContext.sendBroadcast(updateIntent);
        }

        ids = man.getAppWidgetIds(
                new ComponentName(mContext, Widget3.class));
        for (int i = 0; i < ids.length; i++) {
            int appWidgetId = ids[i];
            Global.Log("Global Update Widget " + appWidgetId,2);
            Intent updateIntent = new Intent();
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            updateIntent.putExtra("VIEW", R.layout.widget3_layout);
            // updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
            mContext.sendBroadcast(updateIntent);
        }


        ids = man.getAppWidgetIds(
                new ComponentName(mContext, Widget4.class));
        for (int i = 0; i < ids.length; i++) {
            int appWidgetId = ids[i];
            Global.Log("Global Update Widget " + appWidgetId,2);
            Intent updateIntent = new Intent();
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            updateIntent.putExtra("VIEW", R.layout.widget4_layout);
            // updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
            mContext.sendBroadcast(updateIntent);
        }

        ids = man.getAppWidgetIds(
                new ComponentName(mContext, Widget5.class));
        for (int i = 0; i < ids.length; i++) {
            int appWidgetId = ids[i];
            Global.Log("Global Update Widget " + appWidgetId,2);
            Intent updateIntent = new Intent();
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            updateIntent.putExtra("VIEW", R.layout.widget5_layout);
            // updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data);
            mContext.sendBroadcast(updateIntent);
        }

    }

    private static void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "FOX_ERROR";
            String channelName = "Network Errors";
            channel1 = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            // omitted the LED color
            channel1.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel1);

            channelId = "FOX_ALERT";
            channelName = "Rate Alerts";
            channel2 = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            // omitted the LED color
            channel2.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel2);

        }
    }

    public static void cancelNotify(int id) {
        NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public static void cancelNotifyAlerts() {
        NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Global.FOXRateAlerts == null)
        {
            return;
        }

        for(int k=0; k < Global.FOXRateAlerts.size(); k++) {
            FOXRateAlertRecord rateAlert = Global.FOXRateAlerts.get(k);

            String message = rateAlert.code1 + " --> " +  rateAlert.code2 + " > " + rateAlert.value;
            int id = message.hashCode();
            notificationManager.cancel(id);
        }


    }

    public static void notifyError(String message) {
        Intent intent = new Intent(mContext, ActivityMain.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        // PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        intent.setAction(FOX_OPEN_ERROR);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
               intent , PendingIntent.FLAG_UPDATE_CURRENT);

        int id = "error".hashCode();

        if (Build.VERSION.SDK_INT >= 26) {
            // pick channel

            Notification n = new Notification.Builder(mContext, channel1.getId())
                    .setContentTitle(Global.getContext().getString(R.string.app_name))
                    // .setContentText(String.format(Global.getContext().getString(R.string.notfity_deny),appName))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.notification0)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon3))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build();
            notificationManager.notify(id, n); // we overwrite the current notitication


        } else {

            // build notification
            // the addAction re-use the same intent to keep the example short
            Notification n = new Notification.Builder(mContext)
                    .setContentTitle(Global.getContext().getString(R.string.app_name))
                    // .setContentText(String.format(Global.getContext().getString(R.string.notfity_deny),appName))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.notification0)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon3))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build();
            notificationManager.notify(id, n); // we overwrite the current notitication
        }

        Global.Log("Sent Error Notification",2);

    }

    public static void notifyMessage(String message, int icon) {
        Intent intent = new Intent(mContext, ActivityMain.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        // PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        intent.setAction(FOX_OPEN_ALERTS);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        int id = message.hashCode();

        Date timeNow = new Date();
        Calendar newDate = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

        newDate.setTime(timeNow);
        String humanDate = format.format(newDate.getTime());

        message = humanDate + " " + message;

        int bigIcon = R.drawable.up512;
        if (icon == R.drawable.notification2)
        {
            bigIcon = R.drawable.down512;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            Notification n = new Notification.Builder(mContext, channel2.getId())
                    .setContentTitle(Global.getContext().getString(R.string.app_name))
                    // .setContentText(String.format(Global.getContext().getString(R.string.notfity_deny),appName))
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), bigIcon))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build();
            notificationManager.notify(id, n); // we overwrite the current notitication
        } else {

            // build notification
            // the addAction re-use the same intent to keep the example short
            Notification n = new Notification.Builder(mContext)
                    .setContentTitle(Global.getContext().getString(R.string.app_name))
                    // .setContentText(String.format(Global.getContext().getString(R.string.notfity_deny),appName))
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), bigIcon))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build();
            notificationManager.notify(id, n); // we overwrite the current notitication
        }

        Global.Log("Sent Notification: " + message,3);

    }

    public static void fileAppend(String filename, String message) {

        // Global.Log("Writing to error file..." + filename,3);
        try {
            FileOutputStream fOut = mContext.openFileOutput(filename, MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(message + "\n");
            osw.flush();
            osw.close();
            // Log.w(TAG, "Wrote: " + message);
        } catch (Exception e) {
            Log.w(TAG, "Error Writing to log file: " + e);
        }

    }

    private static void checkRateAlerts()
    {

        // Global.FOXRateAlerts = Global.readRateAlertFile(Global.FILE_FOX_ALERTS);

        if (Global.FOXRateAlerts == null)
        {
            Global.Log("*** No alerts to check!",2);
            return;
        }

        Date timeNow = new Date();
        Calendar newDate = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

        newDate.setTime(timeNow);
        String humanDate = format.format(newDate.getTime());

        SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean del = (SP.getBoolean("delete_rate",false));

        Global.Log("Delete alerts = " + del,2);

        for(int k=0; k < Global.FOXRateAlerts.size(); k++) {
            FOXRateAlertRecord rateAlert = Global.FOXRateAlerts.get(k);

            Global.Log("Checking Rate Alert " + rateAlert.code1 + " v " + rateAlert.code2 + " for " + rateAlert.value, 2);
            if ((Global.FOXCurrencyAll.containsKey(rateAlert.code1)) && (Global.FOXCurrencyAll.containsKey(rateAlert.code2))) {
                float vNew = Global.FOXCurrencyAll.get(rateAlert.code2) / Global.FOXCurrencyAll.get(rateAlert.code1);
                float vOld = rateAlert.oldValue;
                Global.Log(rateAlert.code2 + " Old = " + vOld, 3);
                Global.Log(rateAlert.code2 + " New = " + vNew, 3);
                Global.Log(rateAlert.code2 + " Alert Threshold = " + rateAlert.value, 3);
                Global.FOXRateAlerts.get(k).oldValue = vNew; // need to store the converted rate
                if ((vOld < rateAlert.value) && (vNew > rateAlert.value)) {
                    Global.Log("Alert Rate Changed Up", 2);
                    String message = rateAlert.code1 + " --> " +  rateAlert.code2 + " > " + rateAlert.value;
                    Global.Log("Alert: " + message, 1);
                    Global.fileAppend(FILE_LOG_ALERTS, humanDate + " 0 " + "Alert: " + message);
                    notifyMessage(message,R.drawable.notification1);
                    if (del)
                    {
                        Global.FOXRateAlerts.remove(k);
                    }

                }
                if ((vOld > rateAlert.value) && (vNew < rateAlert.value)) {
                    Global.Log("Alert Rate Changed Down", 2);
                    String message = rateAlert.code1 + " --> " +  rateAlert.code2 + " < " + rateAlert.value;
                    Global.Log("Alert: " + message, 1);
                    Global.fileAppend(FILE_LOG_ALERTS, humanDate + " 0 " + "Alert: " + message);
                    notifyMessage(message,R.drawable.notification2);
                    if (del)
                    {
                        Global.FOXRateAlerts.remove(k);
                    }
                }
            } else{
                Global.Log("Currency not found skipping...",2);
            }
        }

        writeRateAlertFile(Global.FOXRateAlerts,Global.FILE_FOX_ALERTS);

    }



    public static class NextAlarmTime{

        long millisecs;
        String humanDate;
        long interval;

        public NextAlarmTime()
        {
            Date timeNow = new Date();
            Calendar newDate = new GregorianCalendar();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

            long t2 = timeNow.getTime();
            newDate.setTime(new Date(t2));

            SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(Global.getContext());
            boolean ecb = !(SP.getBoolean("use_open",true));

            interval = AlarmManager.INTERVAL_HALF_DAY;
            if (ecb == false) {
                interval = AlarmManager.INTERVAL_HOUR;

                newDate.add(Calendar.HOUR, 1);
                newDate.set(Calendar.MINUTE, 0);
                newDate.set(Calendar.SECOND, 0);
            } else {
                int hour = newDate.get(Calendar.HOUR);
                if (hour > 12)
                {
                    newDate.add(Calendar.DATE,1);
                    newDate.set(Calendar.HOUR, 0);
                } else {
                    newDate.set(Calendar.HOUR, 12);
                }

                newDate.set(Calendar.MINUTE, 0);
                newDate.set(Calendar.SECOND, 0);
            }

            humanDate = format.format(newDate.getTime());
            Global.Log("Next Update = " + humanDate,2);

            millisecs = newDate.getTimeInMillis();

            // return this;
        }

    }

    public static void setAlarm()
    {

        NextAlarmTime nextAlarmTime = new NextAlarmTime();

        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(Global.getContext(), FOXBroadcastReceiver.class);

        alarmIntent.putExtra("ALARM",Global.UPDATEALARM);

        AlarmManager alarmManager =
                (AlarmManager) Global.getContext().getSystemService(Context.ALARM_SERVICE);

        pendingIntent = PendingIntent.getBroadcast(Global.getContext(), 666, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (pendingIntent != null && alarmManager != null) {
            Global.Log("Cancelling Alarm",2);
            alarmManager.cancel(pendingIntent);
        }

        Global.Log("Setting Alarm Interval " + nextAlarmTime.interval,2);
        // Non-Wakeup does not seem to fire on the hour
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextAlarmTime.millisecs,
        // alarmManager.setInexactRepeating(AlarmManager.RTC, nextAlarmTime.millisecs,
                nextAlarmTime.interval, pendingIntent);
    }

    public static void checklastUpdate()
    {

        long t1 = Global.getUpdateTime();;

        Date timeNow = new Date();

        Calendar newDate = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");

        newDate.setTime(new Date(t1));
        String humanDate = format.format(newDate.getTime());

        Global.Log("Previous Update (t1) = " + humanDate + " " + t1,2);

        long t2 = timeNow.getTime();
        newDate.setTime(new Date(t2));
        humanDate = format.format(newDate.getTime());

        Global.Log("Time Now (t2) = " + humanDate +  " " + t2,2);

        SharedPreferences SP = androidx.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean ecb = !(SP.getBoolean("use_open",true));

        long interval = AlarmManager.INTERVAL_HALF_DAY;
        if (ecb == false)
        {
            interval = AlarmManager.INTERVAL_HOUR;
        }

        long x = (t1 + interval + (AlarmManager.INTERVAL_HOUR/2L)); // add half an hour to be safe
        // if ( (t1 + (Global.FOX_INTERVAL * 60 * 1000)) < t2 )
        if ( x < t2 )
        {
            Global.Log("Currency older than " + interval + " minutes...updating...",2);
            Global.updateCurrency();  // takes care of first start as well
        } else {
            if (Global.getErrorCount() > 0) // do we want to back off after a certain amount of errors?
            {
                Global.Log("Errors = " + Global.getErrorCount() + "...updating...",2);
                Global.updateCurrency();  // takes care of first start as well
            }
        }

    }

    public static int getFlag(String code)
    {
        int flag = R.drawable.flag_generic;

        String mDrawableName = "ic_flag_" + code.toLowerCase();
        Resources res = mContext.getResources();
        int resID = res.getIdentifier(mDrawableName , "drawable", mContext.getPackageName());

        if (resID != 0)
        {
            flag = resID;
        }

        return flag;
    }

    static final int CHUNK = (1024*50);

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[CHUNK];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    //
    // Display a popup info screen
    //
    public static void infoMessage(final Context context, String header, String message, int i)
    {
        final Dialog info = new Dialog(context);

        info.setContentView(R.layout.dialog_info);
        info.setTitle(header);

        TextView text = (TextView) info.findViewById(R.id.infoMessage);
        text.setText(message);
        text.setGravity(i);

        Button dialogButton = (Button) info.findViewById(R.id.infoButton);


        dialogButton.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {
                // notificationCancel(context);
                info.cancel();
            }
        });

        info.show();

    }
}