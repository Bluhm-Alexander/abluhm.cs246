package com.example.steven.testtabs;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainActivity extends AppCompatActivity {

    //!!----Variable section----!!
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_REQUEST = 1;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    int currentTab = 0;
    int sizeOfDefaultPlaylists = 1; //For adding only the default tabs to our PagerAdapter
    //context of MainActivity
    private Context mContext;

    //!!----Functions Section----!!
    /*******************************************************************************************
     * onCreate() instantiates the services we need in order for android to play music files
     * and Load a List of media from the device.
     * @param savedInstanceState
     ******************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");



        getPermissions();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager();
        setupTabLayout();

        //Going to AppCore to Start our music Service
        AppCore.getInstance().startService();


        //set up context
        mContext = getApplicationContext();
    }

    /*******************************************************************************************
     *  setupTabLayout() is called in the onCreate function.
     *  Create the Tab Layout is set up through a series of listeners waiting for the tab to
     *  be clicked.
     ******************************************************************************************/

    private void setupTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int previousTab = currentTab;
                currentTab = tabLayout.getSelectedTabPosition();
                Log.d(TAG, "Switched to tab: " + currentTab + " from: " + previousTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Set tab padding
        for(int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(16,16,16,16);
            tab.requestLayout();
        }

    }

    /**********************************************************************************************
     * Sends lists of songs from the media stores to the Tabs. This function is creashing the program
     * Alex is going to add an if statement that checks to see if lists are empty. If they are it
     * will insert a no media into the list.
     *********************************************************************************************/

    private void setupViewPager() {
        //checking for empty Lists
        if (AppCore.getInstance().mediaStorage.getSongs().isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Media!", Toast.LENGTH_SHORT).show();
            //Maybe add something here later.... Ask if it is ok to just make default values

        }

        else {
            //Create default tabs for playlist

            //SimplePlaylist tabs - Single playlist tabs
            for(int i = 0; i < sizeOfDefaultPlaylists; i++)
                pagerAdapter.addFragment(new SongListFragment(), AppCore.getInstance().mediaStorage.getSimplePlaylists().get(i));

            //CompoundPlaylist tabs - Collection of playlist tabs (Expanded)
            for(int i = 0; i < AppCore.getInstance().mediaStorage.getCompoundPlaylists().size(); i++)
                pagerAdapter.addFragment(new ExpandablePlaylistFragment(), AppCore.getInstance().mediaStorage.getCompoundPlaylists().get(i));

            //Set adapter
            viewPager.setAdapter(pagerAdapter);
        }
    }

    /*******************************************************************************************
     * onStart() is called after onCreate(). when User navigates away from the application
     * onStop() is called. When user navigates back to the application onStart is called.
     * onStart() makes sure that the music playing service is passed the right info
     * I think.
     ******************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        if(AppCore.getInstance().playIntent == null){
            AppCore.getInstance().playIntent = new Intent(this, MusicService.class);
            bindService(AppCore.getInstance().playIntent, AppCore.getInstance().musicConnection, Context.BIND_AUTO_CREATE);
            startService(AppCore.getInstance().playIntent);
        }
    }

    /*************************************************************************************
     * onDestroy() Stops music when application is closed.
     * seems to be a glitch when the application is exited. Investigate further.
     ************************************************************************************/

    @Override
    protected void onDestroy() {
        stopService(AppCore.getInstance().playIntent);
        AppCore.getInstance().musicSrv = null;
        super.onDestroy();
    }

    /********************************************************************************************
     * onOptionsItemSelected(item) I think this function checks to see if shuffle is on or not.
     * Could someone please clarify?
     *
     * I think this function is useless here. Possibly for testing purposes I will keep it here
     * until I know better.
     *
     * @param item
     * @return
     *******************************************************************************************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                break;
            case R.id.action_end:
                stopService(AppCore.getInstance().playIntent);
                AppCore.getInstance().musicSrv = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************
     * I think this function checks to see if permissions have been granted to the application
     * if not it displays a message that no permissions have been granted.
     * @param requestCode
     * @param permissions
     * @param grantResults
     *********************************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                        getMusic();
                    }
                }
                else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    /*************************************************************************************
     * User Permissions Function getPermission()
     * prompts user if needs be for permission to access SD Card. No Params
     * We seem to have an odd bug with the permissions function. Program crashes when
     * it is being opened for the first time.
     ************************************************************************************/

    private void getPermissions() {
        //Permissions stuff to access the external SDCard
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSION_REQUEST);
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSION_REQUEST);
            }
        } else {
            getMusic();
        }
    }

    //On press play/pause
    public void playPause(View view) {
        AppCore.getInstance().musicSrv.playPause();
    }

    //On press prev song button
    public void prevSong(View view) {
        AppCore.getInstance().musicSrv.prevSong();
    }

    //On press next song button
    public void nextSong(View view) {
        AppCore.getInstance().musicSrv.nextSong();
    }

    /********************************************************************************************
     * getMusic() retrieves a list of music files from the android device and puts them into a String
     * Default playlists are then created by a different sort type of this list and pushed into
     * the MusicService class.
     *
     * This Function I think needs to be modified so we can move necessary components to appCore
     * AppCore.getInstance().
     *******************************************************************************************/

    public void getMusic() {
        ContentResolver musicResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //I believe that this is the function that puts the files into a string....
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor songCursor = musicResolver.query(songUri, null, selection, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int titleColumn  = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn  = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn     = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);

            for(int i = 0; songCursor.moveToNext(); i++) {
                String thisTitle = songCursor.getString(titleColumn);
                String thisArtist = songCursor.getString(artistColumn);
                String thisAlbum = songCursor.getString(albumColumn);
                long thisId = songCursor.getLong(idColumn);

                Song newSong = AppCore.getInstance().mediaStorage.createSong
                        (thisId, thisTitle, thisArtist, thisAlbum);
            }
            songCursor.close();

            //Default sorting
            Collections.sort(AppCore.getInstance().mediaStorage.getSongs(), new Comparator<Song>(){
                public int compare(Song a, Song b){
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
            createDefaultPlaylists();
        }
    }

    /********************************************************************************************
     * createDefaultPlaylists() creates the default playlists to be added into the Media Player
     * App. The default playlists are made into their own tabs.
     *
     * Add default playlists here with given sort type.
     *******************************************************************************************/

    private void createDefaultPlaylists() {
        //Create new list of songs so order of original list of songs don't change when we sort it
        ArrayList<Song> songs = new ArrayList<>();
        songs.addAll(AppCore.getInstance().mediaStorage.getSongs());

        //Default single playlists

        //TITLE SORT
        SimplePlaylist titleSort = AppCore.getInstance().mediaStorage.createSimplePlaylist
                ("All songs");
        //Sort by title
        Collections.sort(songs, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }});
        titleSort.addAll(songs);

        //ARTIST SORT
        SimplePlaylist artistSort = AppCore.getInstance().mediaStorage.createSimplePlaylist
                ("Artist Sort");
        //Sort by artist
        Collections.sort(songs, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getArtist().compareTo(b.getArtist());
            }});
        artistSort.addAll(songs);


        //ALBUM SORT
        SimplePlaylist albumSort = AppCore.getInstance().mediaStorage.createSimplePlaylist
                ("Album Sort");
        //Sort by album
        Collections.sort(songs, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getAlbum().compareTo(b.getAlbum());
            }});
        albumSort.addAll(songs);

        //End of default (single) playlists


        //Default collection of playlists (For expandable list views)

        //CompoundPlaylist of artists
        CompoundPlaylist artistCollection = AppCore.getInstance().mediaStorage.createCompoundPlaylist
                ("Artists");
        SimplePlaylist currentArtistPlaylist = null;
        String previousArtist = null;

        for(int i = 0; i < artistSort.size(); i++) {
            Song currentSong = artistSort.get(i);
            if(!currentSong.getArtist().equals(previousArtist)) {
                if(i > 0) {
                    artistCollection.add(currentArtistPlaylist);
                }
                currentArtistPlaylist = AppCore.getInstance().mediaStorage.createSimplePlaylist
                        (currentSong.getArtist());
            }
            currentArtistPlaylist.add(currentSong);
            previousArtist = currentSong.getArtist();
        }
        //Add last artist playlist
        artistCollection.add(currentArtistPlaylist);


        //CompoundPlaylist of albums
        CompoundPlaylist albumCollection = AppCore.getInstance().mediaStorage.createCompoundPlaylist
                ("Albums");
        SimplePlaylist currentAlbumPlaylist = null;
        String previousAlbum = null;

        for(int i = 0; i < albumSort.size(); i++) {
            Song currentSong = albumSort.get(i);
            if(!currentSong.getAlbum().equals(previousAlbum)) {
                if(i > 0) {
                    albumCollection.add(currentAlbumPlaylist);
                }
                currentAlbumPlaylist = AppCore.getInstance().mediaStorage.createSimplePlaylist(currentSong.getAlbum());
            }
            currentAlbumPlaylist.add(currentSong);
            previousAlbum = currentSong.getAlbum();
        }
        //Add last album playlist
        albumCollection.add(currentAlbumPlaylist);
    }

    //Create tab for selected playlist
    public boolean createTabForPlaylist(SimplePlaylist playlist) {
        if(playlist.isEmpty()) {
            Toast.makeText(mContext, "Cannot create new tab for empty playlist. For now..." , Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Attempted to create tab with empty playlist");
            return false;
        }
        for(int i = 0; i < tabLayout.getChildCount(); i++) {
            if(pagerAdapter.getPageTitle(i).equals(playlist.getNameOfPlaylist())) {
                Toast.makeText(mContext, "Tab already exists with that name", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Attempted to create tab with already existing name");
                return false;
            }
        }
        //Create new tab with given playlist
        pagerAdapter.addFragment(new SongListFragment(), playlist);
        return true;
    }
}