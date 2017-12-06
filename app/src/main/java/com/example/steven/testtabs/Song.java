package com.example.steven.testtabs;

import android.util.Log;

class Song {
    private long albumID;
    private long id;
    private String title;
    private String artist;
    private String album;
    private final int indexInLibrary;
    private String albumArt;

    Song () {
        albumID = -1;
        id = 0;
        title  = "";
        artist = "";
        album  = "";
        albumArt = "";
        indexInLibrary = 0;
    }

    Song(long songID, String songTitle, String songArtist, String songAlbum, long albumId) { // String artAlbum, long albumId
        id     = songID;
        title  = songTitle;
        artist = songArtist;
        album  = songAlbum;
        //albumArt = artAlbum;
        albumID = albumId;

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
    long getAlbumID()          { return albumID;}
    long getID()               { return id;     }
    String getTitle()          { return title;  }
    String getArtist()         { return artist; }
    String getAlbum()          { return album;  }
    String getAlbumArt()       { return albumArt;}

    String getSongInfo() { return (getIndexInCollection() + " - " + getTitle() + " - " + getArtist() + " - " + getAlbum() + " - " + getID() + "\n"); }
    void logSongInfo()   { Log.d("Song class", getSongInfo()); }
}
