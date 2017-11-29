package com.example.steven.testtabs;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Flip on 11/15/2017.
 */

public class ExpandablePlaylistAdapter extends BaseExpandableListAdapter {
    private Context context;
    private CompoundPlaylist playlists;

    ExpandablePlaylistAdapter(Context c, CompoundPlaylist p) {
        context = c;
        playlists = p;
    }

    @Override
    public int getGroupCount() {
        return playlists.size();
    }

    @Override
    public int getChildrenCount(int parent) {
        return playlists.get(parent).size();
    }

    @Override
    public SimplePlaylist getGroup(int parent) {
        return playlists.get(parent);
    }

    @Override
    public Song getChild(int parent, int child) {
        return playlists.get(parent).get(child);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int parent, int child) {
        return getChild(parent, child).getID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int parent, boolean isExpanded, View convertView, ViewGroup parentView) {
        Log.d("TMP", "Parent index: " + parent);
        Log.d("TMP", "Parent size: " + getGroupCount());
        if(playlists.get(0) == null)
            Log.d("TMP", "NULL");

        SimplePlaylist currentPlaylist = getGroup(parent);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist, parentView, false);
        }

        TextView playlistView = convertView.findViewById(R.id.playlist_title);
        TextView songCountView = convertView.findViewById(R.id.playlist_song_count);

        playlistView.setTypeface(null, Typeface.BOLD);

        playlistView.setText(currentPlaylist.getNameOfPlaylist());
        songCountView.setText(currentPlaylist.size() + " songs");

        return convertView;
    }

    @Override
    public View getChildView(int parent, int child, boolean isLastChild, View convertView, ViewGroup parentView) {
        Song currentSong = getChild(parent, child);
        String title = currentSong.getTitle();
        String artist = currentSong.getArtist();
        String album = currentSong.getAlbum();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.song, parentView, false);
        }

        TextView titleView  = convertView.findViewById(R.id.song_title);
        TextView artistView = convertView.findViewById(R.id.song_artist);
        TextView albumView  = convertView.findViewById(R.id.song_album);

        titleView.setText(title);
        artistView.setText(artist);
        albumView.setText(album);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
