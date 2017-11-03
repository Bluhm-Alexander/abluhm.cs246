package com.example.steven.testtabs;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_REQUEST = 1;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ArrayList<Song> songList = new ArrayList<>();
    private ArrayList<ArrayList<Song>> allLists = new ArrayList<>();
    int currentTab = 0;

    MediaPlayer mediaPlayer;
    MusicService musicSrv;
    Intent playIntent;
    boolean musicBound = false;
    private ServiceConnection musicConnection = new ServiceConnection() {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        //Permissions stuff to access the external SDCard
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            getMusic();
        }

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mediaPlayer);
        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager();
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tabLayout.getSelectedTabPosition();
                musicSrv.setList(allLists.get(currentTab));

                //For debugging
                Toast.makeText(getApplicationContext(),"Switched to tab: " + currentTab,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager() {
        pagerAdapter.addFragment(new SongListFragment(), "Title sort",  allLists.get(0));
        pagerAdapter.addFragment(new SongListFragment(), "Artist sort", allLists.get(1));
        pagerAdapter.addFragment(new SongListFragment(), "Album sort",  allLists.get(2));
        pagerAdapter.addFragment(new SongListFragment(), "Playlist sort", new ArrayList<Song>());
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    //On item click on listView
    public void songPicked(View view) {
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Gets music from External Content
    public void getMusic() {
        ContentResolver musicResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor songCursor = musicResolver.query(songUri, null, selection, null, null);

        if(songCursor != null && songCursor.moveToFirst()) {
            int titleColumn  = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn  = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn     = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {
                String thisTitle  = songCursor.getString(titleColumn);
                String thisArtist = songCursor.getString(artistColumn);
                String thisAlbum  = songCursor.getString(albumColumn);
                long thisId       = songCursor.getLong(idColumn);

                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum));
            }
            while (songCursor.moveToNext());
            songCursor.close();

            //Default sorting
            Collections.sort(songList, new Comparator<Song>(){
                public int compare(Song a, Song b){
                    return a.getTitle().compareTo(b.getTitle());
                }
            });

            allLists.add(new ArrayList<Song>(songList));
            allLists.add(new ArrayList<Song>(songList));
            allLists.add(new ArrayList<Song>(songList));

            Collections.sort(allLists.get(1), new Comparator<Song>(){
                public int compare(Song a, Song b){
                    return a.getArtist().compareTo(b.getArtist());
                }
            });

            Collections.sort(allLists.get(2), new Comparator<Song>(){
                public int compare(Song a, Song b){
                    return a.getAlbum().compareTo(b.getAlbum());
                }
            });
        }
    }

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
}
