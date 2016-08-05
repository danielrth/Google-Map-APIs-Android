package com.bp_android.prium.beeping_android.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.MapsActivity;
import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.ZenMapActivity;
import com.bp_android.prium.beeping_android.adapters.CustomGridViewAdapter;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.Item;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.PrefManager;

/**
 * Created by Vaibhav on 2/24/16.
 */
public class BeepingListActivityZen extends AppCompatActivity {

    GridView gridView;
    // ArrayList<Item> gridArray = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter;
    private Device device;
    Toolbar toolbar;

    public BeepingListActivityZen() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeping_list_zen);

        // device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");
        // ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);
        //set grid view item
       /* Bitmap homeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.comm_announce_auto);
        Bitmap userIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cles_qashqai);
        Bitmap defaultIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.vehicules);
        Bitmap newIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.btn_ajouter_beeping);
        gridArray.add(new Item(homeIcon, "ZEN"));
        gridArray.add(new Item(defaultIcon, "Other"));
        gridArray.add(new Item(homeIcon, "ZEN"));
        gridArray.add(new Item(homeIcon, "ZEN"));
        gridArray.add(new Item(homeIcon, "ZEN"));
        gridArray.add(new Item(newIcon, "Add"));*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.nav_drawer_vert));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        toolbar.setBackgroundColor(getResources().getColor(R.color.black_transparent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black_transparent));
        }

        gridView = (GridView) findViewById(R.id.gridView1_zen);
        try {
            Log.i("DEVICES LIST", "OnCreateView");
            final List<Device> devices = PrefManager.getInstance(this).getZenDevices();
            Log.i("SIZE before", devices.size() + "");
            devices.add(new Device("Add more", -2));
            Log.i("SIZE after", devices.size() + "");
            customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, devices);
            gridView.setAdapter(customGridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                                                    if (customGridAdapter.getCount() - 1 == position) {
                                                    /*    Fragment fragment = new MyBeepingsFragment();
                                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.fragmentLayout, fragment);
                                                        fragmentTransaction.addToBackStack(null);
                                                        fragmentTransaction.commit();*/
                                                        Intent myBeeping = new Intent(BeepingListActivityZen.this, MyBeepingsFragment.class);
                                                        startActivity(myBeeping);
                                                    } else {
                                                        Toast.makeText(BeepingListActivityZen.this, devices.get(position).getName(), Toast.LENGTH_SHORT).show();
                                                        Intent zen = new Intent(BeepingListActivityZen.this, BeepingZENactivity.class);
                                                        zen.putExtra("DEVICE_KEY", customGridAdapter.getItem(position));
                                                        startActivity(zen);
                                                    }
                                                }
                                            }
            );
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(BeepingListActivityZen.this, "No devices", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIST", "onResume");
        customGridAdapter.clear(); // i got null list error on this line.
        final List<Device> devices = PrefManager.getInstance(BeepingListActivityZen.this).getZenDevices();
        devices.add(new Device("Add more", -2));
        customGridAdapter.addAll(devices);
        customGridAdapter.notifyDataSetChanged();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        String serverUrl = "http://beepingsscb.devprium.com";

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performDeviceCall();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {


            } else {
                Toast.makeText(BeepingListActivityZen.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean performDeviceCall() {
            URL url;
            String requestURL = serverUrl + "/api/devices/" + device.getId() +
                    "?access_token=" + PrefManager.getInstance(BeepingListActivityZen.this).getToken();
            Log.e("GET DEVICE", requestURL.toString());
            boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                int responseCode = conn.getResponseCode();
                Log.i("UPD RESPONSE CODE", responseCode + "");
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = "";
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i("UPDATE RESPONSE", response);
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
            return result;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_settings);
        MenuItem edit = menu.add("Edit");
        edit.setIcon(R.drawable.nav_beepings_zen_carte);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MENU", "selected" + item);

        Toast.makeText(this, "Back selected", Toast.LENGTH_SHORT)
                .show();

        try {

            if (item.getTitle().equals("Edit")) {
                Log.i("MENU", "Edit");
                Toast.makeText(this, "Edit selected", Toast.LENGTH_SHORT)
                        .show();
                Intent ed_zen = new Intent(this, ZenMapActivity.class);
                ed_zen.putExtra("DEVICE_KEY", device);
                startActivityForResult(ed_zen, 23);
                // startActivity(ed);
            }
        } catch (NullPointerException e) {
        }
        onBackPressed();
        return true;
    }
}
