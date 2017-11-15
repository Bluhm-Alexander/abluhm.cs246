package com.example.steven.testtabs;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = "MusicService";
    private MediaPlayer player;
    private ArrayList<Song> songs;
    private ArrayList<Playlist> allPlaylists;
    private Playlist currentPlaylist;

    private final IBinder musicBind = new MusicBinder();
    private Song currentSong;
    private boolean loopOn = false;
    private boolean shuffleOn = false;

    private int indexInPlaylist;
    private int indexInLibrary;

    public Song getSong(){
        return songs.get(indexInLibrary);
    }

    public void onCreate(){
        super.onCreate();
        indexInLibrary = 0;
        player = new MediaPlayer();
        allPlaylists = AppCore.getInstance().allLists;
        setPlaylist(0);

        //Get the same values as we left them
        shuffleOn = getSharedPreferences("mediaPlayer", 0).getBoolean("shuffle", false);
        loopOn    = getSharedPreferences("mediaPlayer", 0).getBoolean("loop",    false);
        //Same for playlists

        initMusicPlayer();
    }

    public Song getNowPlaying() {
        Log.d(TAG, "Playing " + currentSong.getTitle() + ". IndexInLibrary = " + indexInLibrary + ". IndexInPlaylist = " + indexInPlaylist);
        return currentSong;
    }

    private void setIndexInLibrary() {
        currentSong    = currentPlaylist.get(indexInPlaylist);
        indexInLibrary = songs.indexOf(currentSong);
    }

    //Initialize
    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //Pass list of songs to MusicService
    public void setList(ArrayList<Song> theSongs){
        songs = theSongs;
    }

    public void setPlaylist(int index) {
        currentPlaylist = allPlaylists.get(index);
    }

    //Access this from MainActivity
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextSong();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    //Restarts song
    private void restartSong() {
        player.seekTo(0);
    }

    //On press prevSong button
    public Song prevSong() {
        //Repeat song if past 2.5 seconds
        if(player.getCurrentPosition() > 3000) {
            Toast.makeText(this, "Restarting song", Toast.LENGTH_SHORT).show();
            restartSong();
            return getNowPlaying();
        }

        //Otherwise, go back a song
        //If at the end of the queue, check if loop is on
        else if(indexInPlaylist == 0) {
            if(loopOn) {
                indexInPlaylist = songs.size() - 1;
            }
            else {
                Toast.makeText(this, "Beginning of queue (prev)", Toast.LENGTH_SHORT).show();
                restartSong();
                return getNowPlaying();
            }
        }
        else
            indexInPlaylist--;

        playSong();
        Toast.makeText(this, "Skipped to previous song", Toast.LENGTH_SHORT).show();
        return currentPlaylist.get(indexInPlaylist);
    }

    //On press nextSong button
    public Song nextSong() {
        //If at the end of the queue, check if loop is on
        if(indexInPlaylist == currentPlaylist.size() - 1) {
            if(loopOn) {
                indexInPlaylist = 0;
            }
            else {
                Toast.makeText(this, "End of queue (next)", Toast.LENGTH_SHORT).show();
                player.reset();
                currentSong = new Song();
                return null;
            }
        }
        else
            indexInPlaylist++;

        playSong();
        Toast.makeText(this, "Skipped to next song", Toast.LENGTH_SHORT).show();
        return currentPlaylist.get(indexInPlaylist);
    }

    //On press playPause button
    public void playPause() {
        if(player.isPlaying()) {
            player.pause();
            Toast.makeText(this, "Pausing music", Toast.LENGTH_SHORT).show();
        }
        else {
            player.start();
            Toast.makeText(this, "Playing music", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleShuffle() {
        shuffleOn = !shuffleOn;
        if(shuffleOn)
            Toast.makeText(this, "Shuffle: On", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Shuffle: Off", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = getSharedPreferences("mediaPlayer", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("shuffle", shuffleOn);
        editor.apply();

        Log.d(TAG, "Saving shuffle boolean to shared preferences. (" + shuffleOn + ")");
    }

    public void toggleLoop() {
        loopOn = !loopOn;
        if(loopOn)
            Toast.makeText(this, "Loop: On", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Loop: Off", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = getSharedPreferences("mediaPlayer", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loop", loopOn);
        editor.apply();

        Log.d(TAG, "Saving loop boolean to shared preferences. (" + loopOn + ")");
    }

    public boolean getShuffleOn() {
        return shuffleOn;
    }

    public boolean getLoopOn() {
        return loopOn;
    }

    //Play song
    public void playSong() {
        player.reset();

        //Get song
        setIndexInLibrary();
        currentSong = songs.get(indexInLibrary);

        //Get id
        long currSong = currentSong.getID();

        //Get uri
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync(); //From the MediaPlayer class
    }

    //Sets song by index
    public void setSong(int songIndex) {
        indexInPlaylist = songIndex;
        setIndexInLibrary();
    }

    //Create playlist from user
    public boolean createUserPlaylist(String nameOfPlaylist) {
        for(int i = 0; i < allPlaylists.size(); i++) {
            if(nameOfPlaylist.equals(allPlaylists.get(i).playlistName)) {
                Toast.makeText(this, "Playlist already exists with that name", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Attempted to create playlist with already existing name");
                return false;
            }
        }
        Playlist newPlaylist = new Playlist(nameOfPlaylist);
        AppCore.getInstance().allLists.add(newPlaylist);
        return true;
    }

    //Add to playlist
    public boolean addToPlaylist(Playlist playlist, Song song) {
        return playlist.add(song);
    }

    //Removes from playlist by object - returns true if successful
    public boolean removeFromPlaylist(Playlist playlist, Song song) {
        return playlist.remove(song);
    }

    //Remove from playlist by index - returns removed song object
    public Song removeFromPlaylist(Playlist playlist, int index) {
        return playlist.remove(index);
    }
}
