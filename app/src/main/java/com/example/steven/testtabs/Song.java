package com.example.steven.testtabs;

import android.util.Log;

class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    private final int indexInLibrary;

    Song () {
        id = 0;
        title  = "";
        artist = "";
        album  = "";
        indexInLibrary = 0;
    }

    Song(long songID, String songTitle, String songArtist, String songAlbum) {
        id     = songID;
        title  = songTitle;
        artist = songArtist;
        album  = songAlbum;
        indexInLibrary = 0;
    }

    Song(long songID, String songTitle, String songArtist, String songAlbum, int indexInLibrary) {
        id     = songID;
        title  = songTitle;
        artist = songArtist;
        album  = songAlbum;
        this.indexInLibrary = indexInLibrary;
    }

    int getIndexInCollection() { return indexInLibrary; }
    long getID()               { return id;     }
    String getTitle()          { return title;  }
    String getArtist()         { return artist; }
    String getAlbum()          { return album;  }

    String getSongInfo() { return (getIndexInCollection() + " - " + getTitle() + " - " + getArtist() + " - " + getAlbum() + " - " + getID() + "\n"); }
    void logSongInfo()   { Log.d("Song class", getSongInfo()); }
}
