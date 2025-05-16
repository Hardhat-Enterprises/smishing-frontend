package com.example.smishingdetectionapp.notifications.clipboard;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
//import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
            boolean enabled = prefs.getBoolean("service_enabled", false);
            //Log.d("BootReceiver", "Boot received. Service enabled: " + enabled);
            if (enabled) {
                Intent serviceIntent = new Intent(context, ClipboardService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}