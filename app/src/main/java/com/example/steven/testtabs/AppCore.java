package com.example.steven.testtabs;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.MenuItem;

import java.util.ArrayList;

/********************************************************************************************
 * Created by okoboji on 11/11/17.
 * I'm so sorry guys but I have to do this. I see no other way of sharing our music Service
 * between activities. I am setting up a Singleton which will hold all the Core components of
 * both our service and quite possibly our created lists as well.
 *
 * This class will contain the Instances of Music Service and the like.
 *********************************************************************************************/

public class AppCore {


    /********************************************************************************************
     * HERE YE HERE YE!!!
     * This class will instantiate itself whenever you want to use or access anything in this class
     * you have to do it like this.
     * AppCore.getInstance().WhateverfunctionYouNeed()
     *
     * WARNING!!!!: Do not pass the Context of any activity to this Class!! Doing so will break
     * the java garbage collection system! DON'T DO IT!!
     ********************************************************************************************/
    private final static AppCore singleInstance = new AppCore();

    //List of Variables
    public ArrayList<Song> songList = new ArrayList<>();
    public ArrayList<Playlist> allLists = new ArrayList<>();
    public MusicService musicSrv;
    public Intent playIntent;
    public ServiceConnection musicConnection;
    public String currentSongName;
    public boolean musicBound = false;


    /********************************************************************************************
     * return the Instance of our Class.
     * @return
     ********************************************************************************************/
    public static AppCore getInstance(){
        return singleInstance;
    }

    /*******************************************************************************************
     * startService() is called in the onCreate() function.
     * Starts up the Music playing service built into android at the start of the program.
     * Not sure if we need to pass the service between activities....
     *******************************************************************************************/

    public void startService() {
        musicConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                //pass list
                musicSrv.setList(songList);
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
    }

    public String getSongName() {
        return musicSrv.getSong().getTitle();
    }
}
