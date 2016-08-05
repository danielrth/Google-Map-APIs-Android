package com.bp_android.prium.beeping_android.temp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.DeviceMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.HttpUtils;
import utils.JsonParser;
import utils.PrefManager;
import utils.SafeJSONObject;

public class DirectionActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float LOCATION_REFRESH_DISTANCE = 10;
    private static final int LOCATION_REFRESH_TIME = 2000;
    private GoogleMap mMap;

    LocationManager mLocationManager;
    private List<DeviceMarker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

//        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(48.857502, 2.2735993);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_geo_beeping_petit_vert)));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.857502, 2.2735993), 14.0f));*/
        new PositionsTask().execute();

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
                    for (DeviceMarker marker : markers) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                                .title(marker.getTitle())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_geo_beeping_petit_vert)));
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
                    + PrefManager.getInstance(DirectionActivity.this).getToken() + "&zz=01";
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

}
