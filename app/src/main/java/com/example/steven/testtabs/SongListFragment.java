package com.example.steven.testtabs;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongListFragment extends ListFragment {
    ArrayList<Song> songList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listfragment, container, false);

        SongAdapter adapter = new SongAdapter(getActivity(),songList);
        setListAdapter(adapter);

        setRetainInstance(true);

        return rootView;
    }

    public void setSongs(ArrayList<Song> arrayList) {
        //Get songs
        songList = arrayList;
    }
}
