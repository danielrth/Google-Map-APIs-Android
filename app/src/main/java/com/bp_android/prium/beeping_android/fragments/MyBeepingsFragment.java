package com.bp_android.prium.beeping_android.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.bp_android.prium.beeping_android.model.Device;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import utils.PrefManager;

/**
 * Created by Vaibhav on 2/9/16.
 */
public class MyBeepingsFragment extends AppCompatActivity {

    private Device device;
    public static final int QR_REQUEST_KEY = 2;
    private Toolbar toolbar;

    public MyBeepingsFragment() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_beeping);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.nav_drawer_vert));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        toolbar.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        //((MainActivity) getActivity()).setToolbarColor(Color.TRANSPARENT);
        Button add = (Button) findViewById(R.id.button_add_beeping);
        //Button list = (Button) view.findViewById(R.id.edit_btn_list_beeping);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Fragment fragment = new SelectBeepingsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                Intent qr = new Intent(MyBeepingsFragment.this, DeviceActivatedActivity.class);
                //qr.putExtra("DEVICE_KEY", device);
                startActivity(qr);
                //startActivityForResult(qr, 22);
            }
        });

    }

}
