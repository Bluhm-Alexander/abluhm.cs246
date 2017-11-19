package com.example.steven.testtabs;

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

    private Fragment settingsFragment;
    private int settingsIndex = 0;
    PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    boolean addFragment(SongListFragment fragment, SimplePlaylist playlist) {
        Log.d(TAG, "Adding new SongListFragment with name: " + playlist.getNameOfPlaylist() + " and size of: " + playlist.size());
        if(fragmentList.contains(fragment)) {
            Log.e(TAG, "Attempted to add a new songListFragment that matches an already existing fragment");
            return false;
        }

        fragmentList.add(fragment);
        fragmentTitleList.add(playlist.getNameOfPlaylist());
        fragment.setSongs(playlist);

        if(settingsIndex > 0 && fragmentList.size() - 1 > settingsIndex) {
            Log.d(TAG, "Creating a new songListFragment AFTER the settings tab was created. Moving settings tab to back");
            moveSettingsTabToBack();
        }
        return true;
    }

    boolean addFragment(ExpandablePlaylistFragment fragment, CompoundPlaylist playlists) {
        Log.d(TAG, "Adding ExpandablePlaylistFragment with name: " + playlists.getNameOfPlaylist() + " and size of: " + playlists.size());
        if(fragmentList.contains(fragment)) {
            Log.e(TAG, "Attempted to add a new expandedPlaylistFragment that matches an already existing fragment");
            return false;
        }
        if(playlists.size() == 0)
            Log.e(TAG, "Playlist is empty!");
        if(playlists == null)
            Log.e(TAG, "Playlist is null!");

        fragmentList.add(fragment);
        fragmentTitleList.add(playlists.getNameOfPlaylist());
        fragment.setSongs(playlists);

        if(settingsIndex > 0 && fragmentList.size() - 1 > settingsIndex) {
            Log.d(TAG, "Creating a new expandedPlaylistFragment AFTER the settings tab was created. Moving settings tab to back");
            moveSettingsTabToBack();
        }
        return true;
    }


    //Create settings tab
    void setupSettingsTab() {
        settingsFragment = new SettingsFragment();
        fragmentList.add(settingsFragment);
        fragmentTitleList.add("Settings");
        settingsIndex = fragmentList.size() - 1;
    }

    //Sets the settings tab as the back tab (in case tabs get added after settings)
    private void moveSettingsTabToBack() {
        //So we can add it back later
        Fragment tmp = fragmentList.get(settingsIndex);

        //Shift tabs over to the left
        for(int i = settingsIndex; i < fragmentList.size(); i++) {
            fragmentList.add(i, fragmentList.get(i + 1));
        }
        //Set to last index
        settingsIndex = fragmentList.size() - 1;

        //Set settings back as last tab
        fragmentList.add(settingsIndex, tmp);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}