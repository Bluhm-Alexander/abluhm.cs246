package com.example.steven.testtabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Flip on 11/15/2017.
 */

public class ExpandablePlaylistFragment extends Fragment {
    private static final String TAG = "ExpandableListFragment";
    ExpandableListView expandableListView;
    CompoundPlaylist playlists;
    ExpandablePlaylistAdapter adapter;
    boolean canAddPlaylists = false;
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.listfragment, container, false);

        expandableListView = (ExpandableListView)rootView.findViewById(R.id.expandable_list);
        adapter = new ExpandablePlaylistAdapter(getActivity(), playlists, canAddPlaylists);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);

        if(canAddPlaylists) {
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                    if (expandableListView.isGroupExpanded(i)) {
                        expandableListView.collapseGroup(i);
                        AppCore.getInstance().currentPlaylistIndexInExpandableListView = -1;
                    }
                    else {
                        collapseAll();
                        expandableListView.expandGroup(i);
                        Log.d("test", "setting index to: " + i);
                        AppCore.getInstance().currentPlaylistIndexInExpandableListView = i;
                    }
                    return true;
                }

                private void collapseAll() {
                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        expandableListView.collapseGroup(i);
                    }
                }
            });
        }

        if(canAddPlaylists) {
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View v, int i, int i1, long id) {
                    if(i == 0) {
                        //New playlist
                        if (i1 == 0) {
                            createPlaylist();
                        }
                        else {
                            AppCore.getInstance().removingPlaylists = !AppCore.getInstance().removingPlaylists;
                            expandableListView.setAdapter(adapter);
                            if(AppCore.getInstance().currentPlaylistIndexInExpandableListView >= 0)
                                expandableListView.expandGroup(AppCore.getInstance().currentPlaylistIndexInExpandableListView);
                        }
                    }
                    else if(i1 == 0) {
                        if(AppCore.getInstance().addingToPlaylistIndex < 0) {
                            //Add songs to playlist
                            Log.d(TAG, "Adding song to user playlist");
                            //AppCore.getInstance().addingToPlaylistIndex = playlists.get(i - 1).getIndexInCollection();
                            AppCore.getInstance().addingToPlaylistIndex = playlists.get(i - 1).getIndexInCollection();
                            Log.d("test", "Setting addingToPlaylistIndex to " + AppCore.getInstance().addingToPlaylistIndex);
                            Toast.makeText(getActivity(), "Select songs to add", Toast.LENGTH_SHORT).show();
                            AppCore.getInstance().viewPager.setCurrentItem(0);
                        }
                        else {
                            Log.d(TAG, "Finished adding songs to user playlist");
                            AppCore.getInstance().addingToPlaylistIndex = -1;
                        }
                        //Refreshes playlist
                        expandableListView.setAdapter(adapter);
                        expandableListView.expandGroup(i);
                    }

                    else {
                        Log.d(TAG, "Pressed song at index: " + (i1 - 1) + " of playlist at index: " + playlists.get(i - 1).getIndexInCollection());
                        AppCore.getInstance().musicSrv.onSongPicked(playlists.get(i - 1).getIndexInCollection(), i1 - 1);
                        expandableListView.setAdapter(adapter);
                        expandableListView.expandGroup(i);
                    }
                    return true;
                }
                private void collapseAll() {
                    for (int i = 1; i < adapter.getGroupCount(); i++) {
                        expandableListView.collapseGroup(i);
                    }
                }
            });
        }
        else {
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View v, int i, int i1, long id) {
                    Log.d(TAG, "Pressed song at index: " + i1 + " of playlist at index: " + playlists.get(i).getIndexInCollection());
                    Log.d(TAG, "Song at: " + i1 + " = " + playlists.get(i).get(i1).getTitle());

                    AppCore.getInstance().musicSrv.onSongPicked(playlists.get(i).getIndexInCollection(), i1);
                    return true;
                }
            });
        }

        setRetainInstance(true);

        return rootView;
    }

    public void setSongs(CompoundPlaylist p) {
        //sets songs
        if(p == null)
            Log.e(TAG, "Passing a null CompoundPlaylist to ExpandablePlaylistFragment");
        else {
            Log.d(TAG, "Setting SimplePlaylist with name: " + p.getNameOfPlaylist() + ".\n");
        }
        playlists = p;
    }

    public void setCanAddPlaylists(boolean b) {
        canAddPlaylists = b;
    }

    public void updatePlaylists() {
        expandableListView.setAdapter(adapter);
        expandableListView.expandGroup(AppCore.getInstance().currentPlaylistIndexInExpandableListView);
    }

    public void createPlaylist() {
        View view = (LayoutInflater.from(getContext())).inflate(R.layout.userinput, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        final EditText userInput = (EditText) view.findViewById(R.id.user_input);

        builder.setCancelable(true);
        builder.setTitle("Create New Playlist");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CharSequence sequence = userInput.getText();
                if (sequence.length() < 1) {
                    Toast.makeText(getContext(), "Must specify a name for playlist", Toast.LENGTH_SHORT).show();
                }
                else {
                    String name = sequence.toString();
                    SimplePlaylist userPlaylist = AppCore.getInstance().mediaStorage.createUserPlaylist(name);

                    //I can't figure out a better way to refresh the list after adding a new playlist
                    updatePlaylists();
                }
            }
        });
        builder.show();
    }
}