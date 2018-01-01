package com.lumberjackapps.dailyhero;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by addoa on 24-Jun-16.
 */
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "Characters.db";
    private static final int DATABASE_VERSION = 1;


    //Defining some of the table's attributes
    public final static String TABLE_CHARACTERSINFO = "CharactersInfo";
    public final static String COLUMN_PICTURE = "Picture";
    public final static String COLUMN_NAME = "Name";
    public final static String COLUMN_SUMMARY = "Summary";
    public final static String COLUMN_TRIVIA = "Trivia";
    public final static String COLUMN_ABILITIES = "Abilities";
    public final static String COLUMN_LINK = "Link";
    public final static String COLUMN_ID = "_id";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(); //Forces the database to update if the version number has been incremented
    }

    public ArrayList getDailyData (int idNum){
        //Opening the Database
        SQLiteDatabase db = getReadableDatabase();

        //Making the Array and fields
        ArrayList<String> details = new ArrayList<String>();
        String name = "";
        String summary = "";
        String trivia = "";
        String abilities = "";
        String picture = "";
        byte[] link = null;

        String link2;

        Cursor cursor = db.query(TABLE_CHARACTERSINFO, new String[]{COLUMN_ID,
                        COLUMN_NAME, COLUMN_PICTURE, COLUMN_SUMMARY, COLUMN_TRIVIA, COLUMN_ABILITIES, COLUMN_LINK}, COLUMN_ID + "=?",
                new String[]{String.valueOf(idNum)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        name = cursor.getString(1);
        picture = cursor.getString(2);
        summary = cursor.getString(3);
        trivia = cursor.getString(4);
        abilities = cursor.getString(5);
        link = cursor.getBlob(6);

        link2 = new String(link);

        details.add(name);
        details.add(summary);
        details.add(trivia);
        details.add(abilities);
        details.add(link2);
        details.add(picture);
        db.close();
        cursor.close();
        return details;
    }


}