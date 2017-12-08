package com.example.steven.testtabs;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SongAdapter extends BaseAdapter {
    private SimplePlaylist songs;
    private LayoutInflater songInf;

    SongAdapter(Context c, SimplePlaylist theSongs){
        songs = theSongs;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return songs.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return songs.get(arg0).getID();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        //map to song layout
        ConstraintLayout songLay = (ConstraintLayout) songInf.inflate
                (R.layout.song, parent, false);
        //get song using position
        final Song currentSong = songs.get(position);
        //get title and artist views
        if(AppCore.getInstance().addingToPlaylistIndex >= 0) {
            ImageView imageView = songLay.findViewById(R.id.imageView);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.add_icon);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppCore.getInstance().mediaStorage.getSimplePlaylist(AppCore.getInstance().addingToPlaylistIndex).contains(currentSong)) {
                        Toast.makeText(convertView.getContext(), "Playlist already contains selected song", Toast.LENGTH_SHORT).show();
                        Log.w("AddSongToPlaylist", "Attempted to add already existing song to user playlist");
                    }
                    else {
                        //Add song to playlist
                        AppCore.getInstance().mediaStorage.getSimplePlaylist(AppCore.getInstance().addingToPlaylistIndex).add(currentSong);
                        Toast.makeText(convertView.getContext(), "Added " + currentSong.getTitle() + " to user playlist", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        TextView songView   = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        TextView albumView  = (TextView)songLay.findViewById(R.id.song_album);

        //get title and artist strings
        songView.setText(currentSong.getTitle());
        artistView.setText(currentSong.getArtist());
        albumView.setText(currentSong.getAlbum());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
