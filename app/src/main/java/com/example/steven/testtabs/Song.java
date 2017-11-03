package com.example.steven.testtabs;

public class Song {
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

    long getID()             { return id;     }
    public String getTitle() { return title;  }
    String getArtist()       { return artist; }
    String getAlbum()        { return album;  }
}
