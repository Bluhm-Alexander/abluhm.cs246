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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final String TAG = "MusicService";

    private MediaPlayer player;
    private final IBinder musicBind = new MusicBinder();

    private ArrayList<Song> musicLibrary;
    private ArrayList<SimplePlaylist> allPlaylists;

    private int currentPlaylist;
    private int currentSong;

    private boolean loopOn = false;
    private boolean shuffleOn = false;

    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();

        //Get the same values as we left them
        shuffleOn = getSharedPreferences("mediaPlayer", 0).getBoolean("shuffle", false);
        loopOn    = getSharedPreferences("mediaPlayer", 0).getBoolean("loop",    false);

        //For each playlist after the defaults, save to preferences
        //Same for playlists, but how?

        initMusicPlayer();
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

    public boolean onSongPicked(int playlistIndex, int songIndex) {
        setPlaylist(playlistIndex);
        setSong(songIndex);

        Log.d(TAG, "Song at: " + songIndex + " = " + allPlaylists.get(playlistIndex).get(songIndex).getTitle());
        Log.d(TAG, "Next song: " + getCurrentSong().getTitle());
        Intent nowPlaying = new Intent(this, NowPlaying.class);

        this.startActivity(nowPlaying);

        return playSong();
    }

    //Pass list of songs to MusicService
    public void setSongList(ArrayList<Song> songList){
        musicLibrary = songList;
        setSong(0);
    }

    //Pass list of playlists to MusicService
    public void setPlaylistList(ArrayList<SimplePlaylist> playlistList) {
        allPlaylists = playlistList;
        setPlaylist(0);
    }

    //Sets the playlist to play from
    public void setPlaylist(int index) {
        Log.d(TAG, "Setting current playlist to " + allPlaylists.get(index).getNameOfPlaylist() + " at index: " + index);
        currentPlaylist = index;
        currentSong = 0; //By default
    }

    //Sets the next song
    public void setSong(int index) {
        Log.d(TAG, "Setting current song to " + getCurrentPlaylist().get(index).getTitle());
        currentSong = index;
    }

    public SimplePlaylist getCurrentPlaylist() {
        return allPlaylists.get(currentPlaylist);
    }

    public Song getCurrentSong() {
        if(currentSong < 0)
            return null;
        else
            return getCurrentPlaylist().get(currentSong);
    }

    //Alias of getCurrentSong()
    public Song getNowPlaying() {
        return getCurrentSong();
    }

    //Access this from MainActivity
    class MusicBinder extends Binder {
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
        /*currentSong++;

        playSong();*/
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
    public int prevSong() {
        //Repeat song if past 3 seconds
        if(player.getCurrentPosition() > 3000) {
            Log.d(TAG, "Restarting song before 3 seconds while executing prevSong()");
            restartSong();
            return currentSong;
        }

        //Otherwise, go back a song
        //If at the end of the queue, check if loop is on
        else if(currentSong == 0) {
            if(loopOn) {
                Log.d(TAG, "Setting currentSong index to " + (getCurrentPlaylist().size() - 1) + ", from: " + currentSong);
                currentSong = getCurrentPlaylist().size() - 1;
            }
            else {
                Log.d(TAG, "Beginning of queue while executing prevSong(). Restarting song.");
                restartSong();
                return currentSong;
            }
        }
        else
            currentSong--;

        playSong();
        Log.d(TAG, "Skipped to previous song while executing prevSong()");
        return currentSong;
    }

    //On press nextSong button
    public int nextSong() {
        //If at the end of the queue, check if loop is on
        if(currentSong == getCurrentPlaylist().size() - 1) {
            if(loopOn) {
                currentSong = 0;
            }
            else {
                Log.d(TAG, "End of queue while executing nextSong()");
                player.reset();
                currentSong = -1;
                return currentSong;
            }
        }
        else
            currentSong++;

        playSong();
        Log.d(TAG, "Skipped to next song while executing nextSong()");
        return currentSong;
    }

    //On press playPause button
    public boolean playPause() {
        if(player.isPlaying()) {
            Log.d(TAG, "Pausing music in playPause()");
            player.pause();
            return false;
        }
        else {
            Log.d(TAG, "Playing music in playPause()");
            player.start();
            return true;
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
    public boolean playSong() {
        Log.d(TAG, "Attempting to play song in playSong()");
        player.reset();

        if(currentSong < 0) {
            Log.d(TAG, "Current song index = " + currentSong);
            return false;
        }

        //Get id of current song
        long songID = getCurrentSong().getID();
        Log.d(TAG, "Next song = " + getCurrentSong().getTitle() + "\n" +
                "from playlist: " + getCurrentPlaylist().getNameOfPlaylist() + " at index: " + currentSong);

        //Get uri
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync(); //From the MediaPlayer class
        return true;
    }

    //Create playlist from user
    public boolean createUserPlaylist(String nameOfPlaylist) {
        for(int i = 0; i < allPlaylists.size(); i++) {
            if(nameOfPlaylist.equals(allPlaylists.get(i).getNameOfPlaylist())) {
                Toast.makeText(this, "Playlist already exists with that name", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Attempted to create playlist with already existing name");
                return false;
            }
        }
        SimplePlaylist newPlaylist = new SimplePlaylist(nameOfPlaylist);
        AppCore.getInstance().allPlaylists.add(newPlaylist);
        return true;
    }

    //Add to playlist
    public boolean addToPlaylist(SimplePlaylist playlist, Song song) {
        return playlist.add(song);
    }

    //Removes from playlist by object - returns true if successful
    public boolean removeFromPlaylist(SimplePlaylist playlist, Song song) {
        return playlist.remove(song);
    }

    //Remove from playlist by index - returns removed song object
    public Song removeFromPlaylist(SimplePlaylist playlist, int index) {
        return playlist.remove(index);
    }

    //Returns isPlaying from mediaPlayer
    public boolean isPlaying() {
        return player.isPlaying();
    }
}
