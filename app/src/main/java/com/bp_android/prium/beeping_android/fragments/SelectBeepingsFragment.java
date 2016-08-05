package com.bp_android.prium.beeping_android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.MapsActivity;
import com.bp_android.prium.beeping_android.R;

/**
 * Created by Vaibhav on 2/10/16.
 */
public class SelectBeepingsFragment extends Fragment {

    public SelectBeepingsFragment() {

    }

    RelativeLayout rel_air, rel_zen;
    TextView lin_air, lin_zen;
    View view_air, view_air1, view_zen, view_zen1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beeping_select, container, false);

        ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);

        rel_air = (RelativeLayout) view.findViewById(R.id.linear_layout_AIR);
        lin_air = (TextView) view.findViewById(R.id.text_air);
        view_air = (View) view.findViewById(R.id.circles_air_grey);
        view_air1 = (View) view.findViewById(R.id.circles_air_white);

        rel_zen = (RelativeLayout) view.findViewById(R.id.linear_layout_ZEN);
        lin_zen = (TextView) view.findViewById(R.id.text_zen);
        view_zen = (View) view.findViewById(R.id.circles_zen_grey);
        view_zen1 = (View) view.findViewById(R.id.circles_zen_white);

        lin_air.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new BeepingListActivity();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                lin_air.setTextColor(Color.WHITE);
                view_air1.setVisibility(View.VISIBLE);
                view_air.setVisibility(View.INVISIBLE);
            }
        });

        lin_zen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*                Fragment fragment = new BeepingListActivityZen();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                Intent myBeepingList = new Intent(getActivity(), BeepingListActivityZen.class);
                startActivity(myBeepingList);
                lin_zen.setTextColor(Color.WHITE);
                view_zen1.setVisibility(View.VISIBLE);
                view_zen.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
}
