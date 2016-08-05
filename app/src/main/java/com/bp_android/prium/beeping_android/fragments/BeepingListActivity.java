package com.bp_android.prium.beeping_android.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.content.Intent;

import com.bp_android.prium.beeping_android.LoginActivity;
import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.MapsActivity;
import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.adapters.CustomGridViewAdapter;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.Item;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.PrefManager;

public class BeepingListActivity extends Fragment {

    GridView gridView;
    CustomGridViewAdapter customGridAdapter;
    private Device device;

    public BeepingListActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_beeping_list, container, false);

        //  device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");
        ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);
        //set grid view item

        gridView = (GridView) view.findViewById(R.id.gridView1);
        try {

            final List<Device> devices = PrefManager.getInstance(getActivity()).getAirDevices();
            Log.i("SIZE before", devices.size() + "");
            devices.add(new Device("Add more", -2));

            customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, devices);
            gridView.setAdapter(customGridAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    if (customGridAdapter.getCount() - 1 == position) {
                        Intent intent = new Intent(getActivity(), BluetoothActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), devices.get(position).getName(), Toast.LENGTH_SHORT).show();
                        Intent air = new Intent(getActivity(), BeepingAIRactivity.class);
                        air.putExtra("DEVICE_KEY", customGridAdapter.getItem(position));
                        startActivity(air);
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "No devices", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LIST", "onResume");
        customGridAdapter.clear();
        final List<Device> devices = PrefManager.getInstance(getActivity()).getAirDevices();
        devices.add(new Device("Add more", -2));
        customGridAdapter.addAll(devices);
        customGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_settings);
        MenuItem edit = menu.add("Edit");
        edit.setIcon(R.drawable.nav_beepings_zen_list);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MENU", "selected" + item);

        try {

            if (item.getTitle().equals("Edit")) {
                Log.i("MENU", "Edit");;
                Intent ed = new Intent(getActivity(), MapsActivity.class);
                ed.putExtra("DEVICE_KEY", device);
                startActivityForResult(ed, 22);
            }
        } catch (NullPointerException e) {
        }

       // onBackPressed();
        return true;
    }

   public class DeviceUpdateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAlert_mode;
        private final String mLost_mode;
        private final String mName;
        private final String mPosition;
        String serverUrl = "http://beepingsscb.devprium.com";

       DeviceUpdateTask(String alert_mode, String lost_mode, String name, String position) {

            mAlert_mode = alert_mode;
            mLost_mode = lost_mode;
            mName = name;
            mPosition = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return deviceUpdateCall(mAlert_mode, mLost_mode, mName, mPosition);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                PrefManager manager = PrefManager.getInstance(getActivity());
                manager.setFirstNameKey(mAlert_mode);
                manager.setLastNameKey(mLost_mode);
                manager.setBirthdayKey(mName);
                manager.setMobileKey(mPosition);
                // onBackPressed();

            } else {
                Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean deviceUpdateCall(String alert_mode, String lost_mode, String name, String position) {
            URL url;
            String requestURL = serverUrl + "/api/users/" + device.getId() +
                    "?access_token=" + PrefManager.getInstance(getActivity()).getToken() +
                    "&alert_mode=" + alert_mode + "&lost_mode=" + lost_mode + "&name=" + name + "&position=" + position;
            Log.e("UPDATE ", requestURL.toString());
            boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("PUT");
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
}
