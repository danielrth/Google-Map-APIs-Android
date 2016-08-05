package com.bp_android.prium.beeping_android.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.R;

/**
 * Created by Vaibhav on 2/17/16.
 */
public class NotificationFragment extends Fragment {

    public NotificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);

        //Get widgets reference from XML layout
        final TextView tView = (TextView) view.findViewById(R.id.tv);
        SwitchCompat sButton = (SwitchCompat) view.findViewById(R.id.switch_btn);

        final TextView tView1 = (TextView) view.findViewById(R.id.tv1);
        SwitchCompat sButton1 = (SwitchCompat) view.findViewById(R.id.switch_btn1);

  /*      //Set a CheckedChange Listener for Switch Button
        sButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    //Do something when Switch button is on/checked
                    tView1.setText("Switch is on.....");
                } else {
                    //Do something when Switch is off/unchecked
                    tView1.setText("Switch is off.....");
                }
            }
        });*/
        return view;
    }
}

