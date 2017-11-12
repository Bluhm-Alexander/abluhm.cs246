package com.example.steven.testtabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NowPlaying extends AppCompatActivity {

    public TextView textView;
    public String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        textView = (TextView) findViewById(R.id.title);
        title = AppCore.getInstance().currentSongName;
        textView.setText(title);
    }

    //On press play/pause
    public void playPause(View view) {
        AppCore.getInstance().musicSrv.playPause();
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
        textView = (TextView) findViewById(R.id.title);
        title = AppCore.getInstance().getSongName();
        textView.setText(title);
    }

}
