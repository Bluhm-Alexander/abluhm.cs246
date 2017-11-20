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

    /**
     * Non-default constructor
     *
     * @param nameOfPlaylist Specified name of playlist
     */
    SimplePlaylist(String nameOfPlaylist) {
        playlistName = nameOfPlaylist;
        indexInCollection = -1;
        Log.w(TAG, "Created simple playlist named: " + nameOfPlaylist + " with no initialized index. Setting to -1");
    }

    /**
     * Default constructor
     *
     * @param nameOfPlaylist Specified name of playlist
     * @param indexInCollection Holds in index of its position in Main Playlist Collection
     */
    SimplePlaylist(String nameOfPlaylist, int indexInCollection) {
        playlistName = nameOfPlaylist;
        this.indexInCollection = indexInCollection;
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

/**
 * CompoundPlaylist
 *
 * Has a name
 * Holds an array of SimplePlaylists
 */
class CompoundPlaylist extends ArrayList<SimplePlaylist> {
    private static final String TAG = "CompoundPlaylist";
    private String nameOfPlaylistCollection;
    private final int indexInCollection;
    private int sizeOfDefaultPlaylists;

    /**
     * Default constructor
     *
     * @param name Name of Playlist
     */
    CompoundPlaylist(String name) {
        Log.d(TAG, "Creating object with name: " + name);
        nameOfPlaylistCollection = name;
        indexInCollection = -1;
    }

    /**
     * Non-default constructor
     *
     * @param name Name of Playlist
     * @param indexInCollection Index of its position in Main Playlist Collection
     */
    public CompoundPlaylist(String name, int indexInCollection) {
        Log.d(TAG, "Creating object with name: " + name);
        nameOfPlaylistCollection = name;
        this.indexInCollection = indexInCollection;
    }

    /**
     * @return Returns name of playlist
     */
    String getNameOfPlaylist() {
        return nameOfPlaylistCollection;
    }

    /**
     * @return Returns index of its position in Main Playlist Collection
     */
    public int getIndexInCollection() {
        return indexInCollection;
    }
}