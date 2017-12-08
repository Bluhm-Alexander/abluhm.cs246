package com.example.steven.testtabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

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
        if(!canAddPlaylists)
            return playlists.size();
        else
            return playlists.size() + 1;
    }

    @Override
    public int getChildrenCount(int parent) {
        if(!canAddPlaylists)
            return playlists.get(parent).size();
        if(parent == 0)
            return 2;
        else {
            SimplePlaylist playlist = (SimplePlaylist) getGroup(parent);
            return playlist.size() + 1;
        }
    }

    @Override
    public Object getGroup(int parent) {
        if(!canAddPlaylists)
            return playlists.get(parent);
        if(parent == 0)
            return "Edit playlists";
        else
            return playlists.get(parent - 1);
    }

    @Override
    public Object getChild(int parent, int child) {
        if(!canAddPlaylists)
            return playlists.get(parent).get(child);
        if(parent == 0) {
            if(child == 0) {
                return "Add playlist";
            }
            else if(child == 1) {
                if(AppCore.getInstance().removingPlaylists)
                    return "Finish removing playlists";
                else
                    return "Remove playlist";
            }
            else {
                Log.e("ExpandableAdapter", "Should not be more than two children in parent 0");
                return null;
            }
        }
        else {
            if(child == 0) {
                SimplePlaylist playlist = (SimplePlaylist) getGroup(parent);
                if(AppCore.getInstance().addingToPlaylistIndex == playlist.getIndexInCollection())
                    return "Finish editing playlist";
                else
                    return "Edit playlist";
            }
            else
                return playlists.get(parent - 1).get(child - 1);
        }
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
        if(canAddPlaylists && parent == 0) {
            String string = (String) getGroup(parent);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.add_playlist, parentView, false);
            TextView textView = (convertView.findViewById(R.id.new_playlist));
            textView.setText(string);
        }
        else {
            final SimplePlaylist currentPlaylist = (SimplePlaylist) getGroup(parent);

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist, parentView, false);

            if(canAddPlaylists) {
                ImageView imageView = convertView.findViewById(R.id.playlist_image_view);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppCore.getInstance().mediaStorage.getUserPlaylists().remove(currentPlaylist);
                        //Fill in gaps
                        for(int i = currentPlaylist.getIndexInUserPlaylist() + 1; i < AppCore.getInstance().mediaStorage.getUserPlaylists().size(); i++) {
                            SimplePlaylist playlist = AppCore.getInstance().mediaStorage.getUserPlaylist(i);
                            playlist.indexInUserPlaylist--;
                            playlist.indexInCollection--;
                        }

                        if(AppCore.getInstance().mediaStorage.getUserPlaylists().isEmpty())
                            AppCore.getInstance().removingPlaylists = false;

                        //To force update the list view
                        ExpandablePlaylistFragment current = (ExpandablePlaylistFragment) AppCore.getInstance().pagerAdapter.getItem(AppCore.getInstance().viewPager.getCurrentItem());
                        current.updatePlaylists();
                    }
                });
                if (AppCore.getInstance().removingPlaylists) {
                    imageView.setVisibility(View.VISIBLE);
                }
                else {
                    imageView.setVisibility(View.GONE);
                }
            }
            else {
                ImageView imageView = convertView.findViewById(R.id.playlist_image_view);
                imageView.setVisibility(View.GONE);
            }


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
        if((child == 0 || (parent == 0 && child == 1)) && canAddPlaylists) {
            String string = (String) getChild(parent, child);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.add_to_playlist, parentView, false);

            TextView textView = (convertView.findViewById(R.id.new_song));
            textView.setText(string);
        }
        else {
            final Song currentSong = (Song) getChild(parent, child);
            String title = currentSong.getTitle();
            String artist = currentSong.getArtist();
            String album = currentSong.getAlbum();

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.song, parentView, false);

            if(AppCore.getInstance().addingToPlaylistIndex >= 0) {
                ImageView imageView = convertView.findViewById(R.id.imageView);
                imageView.setVisibility(View.VISIBLE);
                int addingIndex = AppCore.getInstance().addingToPlaylistIndex;
                final SimplePlaylist currentPlaylist = (SimplePlaylist) getGroup(parent);
                final SimplePlaylist addingToPlaylist = AppCore.getInstance().mediaStorage.getSimplePlaylist(addingIndex);

                if(canAddPlaylists && addingIndex == currentPlaylist.getIndexInCollection()) {
                    imageView.setImageResource(R.drawable.remove_icon);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Remove song
                            if (!currentPlaylist.contains(currentSong)) {
                                Log.e("ExpandableAdapter", "Removing a song that doesn't exist. Fix this");
                            } else {
                                //Remove song from playlist
                                currentPlaylist.remove(currentSong);
                                Toast.makeText(context, "Removed " + currentSong.getTitle() + " from user playlist", Toast.LENGTH_SHORT).show();

                                //To force update the list view
                                ExpandablePlaylistFragment current = (ExpandablePlaylistFragment) AppCore.getInstance().pagerAdapter.getItem(AppCore.getInstance().currentTab);
                                current.updatePlaylists();
                            }
                        }
                    });
                }
                else {
                    imageView.setImageResource(R.drawable.add_icon);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Add song
                            if (addingToPlaylist.contains(currentSong)) {
                                Toast.makeText(context, "Playlist already contains selected song", Toast.LENGTH_SHORT).show();
                                Log.w("AddSongToPlaylist", "Attempted to add already existing song to user playlist");
                            }
                            else {
                                //Add song to playlist
                                addingToPlaylist.add(currentSong);
                                Toast.makeText(context, "Added " + currentSong.getTitle() + " to user playlist", Toast.LENGTH_SHORT).show();
                                //To force update the list view
                                ExpandablePlaylistFragment current = (ExpandablePlaylistFragment) AppCore.getInstance().pagerAdapter.getItem(AppCore.getInstance().pagerAdapter.getCount() - 2);
                                current.updatePlaylists();
                            }
                        }
                    });
                }
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
