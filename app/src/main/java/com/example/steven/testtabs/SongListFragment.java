package com.example.steven.testtabs;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongListFragment extends ListFragment {
    private final String TAG = "SongListFragment";
    private Playlist songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating SongListFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listfragment, container, false);

        //songList = AppCore.getInstance().allLists.get(0);
        SongAdapter adapter = new SongAdapter(getActivity(), songList);
        setListAdapter(adapter);

        setRetainInstance(true);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    public void setSongs(Playlist playlist) {
        //Sets songs
        songList = playlist;
    }

    public String getPlaylistName() {
        return songList.getPlaylistName();
    }
}
