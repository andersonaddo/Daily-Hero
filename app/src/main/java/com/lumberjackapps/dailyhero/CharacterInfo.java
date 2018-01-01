package com.lumberjackapps.dailyhero;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * This is the fragment that is behind the main profile page.
 */
public class CharacterInfo extends Fragment {

     TextView heroName;
     TextView heroSummary;
     TextView heroTrivia;
     TextView heroAbilities;
     TextView heroNum;
     ImageView heroImage;
     private PendingIntent pendingIntent;






    public boolean fileExistance(String fname){
        File file = getActivity().getFileStreamPath(fname);
        return file.exists();
    }




    public String loadCurrentWhat (String filename){
        FileInputStream fileInputStream;
        String info = "";
        try {
            fileInputStream = getActivity().openFileInput(filename);
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




    public CharacterInfo() {
        // Required empty public constructor
    }




    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * This method allows XML images to receive compressed images instead of full memory images.
     * Prevents sluggish scrolling.
     * @param res
     * @param resId resource id of the image
     * @param reqWidth width of image
     * @param reqHeight height of image
     * @return
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_character_info, container, false);
        heroNum = (TextView)view.findViewById(R.id.character_number);

        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);

        heroSummary = (TextView) view.findViewById(R.id.character_summary);
        heroTrivia = (TextView) view.findViewById(R.id.character_trivia);
        heroAbilities = (TextView) view.findViewById(R.id.character_abilities);
        heroName = (TextView) view.findViewById(R.id.character_title);

        //If app used for more than a day, then reload the current stuff from the files
        if (fileExistance("number")){
            if (!loadCurrentWhat("number").equals("1")) {

                heroSummary.setText(loadCurrentWhat("summary"));
                heroTrivia.setText(loadCurrentWhat("trivia"));
                heroAbilities.setText(loadCurrentWhat("abilities"));
                heroName.setText(loadCurrentWhat("name"));
                heroNum.setText(loadCurrentWhat("number"));

                //Including the picture!!
                heroImage = (ImageView) view.findViewById(R.id.character_image);
                final String imageName = loadCurrentWhat("picture");
                int resID = getResources().getIdentifier(imageName, "drawable", "com.lumberjackapps.dailyhero");
                heroImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(), resID, 300, 300));
            }
       }else{
            saveCurrentWhat("number", "1");
            startAlarm();
        }

        if (loadCurrentWhat("number").equals("1")){
            //Loading default details
            heroSummary.setText(getResources().getString(R.string.firstsummary));
            heroTrivia.setText(getResources().getString(R.string.firsttrivia));
            heroAbilities.setText(getResources().getString(R.string.firstabilities));
            heroName.setText(getResources().getString(R.string.firstname));

            //Including the picture!!
            heroImage = (ImageView) view.findViewById(R.id.character_image);
            final String imageName = getResources().getString(R.string.firstpicture);
            int resID = getResources().getIdentifier(imageName, "drawable", "com.lumberjackapps.dailyhero");
            heroImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(), resID, 300, 300));

            saveCurrentWhat("name", heroName.getText().toString());
            saveCurrentWhat("picture", imageName);
            saveCurrentWhat("trivia", heroTrivia.getText().toString());
            saveCurrentWhat("abilities", heroAbilities.getText().toString());
            saveCurrentWhat("link", getResources().getString(R.string.firstlink));
            saveCurrentWhat("summary", heroSummary.getText().toString());
        }

        return view;

    }


    private void saveCurrentWhat(String filename, String string) {
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = getActivity().getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //For the AlarmReceiver to update the character profile every day
    public void startAlarm(){
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, pendingIntent);

    }


    }


