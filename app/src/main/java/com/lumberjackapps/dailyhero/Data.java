package com.lumberjackapps.dailyhero;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by addoa on 03-Jul-16.
 * This is just a data holding class for a character profile. Useful for data persistence and intents
 */
public class Data implements Serializable{
    public String name;
    public String summary;
    public int imageId;
    public String trivia;
    public String link;
    public String abilities;
    public String number;

    Data(String title, String description, String trivia, String abilities, String link, int imageId, String number) {
        this.name = title;
        this.summary = description;
        this.imageId = imageId;
        this.trivia = trivia;
        this.abilities = abilities;
        this.link = link;
        this.number = number;
    }


}