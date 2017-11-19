package com.example.steven.testtabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        textView = (TextView) findViewById(R.id.title);

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

    public void updateTrackInfo() {
        nowPlaying = AppCore.getInstance().musicSrv.getNowPlaying();
        textView.setText(nowPlaying.getTitle());
    }
}
