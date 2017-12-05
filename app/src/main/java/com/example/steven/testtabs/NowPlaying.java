package com.example.steven.testtabs;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.annotation.AnimatorRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileDescriptor;

import static android.view.View.GONE;

public class NowPlaying extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "NowPlaying";

    public TextView title;
    public TextView artist;
    public TextView album;
    public TextView currentTime;
    public TextView songLength;
    public Button playPauseButton;
    public Button previous;
    public Button next;
    public Button shuffle;
    public Button loop;
    public ImageView coverAlbum;

    private AlphaAnimation buttonClick;

    //Got rid of title and added the Song object instead.
    //(Can change back later, just testing for now)
    public Song nowPlaying;
    private SeekBar songProgressBar;

    //Need to have a handler I'm not sure what this does but it interfaces with android thread handler
    //I think
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        //Initializing all here so we don't have to every time we press a button
          //multiple times without closing the nowPlaying fragment
        title  = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);
        album  = (TextView) findViewById(R.id.album);

        coverAlbum = (ImageView) findViewById(R.id.albumArt);

        currentTime = (TextView) findViewById(R.id.currentTime);
        songLength = (TextView) findViewById(R.id.totalTime);

        playPauseButton = (Button) findViewById(R.id.playPauseButton);
        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);

        shuffle = (Button) findViewById(R.id.shuffle);
        loop = (Button) findViewById(R.id.loop);

        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);

        buttonClick = new AlphaAnimation(1F, 0.8F);

        //Have to set the overloaded functions
        songProgressBar.setOnSeekBarChangeListener(this);

        updateTrackInfo();
    }

    @Override
    protected void onPostResume() {
        Log.d(TAG, "Resuming");
        super.onPostResume();
        updateButtons();
        updateTrackInfo();
    }

    public void updateButtons() {
        if(AppCore.getInstance().musicSrv.isPlaying()) {
            Log.d(TAG, "Setting the \"play/pause\" button state to playing (paused icon)");
            playPauseButton.setBackgroundResource(R.drawable.pause);
        }
        else {
            Log.d(TAG, "Setting the \"play/pause\" button state to paused (play icon)");
            playPauseButton.setBackgroundResource(R.drawable.play);
        }
        if(AppCore.getInstance().musicSrv.getShuffleOn()) {
            Log.d(TAG, "Setting the \"shuffle\" button state to ON");
            shuffle.setBackgroundResource(R.drawable.shuffle_pressed);
        }
        else {
            Log.d(TAG, "Setting the \"shuffle\" button state to OFF");
            shuffle.setBackgroundResource(R.drawable.shuffle_unpressed);
        }
        if(AppCore.getInstance().musicSrv.getLoopOn()) {
            loop.setBackgroundResource(R.drawable.loop_pressed);
        }
        else {
            loop.setBackgroundResource(R.drawable.loop_unpressed);
        }
    }

    //On press play/pause
    public void playPause(View view) {
        Log.d(TAG,"Attempting to play/pause music with play/pause button");
        AppCore.getInstance().musicSrv.playPause();
        view.startAnimation(buttonClick);
        updateButtons();
    }

    //On press prev song button
    public void prevSong(View view) {
        AppCore.getInstance().musicSrv.prevSong();
        view.startAnimation(buttonClick);
        updateTrackInfo();
        updateButtons();
    }

    //On press next song button
    public void nextSong(View view) {
        AppCore.getInstance().musicSrv.nextSong();
        view.startAnimation(buttonClick);
        updateTrackInfo();
        updateButtons();
    }

    public void shuffle(View view) {
        AppCore.getInstance().musicSrv.toggleShuffle();
        //view.startAnimation(buttonClick);
        updateButtons();
    }

    public void loop(View view) {
        AppCore.getInstance().musicSrv.toggleLoop();
        //view.startAnimation(buttonClick);
        updateButtons();
    }

    /**********************************************************************************************
     * updateTrackInfo() grabs the current song out of AppCore and applies it locally to the
     * type song nowPlaying. It is very good to just call this function after doing anything to
     * current song position.
     *
     **********************************************************************************************/

    public void updateTrackInfo() {
        nowPlaying = AppCore.getInstance().musicSrv.getNowPlaying();
        if(nowPlaying != null) {
            title.setText(nowPlaying.getTitle());
            album.setText(nowPlaying.getAlbum());
            artist.setText(nowPlaying.getArtist());

            //Log.d("updateTrackInfo()", "The album art path is: " + nowPlaying.getAlbumArt());
            coverAlbum.setImageBitmap(getAlbumart(nowPlaying.getAlbumID())); //was img

            //This should update the progressBar every second
            updateProgressBar();
        }
        else {
            title.setText("Stopped");
            album.setText("");
            artist.setText("");
            updateButtons();
        }
    }

    /*********************************************************************************************
     * This function grabs the album Art out of the MediaStore database which apparently scrubs
     * Android for album Art so we don't have to.
     * @param album_id
     * @return bitmap
     **********************************************************************************************/
    public Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    //I'm going to put the seekbar check on a seperate thread and see what happens.

    /**
     * Update timer on seekbar
     * */

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {
            long totalDuration = AppCore.getInstance().musicSrv.getPlayer().getDuration();
            long currentDuration = AppCore.getInstance().musicSrv.getPlayer().getCurrentPosition();

            // Displaying Total Duration time
            songLength.setText("" + convertMinutes(totalDuration));
            // Displaying time completed playing
            currentTime.setText("" + convertMinutes(currentDuration));

            // Updating progress bar
            int progress = (int)(findPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);


        }
    };

    /**********************************************************************************************
     * This function doesn't do anything its just here to satisfy the interface definitions
     * @param seekBar
     * @param progress
     * @param fromTouch
     *********************************************************************************************/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /*********************************************************************************
     *  This function is a listener for the seekBar whenever the user touches the seek
     *  Bar it stops calling the update time function it basically suspends activity so it doesn't
     *  screw with the updater.
     * @param seekBar
     ********************************************************************************/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    /**************************************************************************************
     * When user lets go of the seek bar it will update the song with the current position.
     * @param seekBar
     **************************************************************************************/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = AppCore.getInstance().musicSrv.getPlayer().getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        Log.d("onStopTrackingTouch", "The function has been called");
        // forward or backward to certain seconds
        AppCore.getInstance().musicSrv.setSeek(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

   public int progressToTimer(int progress, int totalTime) {
        int position = (int)(totalTime * (progress / 100.00));


        return position;
   }


    public String convertMinutes(long milliseconds) {
        String output;
        long m = milliseconds / (60*1000);
        long s = (milliseconds / 1000) % 60;
        //For displaying 0:00 instead of 0:0
        if(s < 10)
            output = m + ":0" + s;
        else
            output = m + ":" + s;
        return output;
    }

    public int findPercentage(long current, long total) {
        int percentage = (int)(current * 100.0 / total + 0.5);
        return percentage;
    }
}