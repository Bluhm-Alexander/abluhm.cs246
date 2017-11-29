package com.example.steven.testtabs;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "PagerAdapter";
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();


    private SettingsFragment settingsFragment;
    private ExpandablePlaylistFragment playlistFragment;

    PagerAdapter(FragmentManager fm) {
        super(fm);

        settingsFragment = new SettingsFragment();
        playlistFragment = new ExpandablePlaylistFragment();

        playlistFragment.setSongs(AppCore.getInstance().mediaStorage.getUserPlaylists());
    }

    /**
     * Adds new fragment for a new tab with passed playlist
     *
     * @param fragment SongListFragment containing a listview of playlist
     * @param playlist Playlist of songs
     * @return returns true if NOT adding a fragment with a duplicate name of a previous one
     */
    boolean addFragment(SongListFragment fragment, SimplePlaylist playlist) {
        Log.d(TAG, "Adding new SongListFragment with name: " + playlist.getNameOfPlaylist() + " and size of: " + playlist.size());
        if(fragmentList.contains(fragment)) {
            Log.e(TAG, "Attempted to add a new songListFragment that matches an already existing fragment");
            return false;
        }

        if(playlist.size() == 0)
            Log.w(TAG, "SimplePlaylist is empty!");
        if(playlist == null)
            Log.w(TAG, "SimplePlaylist is null!");

        fragmentList.add(fragment);
        fragmentTitleList.add(playlist.getNameOfPlaylist());
        fragment.setSongs(playlist);

        return true;
    }

    /**
     * Adds new fragment for a new tab with passed playlist
     *
     * @param fragment ExpandedPlaylistFragment containing an expanded listview of playlists
     * @param playlists Collection of playlists for ExpandedListView
     * @return returns true if NOT adding a fragment with a duplicate name of a previous one
     */

    boolean addFragment(ExpandablePlaylistFragment fragment, CompoundPlaylist playlists) {
        Log.d(TAG, "Adding ExpandablePlaylistFragment with name: " + playlists.getNameOfPlaylist() + " and size of: " + playlists.size());
        if(fragmentList.contains(fragment)) {
            Log.e(TAG, "Attempted to add a new expandedPlaylistFragment that matches an already existing fragment");
            return false;
        }
        if(playlists.size() == 0)
            Log.w(TAG, "CompoundPlaylist is empty!");
        if(playlists == null)
            Log.w(TAG, "CompoundPlaylist is null!");

        fragmentList.add(fragment);
        fragmentTitleList.add(playlists.getNameOfPlaylist());
        fragment.setSongs(playlists);

        return true;
    }

    /**
     * Gets name of fragment at position
     *
     * @param position index of fragment/tab
     * @return returns name of fragment (name of held playlist)
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == fragmentList.size())
            return "Playlists";
        else if(position == fragmentList.size() + 1)
            return "Settings";
        return fragmentTitleList.get(position);
    }

    /**
     * Gets fragment object at position
     *
     * @param position index of fragment/tab
     * @return returns fragment object at position
     */
    @Override
    public Fragment getItem(int position) {
        if(position == fragmentList.size())
            return playlistFragment;
        else if(position == fragmentList.size() + 1)
            return settingsFragment;
        return fragmentList.get(position);
    }

    /**
     *
     * @return returns size of fragmentList
     */
    @Override
    public int getCount() {
        return fragmentList.size() + 2;
    }
}