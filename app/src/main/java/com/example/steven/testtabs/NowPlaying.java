package com.example.steven.testtabs;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileDescriptor;

public class NowPlaying extends AppCompatActivity {
    private static final String TAG = "NowPlaying";

    public TextView textView;
    //Got rid of title and added the Song object instead.
    //(Can change back later, just testing for now)
    public Song nowPlaying;
    private SeekBar songProgressBar;
    Button playPauseButton;
    //Need to have a handler I'm not sure what this does but it interfaces with android thread handler
    //I think
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        playPauseButton = (Button) findViewById(R.id.playPauseButton);

        updateTrackInfo();
    }

    @Override
    protected void onPostResume() {
        Log.d(TAG, "Resuming");
        super.onPostResume();
        if(AppCore.getInstance().musicSrv.isPlaying())
            playPauseButton.setBackgroundResource(R.drawable.pause);
        else
            playPauseButton.setBackgroundResource(R.drawable.play);
    }

    //On press play/pause
    public void playPause(View view) {
        Log.d(TAG,"Attempting to play/pause music with play/pause button");

        //Playing music
        if(AppCore.getInstance().musicSrv.playPause()) {
            Log.d(TAG, "Changing play/pause button to pause");
            view.setBackgroundResource(R.drawable.pause);
        }
        //Pausing music
        else {
            view.setBackgroundResource(R.drawable.play);
            Log.d(TAG, "Changing play/pause button to play");
        }
    }

    //On press prev song button
    public void prevSong(View view) {
        AppCore.getInstance().musicSrv.prevSong();
        updateTrackInfo();
    }

    //On press next song button
    public void nextSong(View view) {
        AppCore.getInstance().musicSrv.nextSong();
        updateTrackInfo();
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
            textView = (TextView) findViewById(R.id.title);
            textView.setText(nowPlaying.getTitle());
            textView = (TextView) findViewById(R.id.album);
            textView.setText(nowPlaying.getAlbum());
            textView = (TextView) findViewById(R.id.artist);
            textView.setText(nowPlaying.getArtist());

            //Log.d("updateTrackInfo()", "The album art path is: " + nowPlaying.getAlbumArt());
            ImageView coverAlbum=(ImageView) findViewById(R.id.albumArt);

            coverAlbum.setImageBitmap(getAlbumart(nowPlaying.getAlbumID())); //was img

            //This should update the progressBar every second
            updateProgressBar();
        }
        else
            textView.setText("End of queue");
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

            TextView seekInfo = (TextView) findViewById(R.id.TotalTime);
            // Displaying Total Duration time
            seekInfo.setText(""+convertMinutes(totalDuration));
            // Displaying time completed playing
            seekInfo = (TextView) findViewById(R.id.CurrentTime);
            seekInfo.setText(""+convertMinutes(currentDuration));

            // Updating progress bar
            int progress = (int)(findPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar = (SeekBar) findViewById(R.id.seekBar);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public String convertMinutes(long milliseconds) {
        String output;
        long m = milliseconds / (60*1000);
        long s = (milliseconds / 1000) % 60;
        output = m + ":" + s;

        return output;
    }

    public int findPercentage(long current, long total) {
        int percentage = (int)(current * 100.0 / total + 0.5);
        return percentage;
    }

}
