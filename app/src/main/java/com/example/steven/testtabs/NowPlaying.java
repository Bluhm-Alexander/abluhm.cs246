package com.example.steven.testtabs;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;

public class NowPlaying extends AppCompatActivity {
    private static final String TAG = "NowPlaying";

    public TextView textView;
    //Got rid of title and added the Song object instead.
    //(Can change back later, just testing for now)
    public Song nowPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        Button playPauseButton = (Button) findViewById(R.id.playPauseButton);
        playPauseButton.setBackgroundResource(R.drawable.pause);

        updateTrackInfo();
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

            //Drawable img = Drawable.createFromPath(nowPlaying.getAlbumArt());
            //Log.d("updateTrackInfo()", "The album art path is: " + nowPlaying.getAlbumArt());
            ImageView coverAlbum=(ImageView) findViewById(R.id.albumArt);

            coverAlbum.setImageBitmap(getAlbumart(nowPlaying.getAlbumID())); //was img
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
}
