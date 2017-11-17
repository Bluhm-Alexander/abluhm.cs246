package com.example.steven.testtabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Flip on 11/15/2017.
 */

public class ExpandablePlaylistFragment extends Fragment {
    private final String TAG = "ExpandableListFragment";
    ExpandableListView expandableListView;
    ArrayList<Playlist> playlists;
    ExpandablePlaylistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating ExpandableListFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listfragment, container, false);

        expandableListView = (ExpandableListView)rootView.findViewById(R.id.expandable_list);
        adapter = new ExpandablePlaylistAdapter(getActivity(), playlists);
        expandableListView.setAdapter(adapter);

        setRetainInstance(true);

        return rootView;
    }

    public void setSongs(ArrayList<Playlist> p) {
        playlists = p;
    }
}
