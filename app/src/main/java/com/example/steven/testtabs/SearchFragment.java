package com.example.steven.testtabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by xflip on 11/14/2017.
 */

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private static SimplePlaylist searchResults;
    ListView results;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating SearchFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_layout, container, false);

        searchResults = AppCore.getInstance().mediaStorage.createSimplePlaylist("searchResults");
        results = (ListView) rootView.findViewById(R.id.results);
        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT);
            }
        });

        SongAdapter adapter = new SongAdapter(getActivity(), searchResults);
        results.setAdapter(adapter);

        setRetainInstance(true);


        return rootView;
    }

    private static void clearResults() {
        searchResults.clear();
    }

}