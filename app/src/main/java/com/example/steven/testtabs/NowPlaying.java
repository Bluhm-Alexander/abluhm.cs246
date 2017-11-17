package com.example.steven.testtabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NowPlaying extends AppCompatActivity {

    public TextView textView;
    //Got rid of title and added the Song object instead.
    //(Can change back later, just testing for now)
    public Song nowPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        textView = (TextView) findViewById(R.id.title);

        updateTrackInfo();
    }

    //On press play/pause
    public void playPause(View view) {
        AppCore.getInstance().musicSrv.playPause();

        //Changes icon depending on play/pause state
        if(AppCore.getInstance().musicSrv.isPlaying())
            view.setBackgroundResource(R.drawable.play);
        else
            view.setBackgroundResource(R.drawable.pause);
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
