package com.lumberjackapps.dailyhero;

/**
 * Created by addoa on 05-Jul-16.
 * This is the class that receives the broadcast when then
 * @see com.lumberjackapps.dailyhero.MainActivity for some of the undocumented methods
 */

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {


    Context context2;

    final int noOfCharacters = 205;
    ArrayList<Integer> usedNumbers = new ArrayList<>();
    final String filename = "numbersUsedFile";

    public String loadCurrentWhat(String filename) {
        FileInputStream fileInputStream;
        String info = "";
        try {
            fileInputStream = context2.getApplicationContext().openFileInput(filename);
            int count;
            while ((count = fileInputStream.read()) != -1) {
                info += Character.toString((char) count);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }


    private void saveCurrentWhat(String filename, String string) {
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = context2.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @see MainActivity
     */
    private void setButtonState(boolean newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context2.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("value_key", newValue);
        editor.commit();
    }


    //Necessary method for formatting the data from the database
    private String unescape(String description) {
        description = description.replaceAll("\\\\n", "\\\n").replaceAll("—","-");
        description = description.replaceAll("'", "\'").replaceAll("’","\'");
        description = description.replaceAll("”","\"").replaceAll("“","\"");
        return description;
    }


    /**This gets the next database ID for the next character profile.
     *
     * @return Returns a new ID, or 0 if all the IDs have been used up.
     */
    private int getNumber() {
        Random rand = new Random();
        int identifier = 0;
        //noOfCharacters is the maximum and the 1 is our minimum
        try {
            getUsedNumbers();
            //Making sure the number hasn't been used before
            while (usedNumbers.size() < noOfCharacters) {
                identifier = rand.nextInt(noOfCharacters) + 1;
                boolean isUsed = false;
                if (usedNumbers.size() > 0) {
                    for (int check = 0; check < usedNumbers.size(); check++) {
                        int comparer = usedNumbers.get(check);
                        if (comparer == identifier) {
                            isUsed = true;
                        }
                    }
                }
                if (isUsed == false) {
                    usedNumbers.add(identifier);
                    saveUsedNumbers();
                    break;
                }
            }
            return identifier;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return identifier;
    }


    private void saveUsedNumbers() {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        try {
            fileOutputStream = context2.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(usedNumbers);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This method gets the used up database IDs from internal storage
    private void getUsedNumbers() {
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        try {
            fileInputStream = context2.getApplicationContext().openFileInput(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            usedNumbers = (ArrayList<Integer>) objectInputStream.readObject();
            objectInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends a notification to notify the user when a new character profile is ready.
     */
    public void sendNotification(int Id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context2.getApplicationContext())
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Learn about " + loadCurrentWhat("name"))
                        .setContentText("Daily Hero Character #" + loadCurrentWhat("number"));

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        if (Build.VERSION.SDK_INT >= 21) {
            int notificationColor = 0xFF2196F5;
            mBuilder.setColor(notificationColor);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context2.getApplicationContext(), MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context2.getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context2.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(Id, mBuilder.build());
    }

    /**
     * Updates the app to a new character profile
     */
    public void UPDATE() {
        int identifier = getNumber();

        if (identifier != 0) {
            //Accessing the database
            MyDatabase characterDatabase = new MyDatabase(context2.getApplicationContext());
            final ArrayList<String> detailsForToday = characterDatabase.getDailyData(identifier);

            //We have the information. Editing value of hero number and all
            String strHeroNumber = "";
            int HeroNumber = Integer.parseInt(loadCurrentWhat("number"));
            HeroNumber += 1;
            strHeroNumber = HeroNumber + "";

            setButtonState(false);

            final String finalStrHeroNumber = strHeroNumber;

            saveCurrentWhat("name", unescape(detailsForToday.get(0)));
            saveCurrentWhat("picture", detailsForToday.get(5));
            saveCurrentWhat("trivia", unescape(detailsForToday.get(2)));
            saveCurrentWhat("abilities", unescape(detailsForToday.get(3)));
            saveCurrentWhat("number", finalStrHeroNumber);
            saveCurrentWhat("link", detailsForToday.get(4));
            saveCurrentWhat("summary", unescape(detailsForToday.get(1)));
            sendNotification(1);


        } else {
            setButtonState(true);


            //Saving everything...
            saveCurrentWhat("name", "At the End of the Tunnel");
            saveCurrentWhat("picture", "thatsallfolks");
            saveCurrentWhat("trivia", "That's OK though. You can continue learning on your own, or leave a review (or donation *wink*) for me to add more profiles.");
            saveCurrentWhat("abilities", "Until next time, \n-Lumberjack Apps");
            saveCurrentWhat("link", "http://lumberjacksapps.wixsite.com/home");
            saveCurrentWhat("summary", "It seems your journey is over and you've run out of characters to learn about (at least for now).");
        }


    }


    @Override
    public void onReceive(Context context, Intent intent) {

        // For our recurring task, we update the character data in internal storage.
        context2 = context.getApplicationContext();
        UPDATE();

    }
}
