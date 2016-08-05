package com.bp_android.prium.beeping_android.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.R;

/**
 * Created by Vaibhav on 2/22/16.
 */
public class TestFragmentForNoZen extends Fragment {

    public TestFragmentForNoZen() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bluetooth_air, container, false);

        ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);

        return view;
    }
}