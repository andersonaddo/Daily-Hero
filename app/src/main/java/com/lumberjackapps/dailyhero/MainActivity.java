package com.lumberjackapps.dailyhero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener {
    ViewPager viewpager = null;
    ActionBar actionBar;
    ArrayList <Data> savedPages = new ArrayList<>();

    //This is fir the email "about" fragment
    public final static String EXTRA_MESSAGE = "com.lumberjackapps.dailyhero.MESSAGE";


    public boolean fileExistance(String fname) {
        File file = getFileStreamPath(fname);
        return file.exists();
    }


    public String loadCurrentWhat(String filename) {
        FileInputStream fileInputStream;
        String info = "";
        try {
            fileInputStream = openFileInput(filename);
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


    /**
     * Checks if the "Save profile" button has already been used for the current profile.
     * Returns false if the profile has been saved, and alter deleted
     */
    public boolean buttonStateUsed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("value_key", false);
    }

    /**
     * Makes the "Safe profile" button usable or unusable
     * @param newValue True if the button should be rendered unusable, false if it shouldn't
     * @see #buttonStateUsed
     */
    public void setButtonState(boolean newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("value_key", newValue);
        editor.commit();
    }

    public void link(View v) {
        String url = "";
        if (fileExistance("number")) {
            //Intent for using provided link through the browser
            url = loadCurrentWhat("link");

        } else {
            url = getResources().getString(R.string.firstlink);
        }
        Intent browser = new Intent(Intent.ACTION_VIEW);
        browser.setData(Uri.parse(url));
        startActivity(browser);
    }

    /**
     * Shares the current profile over a selected profile media
     * @param v Set XML "onClick" property
     */
    public void shareToday (View v){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Daily Hero Character # " + loadCurrentWhat("number") +":" + loadCurrentWhat("name") + "\n" + "Find out more at " + loadCurrentWhat("link"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share today's profile using: "));
        }
        else {
            Toast.makeText(this, "You don't have any apps to share this profile.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Adds the current profile to the persistent file that has the player's saved profiles
     * @param v Set XML "onClick" property
     */
    public void saveListofSaves (View v){
        if (!buttonStateUsed() && !loadCurrentWhat("abilities").equals("Until next time! \n-Lumberjack Apps")) {
            setButtonState(true);
            String title = loadCurrentWhat("name");
            String description = loadCurrentWhat("summary");
            String trivia = loadCurrentWhat("trivia");
            String abilities = loadCurrentWhat("abilities");
            String link = loadCurrentWhat("link");
            String number = loadCurrentWhat("number");
            int imageId = getResources().getIdentifier(loadCurrentWhat("picture"), "drawable", "com.lumberjackapps.dailyhero");
            Data savedDay = new Data(title, description, trivia, abilities, link, imageId, number);
            String filename2 = "savedPages";
            if (fileExistance(filename2)) {
                FileInputStream fileInputStream;
                ObjectInputStream objectInputStream;
                try {
                    fileInputStream = openFileInput(filename2);
                    objectInputStream = new ObjectInputStream(fileInputStream);
                    savedPages = (ArrayList<Data>) objectInputStream.readObject();
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
            savedPages.add(savedDay);
            FileOutputStream fileOutputStream;
            ObjectOutputStream objectOutputStream;
            try {
                fileOutputStream = openFileOutput(filename2, Context.MODE_PRIVATE);
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(savedPages);
                Toast.makeText(this, "Saved. You'll see it next time you open Daily Hero.", Toast.LENGTH_LONG).show();
                objectOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "This profile has already been saved", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //In this method, a viewpager for swiping navigation and a actionBar for tab navigation
        //Are created. They are then synchronized.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewpager.setAdapter(new MyAdapter(fragmentManager));
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Adding a navigation mode to the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);


        //Making and adding the tabs
        ActionBar.Tab tab1 = actionBar.newTab().setText("About").setTabListener(this);
        ActionBar.Tab tab2 = actionBar.newTab().setText("Character of the Day").setTabListener(this);
        ActionBar.Tab tab3 = actionBar.newTab().setText("Saved Profiles").setTabListener(this);
        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);



            viewpager.setCurrentItem(1);
            actionBar.setSelectedNavigationItem(1);


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}

class MyAdapter extends FragmentPagerAdapter {

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new AboutPage();
        }
        if (position == 1) {
            fragment = new CharacterInfo();
        }
        if (position == 2) {
            fragment = new SavedPages();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "About";
        }
        if (position == 1) {
            return "Character of the Day";
        }
        if (position == 2) {
            return "Saved Pages";
        }
        return null;
    }

}


