package com.example.steven.testtabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View v, int i, int i1, long id) {
                Log.d(TAG, "Pressed song at index: " + i1 + " of playlist at index: " + playlists.get(i).getIndexInCollection());
                Log.d(TAG, "Song at: " + i1 + " = " + playlists.get(i).get(i1).getTitle());

                AppCore.getInstance().musicSrv.onSongPicked(playlists.get(i).getIndexInCollection(), i1);
                return true;
            }
        });

        if(canAddPlaylists) {
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                    if (i > 0 && !playlists.get(i - 1).isEmpty()) {
                        if (expandableListView.isGroupExpanded(i))
                            expandableListView.collapseGroup(i);
                        else {
                            collapseAll();
                            expandableListView.expandGroup(i);
                        }
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
        Log.d("test", "groupCount: " + adapter.getGroupCount());
        expandableListView.setAdapter(adapter);
    }
}