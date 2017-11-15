package com.example.steven.testtabs;

import android.util.Log;

class Song {
    private long id;
    private String title;
    private String artist;
    private String album;

    Song(long songID, String songTitle, String songArtist, String songAlbum) {
        id     = songID;
        title  = songTitle;
        artist = songArtist;
        album  = songAlbum;
    }

    Song () {
        id = 0;
        title  = "";
        artist = "";
        album  = "";
    }

    long getID()         { return id;     }
    String getTitle()    { return title;  }
    String getArtist()   { return artist; }
    String getAlbum()    { return album;  }

    String getSongInfo() { return (getTitle() + " - " + getArtist() + " - " + getAlbum() + " - " + getID() + "\n"); }
    void logSongInfo() { Log.d("Song class", getSongInfo()); }
}
