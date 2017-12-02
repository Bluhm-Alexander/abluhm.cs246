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
    private boolean canAddPlaylists;

    ExpandablePlaylistAdapter(Context c, CompoundPlaylist p, boolean canAdd) {
        context = c;
        playlists = p;
        canAddPlaylists = canAdd;
    }

    @Override
    public int getGroupCount() {
        if(canAddPlaylists)
            return playlists.size() + 1;
        else
            return playlists.size();
    }

    @Override
    public int getChildrenCount(int parent) {
        if(!canAddPlaylists)
            return playlists.get(parent).size();
        if(parent == 0)
            return 0;
        else
            return playlists.get(parent - 1).size();
    }

    @Override
    public Object getGroup(int parent) {
        if(!canAddPlaylists)
            return playlists.get(parent);
        if(parent == 0)
            return "New Playlist";
        else
            return playlists.get(parent - 1);
    }

    @Override
    public Song getChild(int parent, int child) {
        if(!canAddPlaylists)
            return playlists.get(parent).get(child);
        if(parent == 0)
            return null;
        else
            return playlists.get(parent - 1).get(child);
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
        LayoutInflater inflater;

        if(parent == 0 && canAddPlaylists) {
            String string = (String) getGroup(parent);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.add_playlist, parentView, false);

            TextView textView = (convertView.findViewById(R.id.new_playlist));
        }
        else {
            SimplePlaylist currentPlaylist = (SimplePlaylist) getGroup(parent);

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist, parentView, false);

            TextView playlistView = convertView.findViewById(R.id.playlist_title);
            TextView songCountView = convertView.findViewById(R.id.playlist_song_count);

            playlistView.setTypeface(null, Typeface.BOLD);

            playlistView.setText(currentPlaylist.getNameOfPlaylist());
            songCountView.setText(currentPlaylist.size() + " songs");

        }
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
