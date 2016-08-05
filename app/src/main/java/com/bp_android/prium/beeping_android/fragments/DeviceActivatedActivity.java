package com.bp_android.prium.beeping_android.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import utils.PrefManager;

public class DeviceActivatedActivity extends AppCompatActivity {

    Button continueZen;
    private Toolbar toolbar;
    private Device device;
    public static final int QR_REQUEST_KEY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_activated);

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

        new IntentIntegrator(this).initiateScan();
        Log.i("Start", "start scan");

        continueZen = (Button) findViewById(R.id.continueButtonZen);

        continueZen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ON CLICK", "Going to next");
                try {
                    Intent add_zen = new Intent(DeviceActivatedActivity.this, AddBeepingZenActivity.class);
                    add_zen.putExtra("DEVICE_KEY", device);
                    startActivityForResult(add_zen, 22);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(DeviceActivatedActivity.this, "Add device before moving further please", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 22) {
            if (resultCode == RESULT_OK && data != null) {
                device = (Device) data.getSerializableExtra("DEVICE_KEY");
                Log.i("activity air", "onresult");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      /*  if (requestCode == QR_REQUEST_KEY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                new UserLoginTask(data.getStringExtra("SERIAL_NUMBER")).execute();
            }
        }*/
        if (requestCode == 22) {

        } else {
            Log.i("OnResult", requestCode + "");
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    new UserLoginTask(result.getContents()).execute();
                }
            } else {
                new UserLoginTask(data.getStringExtra("SERIAL_NUMBER")).execute();
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mSerial_number;
        String serverUrl = "http://beepingsscb.devprium.com";
        private ProgressDialog progressDialog;

        UserLoginTask(String serial_number) {
            mSerial_number = serial_number;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DeviceActivatedActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performCodeCall(mSerial_number);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.i("RESULT", success + "");
            if (success) {
                Toast.makeText(getBaseContext(), "Device added successfully", Toast.LENGTH_SHORT).show();
                Log.i("TEST", device.getId() + "");
            } else {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean performCodeCall(String serial_number) {
            URL url;
            String requestURL = serverUrl + "/api/devices?access_token=" + PrefManager.getInstance(DeviceActivatedActivity.this).getToken() + "&serial_number=" + serial_number;
            Log.e("Add device", requestURL.toString());
            boolean result = false;
            try {
                Log.i("SERIAL NUMBER", serial_number);
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                Log.i("RESPONSE CODE", responseCode + "");
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = "";
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i("RESPONSE", response);
                    JSONObject tokenJSON = new JSONObject(response).getJSONObject("data");
                    device = new Device(tokenJSON.getInt("id"), serial_number);
                    PrefManager.getInstance(DeviceActivatedActivity.this).addZenDevice(device);
                    for (Device device1 : PrefManager.getInstance(DeviceActivatedActivity.this).getZenDevices()) {
                        Log.i("Z DEV", device1.getId() + "");
                    }

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

