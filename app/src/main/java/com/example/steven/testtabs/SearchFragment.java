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
import android.widget.Button;
import android.widget.EditText;
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
    private static SimplePlaylist dummyPlaylst;
    ListView results;
    SongAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating SearchFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_layout, container, false);

        searchResults = new SimplePlaylist("Search Results");
        dummyPlaylst = AppCore.getInstance().mediaStorage.createSimplePlaylist("Searched Song");
        AppCore.getInstance().mediaStorage.createSimplePlaylist("searchResults");
        adapter = new SongAdapter(getActivity(), searchResults);
        results = rootView.findViewById(R.id.results);
        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dummyPlaylst.clear();
                dummyPlaylst.add(searchResults.get(i));
                AppCore.getInstance().musicSrv.onSongPicked(dummyPlaylst.getIndexInCollection(), 0);
            }
        });

        results.setAdapter(adapter);

        final EditText editText = rootView.findViewById(R.id.editText);
        Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearResults();
                CharSequence sequence = editText.getText();
                String string = sequence.toString();
                string = string.toLowerCase();
                if(sequence.length() != 0) {
                    for (int i = 0; i < AppCore.getInstance().mediaStorage.getSongs().size(); i++) {
                        Song song = AppCore.getInstance().mediaStorage.getSongs().get(i);
                        if (song.getTitle().toLowerCase().contains(string)) {
                            searchResults.add(song);
                        }
                    }
                }
                results.setAdapter(adapter);
            }
        });

        setRetainInstance(true);

        return rootView;
    }

    private static void clearResults() {
        searchResults.clear();
        dummyPlaylst.clear();
    }

}