package com.example.steven.testtabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by xflip on 11/14/2017.
 */

public class SearchFragment extends ListFragment {
    private static final String TAG = "SearchFragment";
    private static SimplePlaylist searchResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating SearchFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_layout, container, false);

        searchResults = AppCore.getInstance().mediaStorage.createSimplePlaylist("searchResults");

        SongAdapter adapter = new SongAdapter(getActivity(), searchResults);
        setListAdapter(adapter);

        setRetainInstance(true);


        return rootView;
    }

    private static void clearResults() {
        searchResults.clear();

    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        switch(position) {
            case 0:
                //Nothing for now
                Toast.makeText(getActivity(), "Album art: Off", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}