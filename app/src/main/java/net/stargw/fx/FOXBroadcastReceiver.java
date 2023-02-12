package net.stargw.fx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class FOXBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Global.Log("FOX Broadcast Receiver got something!",3);


        if ( (intent.getAction() != null))
        {
            Global.Log("FOX Broadcast Receiver Action = " + intent.getAction(),3);
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            {
                Global.FOXCurrencyAll = Global.readFOXFile(Global.FILE_FOX_CACHE);
                Global.setAlarm();
                return;
            }
        }

        Bundle extras = intent.getExtras();

        if (extras != null) {
            String name = extras.getString("ALARM");
            if (name != null) {
                Global.Log("Intent name = " + name, 3);

                if (name.equals(Global.UPDATEALARM)) {
                    Global.Log("Got Alarm: " + Global.UPDATEALARM, 2);
                    Global.updateCurrency();
                }
            }
        }

    }

}
