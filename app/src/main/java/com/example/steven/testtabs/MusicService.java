package com.example.steven.testtabs;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();

    private boolean loopOn = false;
    private boolean shuffleOn = false;

    public void onCreate(){
        super.onCreate();
        songPosn = 0;
        player = new MediaPlayer();

        initMusicPlayer();
    }

    public Song getNowPlaying() {
        return songs.get(songPosn);
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
    public boolean onUnbind(Intent intent){
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

    //On press prevSong button
    public void prevSong() {
        //Repeat song if past 2.5 seconds
        if(player.getCurrentPosition() > 2500) {
            Toast.makeText(this, "Restarting song", Toast.LENGTH_SHORT).show();
            player.seekTo(0);
            return;
        }

        //Otherwise, go back a song
        //If at the end of the queue, check if loop is on
        else if(songPosn == 0) {
            if(loopOn) {
                songPosn = songs.size() - 1;
            }
            else {
                Toast.makeText(this, "End of queue (prev)", Toast.LENGTH_SHORT).show();
                player.reset();
                return;
            }
        }
        else
            songPosn--;

        playSong();
        Toast.makeText(this, "Skipped to previous song", Toast.LENGTH_SHORT).show();
    }

    //On press nextSong button
    public void nextSong() {
        //If at the end of the queue, check if loop is on
        if(songPosn == songs.size() - 1) {
            if(loopOn) {
                songPosn = 0;
            }
            else {
                Toast.makeText(this, "End of queue (next)", Toast.LENGTH_SHORT).show();
                player.reset();
                return;
            }
        }
        else
            songPosn++;

        playSong();
        Toast.makeText(this, "Skipped to next song", Toast.LENGTH_SHORT).show();
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
    }

    public void toggleLoop() {
        loopOn = !loopOn;
        if(loopOn)
            Toast.makeText(this, "Loop: On", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Loop: Off", Toast.LENGTH_SHORT).show();
    }

    //Play song
    public void playSong(){
        player.reset();

        //Get song
        Song playSong = songs.get(songPosn);

        //Get id
        long currSong = playSong.getID();

        //Get uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync(); //From the MediaPlayer class
    }

    //Possibly unused right now
    public void setSong(int songIndex){ songPosn = songIndex; }

    //Sets song by obj. Finds pressed song in other sorted list and finds the index of it in THIS list
    public void setSong(Song thisSong) {
        songPosn = songs.indexOf(thisSong);
    }
}
