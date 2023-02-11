package net.stargw.fox;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget2 extends WidgetProvider {

    @Override
    public int getView() {
        Global.Log("Widget Layout 2 (" + R.layout.widget2_layout + ")",3);
        return R.layout.widget2_layout;
    }

    @Override
    public int getWidgetLayout() {
        return 2;
    }
}
