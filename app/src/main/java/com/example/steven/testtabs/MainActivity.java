package com.example.steven.testtabs;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    private TabLayout tabLayout;
    private TextView currentSongName;
    private RelativeLayout bottomBar;
    private Button playPauseButton;

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

        currentSongName = (TextView) findViewById(R.id.currentSongName);
        bottomBar = (RelativeLayout) findViewById(R.id.bottom_bar);

        //There is NOT(?) a permissions bug.
        // I might have fixed it - Colton
        getPermissions();

        AppCore.getInstance().pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        AppCore.getInstance().viewPager = (ViewPager) findViewById(R.id.container);
        playPauseButton = (Button) findViewById(R.id.play_pause);
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
        tabLayout.setupWithViewPager(AppCore.getInstance().viewPager);

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
        //Create default tabs for playlist

        //SimplePlaylist tabs - Single playlist tabs
        for(int i = 0; i < sizeOfDefaultPlaylists && i < AppCore.getInstance().mediaStorage.getSimplePlaylists().size(); i++)
            AppCore.getInstance().pagerAdapter.addFragment(new SongListFragment(), AppCore.getInstance().mediaStorage.getSimplePlaylist(i));

        //CompoundPlaylist tabs - Collection of playlist tabs (Expanded)
        for(int i = 0; i < AppCore.getInstance().mediaStorage.getCompoundPlaylists().size(); i++)
            AppCore.getInstance().pagerAdapter.addFragment(new ExpandablePlaylistFragment(), AppCore.getInstance().mediaStorage.getCompoundPlaylist(i));

        //Set adapter
        AppCore.getInstance().viewPager.setAdapter(AppCore.getInstance().pagerAdapter);
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
     * onResume() sets the name of the current song on the bottom bar.
     ************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();

        // Set the song title on the bottom bar
        if (AppCore.getInstance().musicSrv == null)
            return;

        if(AppCore.getInstance().musicSrv.getNowPlaying() != null) {
            bottomBar.setVisibility(View.VISIBLE);

            //Change state of the play_pause button according to state of musicSrv
            if(AppCore.getInstance().musicSrv.isPlaying())
                playPauseButton.setBackgroundResource(R.drawable.pause);
            else
                playPauseButton.setBackgroundResource(R.drawable.play);

            currentSongName.setText(AppCore.getInstance().musicSrv.getNowPlaying().getTitle());
        }
        else {
            bottomBar.setVisibility(View.GONE);
        }
    }

    public void createPlaylist(View v) {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.userinput, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        final EditText userInput = (EditText) view.findViewById(R.id.user_input);

        builder.setCancelable(true);
        builder.setTitle("Create New Playlist");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CharSequence sequence = userInput.getText();
                String name = sequence.toString();
                AppCore.getInstance().mediaStorage.createUserPlaylist(name);
                //I can't figure out a better way to refresh the list after adding a new playlist
                ExpandablePlaylistFragment current = (ExpandablePlaylistFragment) AppCore.getInstance().pagerAdapter.getItem(currentTab);
                current.updatePlaylists();
            }
        });
        builder.show();

        //TODO: Save to shared preferences

        //I can't figure out a better way to refresh the list after adding a new playlist
        ExpandablePlaylistFragment current = (ExpandablePlaylistFragment) AppCore.getInstance().pagerAdapter.getItem(currentTab);
        current.updatePlaylists();
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
        }
        else {
            getMusic();
        }
    }

    public void onPlayPausePress(View view) {
        if(AppCore.getInstance().musicSrv == null) {
            Log.w(TAG, "playPause(): musicSrv == null!");
            return;
        }

        AppCore.getInstance().musicSrv.playPause();
        if(AppCore.getInstance().musicSrv.isPlaying())
            view.setBackgroundResource(R.drawable.pause);
        else
            view.setBackgroundResource(R.drawable.play);
    }

    // Tap the bottom bar
    public void openNowPlaying(View view) {
        Intent nowPlaying = new Intent(this, NowPlaying.class);
        startActivity(nowPlaying);
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
        Log.d("getMusic()", "Getting music");
        ContentResolver musicResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //I believe that this is the function that puts the files into a string....
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor songCursor = musicResolver.query(songUri, null, selection, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            Log.d("getMusic()", "Found music on device");
            int titleColumn  = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn  = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn     = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            //Adding ALBUM_ID apparently it is a lot more stable for pulling Album Art
            //Also I may be using this later to search through songs because it is faster apparently
            //because it is a hash
            int albumID      = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artColumn    = songCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            int i = 0;
            do {
                String thisTitle = songCursor.getString(titleColumn);
                String thisArtist = songCursor.getString(artistColumn);
                String thisAlbum = songCursor.getString(albumColumn);
                //This is where we will add Album Art to the song Class.
                //We may need to reduce resolution in order to improve performance
                //if we want to include the images in the main menu
                //for now the NowPlaying activity will retrieve and render these images based on
                //their path for performance
                String coverPath;
                //Must check to see if the path to album art is empty
                Log.d("getMusic()", "artColumn index is: " + artColumn);
                if (artColumn < 0) {
                    coverPath = "NA";
                }
                else {
                    coverPath = songCursor.getString(artColumn);
                }

                long thisAlbumId = songCursor.getLong(albumID);
                long thisId = songCursor.getLong(idColumn);

                AppCore.getInstance().mediaStorage.createSong
                        (thisId, thisTitle, thisArtist, thisAlbum, coverPath, thisAlbumId);
                i++;
            }
            while(songCursor.moveToNext());
            songCursor.close();

            Log.d("getMusic()", "Finished grabbing " + i + " songs");
            //Default sorting
            Collections.sort(AppCore.getInstance().mediaStorage.getSongs(), new Comparator<Song>(){
                public int compare(Song a, Song b){
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
        }
        else
            Log.w("getMusic()", "Found no songs on this device. Bug or is this okay?");

        createDefaultPlaylists();
    }

    /********************************************************************************************
     * createDefaultPlaylists() creates the default playlists to be added into the Media Player
     * App. The default playlists are made into their own tabs.
     *
     * Add default playlists here with given sort type.
     *******************************************************************************************/

    private void createDefaultPlaylists() {
        Log.d("createDefaultPlaylists", "Creating default playlists");
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

        int i;
        for(i = 0; i < artistSort.size(); i++) {
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
        if(i > 0)
            artistCollection.add(currentArtistPlaylist);


        //CompoundPlaylist of albums
        CompoundPlaylist albumCollection = AppCore.getInstance().mediaStorage.createCompoundPlaylist
                ("Albums");
        SimplePlaylist currentAlbumPlaylist = null;
        String previousAlbum = null;

        for(i = 0; i < albumSort.size(); i++) {
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
        //Just in case the "last playlist" isn't at index 0 (was never initialized)
        //Add last album playlist
        if(i > 0)
            albumCollection.add(currentAlbumPlaylist);

        //Logging for each playlist to check if empty. Could be bad?
        for(int index = 0; index < AppCore.getInstance().mediaStorage.getSimplePlaylists().size(); index++) {
            SimplePlaylist simplePlaylist = AppCore.getInstance().mediaStorage.getSimplePlaylist(index);
            if(simplePlaylist.isEmpty())
                Log.w("createDefaultPlaylists", "Playlist : " + simplePlaylist.getNameOfPlaylist() + ". Bug or is this okay?");
        }
    }

    //Create tab for selected playlist
    public boolean createTabForPlaylist(SimplePlaylist playlist) {
        if(playlist.isEmpty()) {
            Toast.makeText(mContext, "Cannot create new tab for empty playlist. For now..." , Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Attempted to create tab with empty playlist");
            return false;
        }
        for(int i = 0; i < tabLayout.getChildCount(); i++) {
            if(AppCore.getInstance().pagerAdapter.getPageTitle(i).equals(playlist.getNameOfPlaylist())) {
                Toast.makeText(mContext, "Tab already exists with that name", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Attempted to create tab with already existing name");
                return false;
            }
        }
        //Create new tab with given playlist
        AppCore.getInstance().pagerAdapter.addFragment(new SongListFragment(), playlist);
        return true;
    }
}