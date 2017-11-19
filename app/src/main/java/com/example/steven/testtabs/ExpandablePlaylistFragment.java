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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating ExpandableListFragment");
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listfragment, container, false);

        expandableListView = (ExpandableListView)rootView.findViewById(R.id.expandable_list);
        adapter = new ExpandablePlaylistAdapter(getActivity(), playlists);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View v, int i, int i1, long id) {
                Log.d(TAG, "Attempting to play song through onChildClick()");
                int parentIndex = playlists.get(i).getIndexInCollection();
                int childIndex  = playlists.get(i).get(i1).getIndexInCollection();
                AppCore.getInstance().musicSrv.onSongPicked(parentIndex, childIndex);
                return true;
            }
        });

        setRetainInstance(true);

        return rootView;
    }

    public void setSongs(CompoundPlaylist p) {
        playlists = p;
    }
}
