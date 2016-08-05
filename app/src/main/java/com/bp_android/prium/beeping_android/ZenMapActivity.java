package com.bp_android.prium.beeping_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.DeviceMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.JsonParser;
import utils.PrefManager;

/**
 * Created by Vaibhav on 3/25/16.
 */
public class ZenMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final float LOCATION_REFRESH_DISTANCE = 10;
    private static final int LOCATION_REFRESH_TIME = 2000;
    private GoogleMap mMap;

    private Device device;
    LocationManager mLocationManager;
    private List<DeviceMarker> markers;
    PolylineOptions rectLine = new PolylineOptions().width(5).color(
            Color.RED);
    LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_direction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        myLocation = new LatLng(48.857502, 2.2735993);
        new PositionsTask().execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public class PositionsTask extends AsyncTask<Void, Void, Boolean> {


        String serverUrl = "http://beepingsscb.devprium.com/api/";


        @Override
        protected Boolean doInBackground(Void... params) {

            return getPositions();

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                DeviceMarker lastMarker = null;
                for (DeviceMarker marker : markers) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                            .title(marker.getTitle())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_geo_beeping_petit_vert)));
                    if (lastMarker != null)
                        mMap.addPolyline(new PolylineOptions().add(new LatLng(marker.getLatitude(), marker.getLongitude()), new LatLng(lastMarker.getLatitude(), lastMarker.getLongitude())).width(6).color(Color.BLUE).visible(true));
                    lastMarker = marker;
                }
                LatLng toMove = new LatLng(markers.get(0).getLatitude(), markers.get(0).getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toMove, 14.0f));

            } else {
                Toast.makeText(getBaseContext(), "No position", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean getPositions() {
            URL url;
            String requestURL = serverUrl + "devices/?access_token="
                    + PrefManager.getInstance(ZenMapActivity.this).getToken();
            Log.e("Sign in ", requestURL.toString());
            boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                Log.i("POS CODE", responseCode + "");
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = "";
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i("RESPONSE", response);

                    markers = JsonParser.parseMarkers(new JSONObject(response).getJSONArray("data"));

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 24) {
            if (resultCode == RESULT_OK && data != null) {
                device = (Device) data.getSerializableExtra("DEVICE_KEY");
                Log.i("activity zen", "onresult");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

