package com.bp_android.prium.beeping_android.adapters;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Vaibhav on 3/2/16.
 */
public class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
