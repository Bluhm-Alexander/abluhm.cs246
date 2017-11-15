package com.example.steven.testtabs;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

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
                currentTab = tabLayout.getSelectedTabPosition();

                //For debugging will remove this later
                Toast.makeText(getApplicationContext(),"Switched to tab: " + currentTab,Toast.LENGTH_SHORT).show();
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
        if (AppCore.getInstance().allLists.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Media!", Toast.LENGTH_SHORT).show();
            //Maybe add something here later.... Ask if it is ok to just make default values

        }

        else {
            //Create default tabs for playlist
            pagerAdapter.addFragment(new SongListFragment(), AppCore.getInstance().allLists.get(0));
            pagerAdapter.addFragment(new SongListFragment(), AppCore.getInstance().allLists.get(1));
            pagerAdapter.addFragment(new SongListFragment(), AppCore.getInstance().allLists.get(2));

            //Add settings tab AFTER default tabs
            pagerAdapter.setupSettingsTab();

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
                } else {
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

    /**********************************************************************************************
     * songPicked(view)
     * When music is selected in the Tab it passes which song has been slected to the music service
     * Alex is modifying this function so that Music Service is passed to the NowPlaying Activity.
     * @param view
     *********************************************************************************************/
    public void songPicked(View view) {
        //Sets playlist that song was called from
        AppCore.getInstance().musicSrv.setPlaylist(currentTab);
        //Sets selected song to be played
        AppCore.getInstance().musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        //Play the song
        AppCore.getInstance().musicSrv.playSong();
        //Making new intent to switch to NowPlaying Activity
        Intent nowPlaying = new Intent(mContext, NowPlaying.class);

        mContext.startActivity(nowPlaying);

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
     * It inserts the track lists into the variable allLists. all Lists tabs correspond to an
     * integer. Artist tab is allLists(1).
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
            int titleColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {
                String thisTitle = songCursor.getString(titleColumn);
                String thisArtist = songCursor.getString(artistColumn);
                String thisAlbum = songCursor.getString(albumColumn);
                long thisId = songCursor.getLong(idColumn);

                AppCore.getInstance().songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum));
            }
            while (songCursor.moveToNext());
            songCursor.close();

            //Default sorting
            Collections.sort(AppCore.getInstance().songList, new Comparator<Song>(){
                public int compare(Song a, Song b){
                    return a.getTitle().compareTo(b.getTitle());
                }
            });

            createDefaultPlaylists();
        }
    }

    //Create default playlists
    private void createDefaultPlaylists() {
        //Create new list of songs so order of original list of songs don't change when we sort it
        ArrayList<Song> songs = new ArrayList<>();
        songs.addAll(AppCore.getInstance().songList);

        //Create playlist for title sort
        Playlist titleSort  = new Playlist("Title Sort");
        //Default list already sorted by title. Skip the sort for this
        //Store into playlist
        titleSort.addAll(songs);


        //Create playlist for artist sort
        Playlist artistSort = new Playlist("Artist Sort");
        //Sort by artist
        Collections.sort(songs, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getArtist().compareTo(b.getArtist());
            }
        });
        //Store into playlist
        artistSort.addAll(songs);


        //Create playlist for album sort
        Playlist albumSort = new Playlist("Album Sort");
        //Sort by album
        Collections.sort(songs, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getAlbum().compareTo(b.getAlbum());
            }
        });
        //Store into playlist
        albumSort.addAll(songs);


        AppCore.getInstance().allLists.add(titleSort);
        AppCore.getInstance().allLists.add(artistSort);
        AppCore.getInstance().allLists.add(albumSort);
    }

    //Create playlist from user
    public boolean createUserPlaylist(String nameOfPlaylist) {
        for(int i = 0; i < AppCore.getInstance().allLists.size(); i++) {
            if(nameOfPlaylist == AppCore.getInstance().allLists.get(i).playlistName) {
                Toast.makeText(mContext, "Playlist already exists with that name", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Attempted to create playlist with already existing name");
                return false;
            }
        }
        Playlist newPlaylist = new Playlist(nameOfPlaylist);
        AppCore.getInstance().allLists.add(newPlaylist);
        return true;
    }

    //Create tab for selected playlist
    public boolean createTabForPlaylist(Playlist playlist) {
        if(playlist.isEmpty()) {
            Toast.makeText(mContext, "Cannot create new tab for empty playlist. For now..." , Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Attempted to create tab with empty playlist");
            return false;
        }
        for(int i = 0; i < tabLayout.getChildCount(); i++) {
            if(pagerAdapter.getPageTitle(i).equals(playlist.getPlaylistName())) {
                Toast.makeText(mContext, "Tab already exists with that name", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Attempted to create tab with already existing name");
                return false;
            }
        }
        //Create new tab with given playlist
        pagerAdapter.addFragment(new SongListFragment(), playlist);
        return true;
    }

    //Add to playlist
    public boolean addToPlaylist(Playlist playlist, Song song) {
        return AppCore.getInstance().musicSrv.addToPlaylist(playlist, song);
    }

    //Remove from playlist by index - returns removed song
    public Song removeFromPlaylist(Playlist playlist, int index) {
        return AppCore.getInstance().musicSrv.removeFromPlaylist(playlist, index);
    }

    //Removes from playlist by object - returns if successful
    public boolean removeFromPlaylist(Playlist playlist, Song song) {
        return removeFromPlaylist(playlist, song);
    }
}
