package net.stargw.fox;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.graphics.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by swatts on 11/02/18.
 */

public class WidgetProvider extends AppWidgetProvider {

    // Widget has:
    //  1) Layout/View - there are 5 of these
    //  2) An ID - which represents the instance
    // For each instance it will try to update all 5 views

    Context myContext;
    int myView;

    public int getView() {
        Global.Log("Widget Get Layout Generic",3);
        return R.layout.widget1_layout;
    }

    public int getWidgetLayout() {
        return 1;
    }

    // I handle the updates manually rather than letting this do them!!
    // Not clear if onUpdate would be called by the widget update xml value???
/*
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        myContext = context;
        // context = Global.getContext();

        Log.w("FOXWidget1",  "onUpdate");

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            try {

                Intent intent = new Intent(context, ActivityMain.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                RemoteViews views = new RemoteViews(context.getPackageName(), getView());

                views.setOnClickPendingIntent(R.id.widgit_click, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }

            updateGUI(context,appWidgetManager,appWidgetId);

            Log.w("FOXWidget1", "Loop W = " + String.valueOf(i));

        }


    }
*/

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        myView = getView();

        Global.Log("Widget Layout " + getWidgetLayout() + " (" + myView + ") onReceive - ID = " + appWidgetId,3);

        if ((intent.getAction() != null)) {
            Global.Log("Widget Layout " + getWidgetLayout() + " Action = " + intent.getAction(),3);
        }

        if ((intent.getAction() != null) && (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction()))) {
            Bundle extras = intent.getExtras();
            if(extras!=null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                // ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
                // int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                // onUpdate(context, appWidgetManager, appWidgetIds);
                int v = extras.getInt("VIEW");
                Global.Log("Widget Layout " + getWidgetLayout() + " (" + myView + ") - does it match Layout " + v + " ?",3);
                if (v == myView) {
                    Global.Log("Widget Layout " + getWidgetLayout() + " and ID " + appWidgetId + " MATCHED - updating",3);
                    updateGUI(context, appWidgetManager, appWidgetId);
                }
            }
        }

        if ((intent.getAction() != null) && (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(intent.getAction()))) {
            Bundle extras = intent.getExtras();
            if(extras!=null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                // ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
                // int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

                int v = extras.getInt("VIEW");
                Global.Log("Widget Layout " + getWidgetLayout() + " (" + myView + ") - does it match Layout " + v + " ?",3);
                if (v == myView) {
                    Global.Log("Widget Layout " + getWidgetLayout() + " and ID " + appWidgetId + " MATCHED - deleting",3);
                    // onUpdate(context, appWidgetManager, appWidgetIds);
                    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(myContext);

                    p.edit().remove("C1-" + appWidgetId).commit();
                    p.edit().remove("C2-" + appWidgetId).commit();
                    p.edit().remove("O1-" + appWidgetId).commit();
                }


            }
        }


    }

    public void updateGUI(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        myContext = context;

        Global.Log("Widget Layout " + getWidgetLayout() + " and ID = " + appWidgetId + " update the display",3);

        // Gets info on itself somehow!
        RemoteViews views = new RemoteViews(context.getPackageName(), getView());

        // views.setLightBackgroundLayoutId();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(myContext);
        int opacity = p.getInt("O1-" + appWidgetId, 0);
        // Global.Log("Set Opacity = " + opacity,3);
        views.setInt(R.id.widgit_click, "setBackgroundColor", ColorUtils.setAlphaComponent(Color.BLACK, (int) (255*opacity/100)));

        // widgit_click
        // myButton.getBackground().setAlpha(128);


        String c1 = p.getString("C1-" + appWidgetId, "GBP");
        String c2 = p.getString("C2-" + appWidgetId, "USD");

        Global.Log("Widget Layout " + getWidgetLayout() + " and ID = "  + appWidgetId + " using " + c1 + " and " + c2,3);

        if (Global.FOXCurrencyAll != null) {
            if (Global.FOXCurrencyAll.containsKey(c1) && Global.FOXCurrencyAll.containsKey(c2)) {
                // float f1 = Global.FOXCurrencyAll.get("THB") / Global.FOXCurrencyAll.get(Global.getBaseCurrency());
                float f1 = Global.FOXCurrencyAll.get(c2) / Global.FOXCurrencyAll.get(c1);  // * Global.getBaseCurrencyAmount();
                views.setTextViewText(R.id.widgit_update_value, String.format(java.util.Locale.US, "%.3f", f1));
            } else {
                views.setTextViewText(R.id.widgit_update_value, String.format(java.util.Locale.US, "%.3f", 0f));
            }

            views.setImageViewResource(R.id.widgit_flag1,Global.getFlag(c1));
            views.setImageViewResource(R.id.widgit_flag2,Global.getFlag(c2));

            views.setTextViewText(R.id.widgit_code1,c1);
            views.setTextViewText(R.id.widgit_code2,c2);

            // views.setViewVisibility(R.id.widgit_update_date, View.INVISIBLE);
            // views.setViewVisibility(R.id.widgit_icon2, View.INVISIBLE);
        }

        long t1 = Global.getUpdateTime();;

        Calendar newDate = new GregorianCalendar();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");
        if (getView() == R.layout.widget2_layout) {
            format = new SimpleDateFormat("HH:mm\ndd/MM/yy");
        }

        newDate.setTime(new Date(t1));
        String humanDate = format.format(newDate.getTime());

        views.setTextViewText(R.id.widgit_update_date, humanDate);

        Intent i = new Intent(context, ActivityMain.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        views.setOnClickPendingIntent(R.id.widgit_click, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        Global.Log("Widget Layout " + getWidgetLayout() + " and ID = " + appWidgetId + " set Date = " + humanDate,3);
    }



}
