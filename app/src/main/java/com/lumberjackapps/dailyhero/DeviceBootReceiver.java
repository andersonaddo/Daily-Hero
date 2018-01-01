package com.lumberjackapps.dailyhero;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by addoa on 05-Jul-16.
 * Receives a broadcast whenever the device reboots. Sets a character profile change to happen in 12 hours,
 * since device rebooting would cancel the AlarmReceiver daily broadcasting cycle.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    Context context2;

    public boolean fileExistance(String fname){
        File file = context2.getApplicationContext().getFileStreamPath(fname);
        return file.exists();
    }




    public String loadCurrentWhat (String filename){
        FileInputStream fileInputStream;
        String info = "";
        try {
            fileInputStream = context2.getApplicationContext().openFileInput(filename);
            int count;
            while ((count=fileInputStream.read()) != -1){
                info += Character.toString((char)count);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context2 = context;
            if (fileExistance("number")){
                     /* Setting the alarm here */
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_DAY, pendingIntent);
            }

    }
}