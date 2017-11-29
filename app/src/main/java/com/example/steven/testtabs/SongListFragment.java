package com.example.steven.testtabs;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SongListFragment extends ListFragment {
    private static final String TAG = "SongListFragment";
    private SimplePlaylist songList;

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
    public void onListItemClick(ListView listView, View v, int i, long id) {
        Log.d(TAG, "Pressed song at index: " + i);
        super.onListItemClick(listView, v, i, id);

        AppCore.getInstance().musicSrv.onSongPicked(songList.getIndexInCollection(), i);
    }

    public void setSongs(SimplePlaylist p) {
        //Sets songs
        if(p == null)
            Log.e(TAG, "Passing a null playlist to SongListFragment");
        else {
            Log.d(TAG, "Setting playlist with name: " + p.getNameOfPlaylist() + ".\n" +
                    "Playlist's index inside of playlist collection: " + p.getIndexInCollection());
        }
        songList = p;
    }

    public String getPlaylistName() {
        return songList.getNameOfPlaylist();
    }
}
