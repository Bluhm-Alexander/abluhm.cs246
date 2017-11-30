package com.example.steven.testtabs;

import android.database.Observable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

/**
 * SimplePlaylist
 *
 * Has a name
 * Holds an array of songs
 * Holds an index of its position in the Main Playlist Collection
 */
class SimplePlaylist extends ArrayList<Song> {
    private static final String TAG = "SimplePlaylist";
    private String playlistName;
    private final int indexInCollection;
    private int numSongs;

    /**
     * Non-default constructor
     *
     * @param nameOfPlaylist Specified name of playlist
     */
    SimplePlaylist(String nameOfPlaylist) {
        Log.d(TAG, "Creating SimplePlaylist with name: " + nameOfPlaylist);
        playlistName = nameOfPlaylist;
        if(AppCore.getInstance().mediaStorage == null)
            indexInCollection = -1;
        else
            indexInCollection = AppCore.getInstance().mediaStorage.getSimplePlaylists().size();
    }

    /**
     * @return Returns name of playlist
     */
    String getNameOfPlaylist() {
        return playlistName;
    }

    /**
     * @return Returns index of its position in Main Playlist Collection
     */
    int getIndexInCollection() {
        return indexInCollection;
    }
}

