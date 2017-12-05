package com.example.steven.testtabs;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
            return playlists.get(parent - 1).size() + 1;
    }

    @Override
    public Object getGroup(int parent) {
        if(!canAddPlaylists)
            return playlists.get(parent);
        if(parent == 0)
            return "New playlist";
        else
            return playlists.get(parent - 1);
    }

    @Override
    public Object getChild(int parent, int child) {
        if(!canAddPlaylists)
            return playlists.get(parent).get(child);
        if(child == 0)
            return "Add to playlist";
        else
            return playlists.get(parent - 1).get(child - 1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int parent, int child) {
        return child;
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
    public View getChildView(final int parent, final int child, boolean isLastChild, View convertView, final ViewGroup parentView) {
        LayoutInflater inflater;

        if(child == 0 && canAddPlaylists) {
            String string = (String) getChild(parent, child);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.add_to_playlist, parentView, false);

            TextView textView = (convertView.findViewById(R.id.new_song));
            if(AppCore.getInstance().addingToPlaylistIndex >= 0) {
                textView.setText("Stop adding to playlist");
            }
            else {
                textView.setText("Add song");
            }

        }
        else {
            final Song currentSong = (Song) getChild(parent, child);
            final String title = currentSong.getTitle();
            String artist = currentSong.getArtist();
            String album = currentSong.getAlbum();

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.song, parentView, false);

            if(AppCore.getInstance().addingToPlaylistIndex >= 0) {
                ImageView imageView = convertView.findViewById(R.id.imageView);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(AppCore.getInstance().mediaStorage.getSimplePlaylist(AppCore.getInstance().addingToPlaylistIndex).contains(currentSong))
                            Toast.makeText(context, "Playlist already contains selected song", Toast.LENGTH_SHORT).show();
                        else {
                            AppCore.getInstance().mediaStorage.getSimplePlaylist(AppCore.getInstance().addingToPlaylistIndex).add(currentSong);
                            Toast.makeText(context, "Added " + title + " to user playlist", Toast.LENGTH_SHORT).show();
                            ExpandablePlaylistFragment current = (ExpandablePlaylistFragment) AppCore.getInstance().pagerAdapter.getItem(AppCore.getInstance().viewPager.getCurrentItem());
                            current.updatePlaylists();
                        }
                    }
                });
            }
            TextView titleView = convertView.findViewById(R.id.song_title);
            TextView artistView = convertView.findViewById(R.id.song_artist);
            TextView albumView = convertView.findViewById(R.id.song_album);

            titleView.setText(title);
            artistView.setText(artist);
            albumView.setText(album);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
