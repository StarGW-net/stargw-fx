package net.stargw.fx;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateCurrencyWorker extends Worker {

    public UpdateCurrencyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Global.Log("WorkManager: Periodic currency update", 2);
        
        boolean success = Global.updateCurrencySync();
        
        if (success) {
            Global.setUpdateCurrencySchedule();
            return Result.success();
        } else {
            return Result.retry();
        }
    }
}
