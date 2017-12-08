package com.example.steven.testtabs;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.Log;

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
    //Look up if this stuff is safe to be here
    //List of Variables
    private static final String TAG = "AppCore";

    public MediaStorage mediaStorage = new MediaStorage();
    public int addingToPlaylistIndex = -1;
    public MusicService musicSrv;
    public Intent playIntent;
    public ServiceConnection musicConnection;
    public PagerAdapter pagerAdapter;
    public ViewPager viewPager;
    public int currentPlaylistIndexInExpandableListView; //Ignore, but this helps in updating tabs
    public boolean removingPlaylists = false;
    public boolean musicBound = false;

    /*********************************************************************************************
     * Private constructor;
     *********************************************************************************************/
    private AppCore() {
    }

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


    //tell java that this is synchronized
    public void startService() {
        musicConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                if(musicSrv == null)
                    Log.e(TAG, "Music service is null!");
                //pass list
                musicSrv.setMediaStorage(mediaStorage);
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
    }
}
