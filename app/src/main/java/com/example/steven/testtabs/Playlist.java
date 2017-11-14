package com.example.steven.testtabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by xflip on 11/14/2017.
 */

class Playlist extends ArrayList<Song> {
    ArrayList<Song> defaultOrder;
    String playlistName;

    Playlist(String nameOfPlaylist) {
        playlistName = nameOfPlaylist;
        defaultOrder = new ArrayList<>();
    }

    String getPlaylistName() {
        return playlistName;
    }
    /*
    public void shuffle() {
        Collections.shuffle(this);
    }

    public void unShuffle() {
        clear();
        addAll(defaultOrder);
    }
    */
}