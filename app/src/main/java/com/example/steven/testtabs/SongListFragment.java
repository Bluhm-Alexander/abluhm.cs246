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
    private int playlistIndex;

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
        Log.d(TAG, "Attempting to play song through onListItemClick()");
        super.onListItemClick(listView, v, i, id);
        int playlistIndex = songList.getIndexInCollection();
        int songIndex = songList.get(i).getIndexInCollection();

        AppCore.getInstance().musicSrv.onSongPicked(playlistIndex, songIndex);
    }

    public void setSongs(SimplePlaylist playlist) {
        //Sets songs
        songList = playlist;
    }

    public String getPlaylistName() {
        return songList.getNameOfPlaylist();
    }
}
