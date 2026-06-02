package net.stargw.fx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
                Global.setUpdateCurrencySchedule();
            }
        }

        // WorkManager handles the periodic updates now.
        // We only need this for BOOT_COMPLETED to ensure it is scheduled,
        // although WorkManager is persistent.
    }

}
