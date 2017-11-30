package com.example.steven.testtabs;

import android.util.Log;

import java.util.ArrayList;

/**
 * CompoundPlaylist
 *
 * Has a name
 * Holds an array of SimplePlaylists
 */
class CompoundPlaylist extends ArrayList<SimplePlaylist> {
    private static final String TAG = "CompoundPlaylist";
    private String nameOfPlaylistCollection;

    /**
     * Non-default constructor
     *
     * @param name Name of Playlist
     */
    CompoundPlaylist(String name) {
        Log.d(TAG, "Creating CompoundPlaylist with name: " + name);
        nameOfPlaylistCollection = name;
    }

    /**
     * @return Returns name of playlist
     */
    String getNameOfPlaylist() {
        return nameOfPlaylistCollection;
    }
}
