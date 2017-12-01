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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by xflip on 11/14/2017.
 */

public class SettingsFragment extends ListFragment {
    private static final String TAG = "SettingsFragment";
    private static ArrayList<String> settingNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "Creating SettingsFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listfragment, container, false);

        settingNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settingNames);
        setListAdapter(adapter);

        setRetainInstance(true);

        addSettings();

        return rootView;
    }

    private static void addSettings() {
        settingNames.add("Toggle on/off album art");
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