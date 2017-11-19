package com.example.steven.testtabs;

import android.database.Observable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

/**
 * Created by xflip on 11/14/2017.
 */


class Playlist<T> extends ArrayList<T> {


    public Playlist() {

    }
}

//ArrayList of songs
class SimplePlaylist extends ArrayList<Song> {
    private static final String TAG = "SimplePlaylist";
    private ArrayList<Song> defaultOrder;
    private String playlistName;
    private final int indexInCollection;

    SimplePlaylist(String nameOfPlaylist) {
        playlistName = nameOfPlaylist;
        defaultOrder = new ArrayList<>();
        indexInCollection = -1;
        Log.w(TAG, "Created simple playlist named: " + nameOfPlaylist + " with no initialized index. Setting to -1");
    }

    SimplePlaylist(String name, int indexInCollection) {
        playlistName = name;
        defaultOrder = new ArrayList<>();
        this.indexInCollection = indexInCollection;
    }

    String getNameOfPlaylist() {
        return playlistName;
    }

    int getIndexInCollection() {
        return indexInCollection;
    }
}

//ArrayList of playlists
class CompoundPlaylist extends ArrayList<SimplePlaylist> {
    private static final String TAG = "CompoundPlaylist";
    private String nameOfPlaylistCollection;
    private final int indexInCollection;
    private boolean finishedAddingDefaultPlaylists;
    private int sizeOfDefaultPlaylists;

    CompoundPlaylist(String name) {
        Log.d(TAG, "Creating object with name: " + name);
        nameOfPlaylistCollection = name;
        indexInCollection = -1;
    }

    public CompoundPlaylist(String name, int indexInCollection) {
        Log.d(TAG, "Creating object with name: " + name);
        nameOfPlaylistCollection = name;
        this.indexInCollection = indexInCollection;
    }

    String getNameOfPlaylist() {
        return nameOfPlaylistCollection;
    }

    public void setFinishedAddingDefaultPlaylists(boolean option) {
        finishedAddingDefaultPlaylists = option;
        sizeOfDefaultPlaylists = size();
    }

    public int sizeOfDefaultPlaylists() {
        return sizeOfDefaultPlaylists;
    }

    public int getIndexInCollection() {
        return indexInCollection;
    }
}