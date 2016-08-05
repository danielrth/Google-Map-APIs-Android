package com.bp_android.prium.beeping_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.fragments.BeepingZenEditActivity;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.DeviceMarker;
import com.bp_android.prium.beeping_android.temp.Circle;
import com.bp_android.prium.beeping_android.temp.CircleParams;
import com.bp_android.prium.beeping_android.temp.SegmentParams;
import com.bp_android.prium.beeping_android.temp.SegmentPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.DatabaseManager;
import utils.HttpUtils;
import utils.JsonParser;
import utils.PrefManager;
import utils.SafeJSONArray;
import utils.SafeJSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final float LOCATION_REFRESH_DISTANCE = 10;
    private static final int LOCATION_REFRESH_TIME = 2000;
    private GoogleMap mMap;
    private EditText mSearchPlace;
    private ListView mListViewPlaces;
    private PlaceAdapter mPlacesAdapter = null;
    private FrameLayout mMapCoverView;
    private Button mButtonFreeShape;
    private Button mButtonCircle;
    private TextView mTvRadius;
    private ImageView mCircleEndMarker;
    private ImageView mCircleCentrMarker;

    private Device device;
    LocationManager mLocationManager;
    private List<DeviceMarker> deviceMarkers;
    ArrayList<LatLng> shapePoints = new ArrayList<>();
    float circleRadius = 0;
    LatLngBounds mapBounds = null;
    private LatLng position;
    Point centerPositionPixel;

    boolean[] shapeTypeSelected = new boolean[]{false, false};
    boolean callGetAPI = true;
    SafeJSONArray searchedPlacesResult = null;
    DatabaseManager mDBManager;
    SafeJSONObject mLastShape;

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

        device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mDBManager = new DatabaseManager(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        mSearchPlace = (EditText) findViewById(R.id.search_place);
        mListViewPlaces = (ListView) findViewById(R.id.listviewPlaces);
        mPlacesAdapter = new PlaceAdapter(this);
        mListViewPlaces.setAdapter(mPlacesAdapter);
        mListViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListViewPlaces.setVisibility(View.INVISIBLE);
                SafeJSONObject geoRange = searchedPlacesResult.getJSONObject(position).getJSONObject("geometry");
                mapBounds = new LatLngBounds(
                        new LatLng(geoRange.getJSONObject("viewport").getJSONObject("southwest").getDouble("lat"),
                                geoRange.getJSONObject("viewport").getJSONObject("southwest").getDouble("lng")),
                        new LatLng(geoRange.getJSONObject("viewport").getJSONObject("northeast").getDouble("lat"),
                                geoRange.getJSONObject("viewport").getJSONObject("northeast").getDouble("lng")));

                if (mapBounds != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 0));
            }
        });

        mTvRadius = (TextView) findViewById(R.id.tvRadius);
        mButtonFreeShape = ((Button) findViewById(R.id.button_free_shape));
        mButtonFreeShape.setSelected(false);
        mButtonCircle = ((Button) findViewById(R.id.button_circle));
        mButtonCircle.setSelected(false);

        mMapCoverView = (FrameLayout) findViewById(R.id.map_cover_view);
        mMapCoverView.setVisibility(View.INVISIBLE);
        initDrawer();
    }

    @Override
    public void onPause() {
        if (mMap != null)
            mMap.clear();
        if (shapePoints != null)
            shapePoints.clear();
        super.onPause();
    }

    private void initDrawer() {
        mCircleEndMarker = (ImageView) findViewById(R.id.circle_end_marker);
        mCircleCentrMarker = (ImageView) findViewById(R.id.circle_center_marker);

        mMapCoverView.setClickable(true);
        mMapCoverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float posX = event.getX();
                float posY = event.getY();
                LatLng posLatLng = mMap.getProjection().fromScreenLocation(new Point((int) posX, (int) posY));

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        shapePoints.clear();
                        shapePoints.add(posLatLng);

                        mCircleCentrMarker.setVisibility(View.VISIBLE);
                        mCircleCentrMarker.setX(posX - mCircleCentrMarker.getWidth() / 2);
                        mCircleCentrMarker.setY(posY - mCircleCentrMarker.getHeight() / 2);

                        centerPositionPixel = new Point((int)posX, (int)posY);
                        mTvRadius.setVisibility(View.VISIBLE);
                        if (shapeTypeSelected[0]) {
                            mTvRadius.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        shapePoints.add(posLatLng);
                        int lastIndex = shapePoints.size() - 1;
                        if (shapeTypeSelected[0]) {
                            PolylineOptions line = new PolylineOptions().add(shapePoints.get(lastIndex - 1), shapePoints.get(lastIndex)).width(15).color(Color.argb(255,80,201,181)).visible(true);
                            mMap.addPolyline(line);
                        } else if (shapeTypeSelected[1]) {
                            mMap.clear();
                            PolylineOptions line = new PolylineOptions().add(shapePoints.get(0), shapePoints.get(lastIndex)).width(15).color(Color.WHITE).visible(true);
                            mMap.addPolyline(line);

                            Location centerPos = new Location("");
                            centerPos.setLatitude(shapePoints.get(0).latitude);
                            centerPos.setLongitude(shapePoints.get(0).longitude);
                            Location edgePos = new Location("");
                            edgePos.setLatitude(posLatLng.latitude);
                            edgePos.setLongitude(posLatLng.longitude);
                            circleRadius = centerPos.distanceTo(edgePos);

                            CircleOptions circle = new CircleOptions();
                            circle.center(shapePoints.get(0)).fillColor(Color.argb(30, 0, 0, 0)).radius(circleRadius);
                            circle.strokeColor(Color.WHITE).strokeWidth(10);
                            mMap.addCircle(circle);

                            int radiusPixel = (int)Math.sqrt((posX - centerPositionPixel.x) * (posX - centerPositionPixel.x) + (posY - centerPositionPixel.y) * (posY - centerPositionPixel.y));
                            mTvRadius.setText(String.valueOf(((float)circleRadius) / 1000.0) + "km");
                            mTvRadius.setX(centerPositionPixel.x);
                            mTvRadius.setY(centerPositionPixel.y + radiusPixel);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (shapePoints.size() > 0){
                            mCircleEndMarker.setVisibility(View.VISIBLE);
                            mCircleEndMarker.setX(posX - mCircleEndMarker.getWidth() / 2);
                            mCircleEndMarker.setY(posY - mCircleEndMarker.getHeight() / 2);
                            String strShapePoints = "";
                            if (shapeTypeSelected[0]) {
                                for (int i = 0; i < shapePoints.size(); i++)
                                    strShapePoints += String.valueOf(shapePoints.get(i).latitude) + ":" + String.valueOf(shapePoints.get(i).longitude) + "-";
                                mDBManager.updateSetting(String.valueOf(device.getId()), "FreeShape", strShapePoints);
                            }
                            else if (shapeTypeSelected[1]){
                                strShapePoints += String.valueOf(shapePoints.get(0).latitude) + ":" + String.valueOf(shapePoints.get(0).longitude) + "-" + String.valueOf(circleRadius);
                                mDBManager.updateSetting(String.valueOf(device.getId()), "Circle", strShapePoints);
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void drawLastShape(SafeJSONObject lastShape) {
        if (lastShape == null) return;

        if (lastShape.getString("shape_type").equals("Circle")) {
            String[] strShapePoints = lastShape.getString("shape_points").split("-");

            LatLng centerPos = new LatLng(Double.valueOf(strShapePoints[0].split(":")[0]), Double.valueOf(strShapePoints[0].split(":")[1]));
            circleRadius = Float.valueOf(strShapePoints[1]);

//            mMap.addMarker(new MarkerOptions().position(centerPos)
//                    .title("")
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_center_white_marker1)));

            CircleOptions circle = new CircleOptions();
            circle.center(centerPos).radius(circleRadius);
            circle.strokeColor(Color.argb(255, 80, 201, 181)).strokeWidth(10);
            mMap.addCircle(circle);

//            int radiusPixel = (int)Math.sqrt((posX - centerPositionPixel.x) * (posX - centerPositionPixel.x) + (posY - centerPositionPixel.y) * (posY - centerPositionPixel.y));
//            mTvRadius.setText(String.valueOf(Math.round(circleRadius / 1000)) + "km");
//            mTvRadius.setX(centerPositionPixel.x);
//            mTvRadius.setY(centerPositionPixel.y + radiusPixel);
        }
        else {
            String[] strShapePoints = lastShape.getString("shape_points").split("-");
            for (int i = 0; i < strShapePoints.length; i++) {
                String[] stringValues = strShapePoints[i].split(":");
                LatLng ll1 = new LatLng(Double.valueOf(strShapePoints[i].split(":")[0]), Double.valueOf(strShapePoints[i].split(":")[1]));
                LatLng ll2 = new LatLng(Double.valueOf(strShapePoints[(i+1) % strShapePoints.length].split(":")[0]), Double.valueOf(strShapePoints[(i+1) % strShapePoints.length].split(":")[1]));

                PolylineOptions line = new PolylineOptions().add(ll1, ll2).width(15).color(Color.argb(255, 80, 201, 181)).visible(true);
                mMap.addPolyline(line);

            }

//            mMap.addMarker(new MarkerOptions().
//                    position(new LatLng(Double.valueOf(strShapePoints[0].split(":")[0]), Double.valueOf(strShapePoints[0].split(":")[1])))
//                    .title("")
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_center_white_marker1)));
//            mMap.addMarker(new MarkerOptions().
//                    position(new LatLng(Double.valueOf(strShapePoints[strShapePoints.length - 1].split(":")[0]), Double.valueOf(strShapePoints[strShapePoints.length - 1].split(":")[1])))
//                    .title("")
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_end_marker1)));
        }
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

    public void onSearchButtonClick(View view) {
        String strPlace = mSearchPlace.getText().toString();
        if (strPlace.equals(""))    return;
        mListViewPlaces.setVisibility(View.VISIBLE);
        SearchPlaces(strPlace);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(mSearchPlace.getWindowToken(), 0);
        }
    }

    public void onButtonDrawFreeShapeClick(View view) {
        shapePoints.clear();
        mMap.clear();
        if (shapeTypeSelected[0]) {

            shapeTypeSelected[0] = false;
            mButtonFreeShape.setSelected(false);
            if (!shapeTypeSelected[1])
                mMapCoverView.setVisibility(View.INVISIBLE);
        } else {
            shapeTypeSelected[0] = true;
            mButtonFreeShape.setSelected(true);
            shapeTypeSelected[1] = false;
            mButtonCircle.setSelected(false);
            mMapCoverView.setVisibility(View.VISIBLE);
            mTvRadius.setVisibility(View.INVISIBLE);
        }
    }

    public void onButtonDrawCircleClick(View view) {
        shapePoints.clear();
        mMap.clear();
        if (shapeTypeSelected[1]) {
            shapeTypeSelected[1] = false;
            mButtonCircle.setSelected(false);
            if (!shapeTypeSelected[0])
                mMapCoverView.setVisibility(View.INVISIBLE);
        } else {
            shapeTypeSelected[1] = true;
            mButtonCircle.setSelected(true);
            shapeTypeSelected[0] = false;
            mButtonFreeShape.setSelected(false);
            mMapCoverView.setVisibility(View.VISIBLE);
//            mTvRadius.setVisibility(View.VISIBLE);

        }
    }

    public void onButtonCancelClick(View view) {
        shapePoints.clear();
        mMap.clear();
    }

    public void onButtonSendClick(View view) {
        if (shapeTypeSelected[1]) {
            mMap.clear();
            CircleOptions circle = new CircleOptions();
            circle.center(shapePoints.get(0)).strokeColor(Color.argb(255, 80, 201, 181)).strokeWidth(10).radius(circleRadius);
            mMap.addCircle(circle);
        }
        else if (shapeTypeSelected[0]) {
            int lastIndex = shapePoints.size() - 1;
            PolylineOptions line = new PolylineOptions().add(shapePoints.get(lastIndex), shapePoints.get(0)).width(15).color(Color.argb(255,80,201,181)).visible(true);
            mMap.addPolyline(line);
        }

        mCircleEndMarker.setVisibility(View.INVISIBLE);
        mCircleCentrMarker.setVisibility(View.INVISIBLE);
        mTvRadius.setVisibility(View.INVISIBLE);

        new PositionsTask().execute();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new PositionsTask().execute();

        mLastShape = mDBManager.getShape(String.valueOf(device.getId()));
        if (mLastShape != null)
            drawLastShape(mLastShape);
    }

    public void SearchPlaces(String query) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());

        try {
            String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(query, "utf-8");
            String response = HttpUtils.HttpGetWithEntity(urlStr);
            try {
                SafeJSONObject result = new SafeJSONObject(response);
                searchedPlacesResult = result.getJSONArray("results");
                if (searchedPlacesResult != null)
                    mPlacesAdapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent zen = new Intent(this, BeepingZenEditActivity.class);
        startActivity(zen);
        return false;
    }

    public class PositionsTask extends AsyncTask<Void, Void, Boolean> {


        String serverUrl = "http://beepingsscb.devprium.com/api/";


        @Override
        protected Boolean doInBackground(Void... params) {

            if (callGetAPI) {
                return getPositions();
            } else if (shapeTypeSelected[0])
                try {
                    return postShape();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            else
                try {
                    return postCircle();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (callGetAPI) {
                if (success) {
                    for (DeviceMarker marker : deviceMarkers) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                                .title(marker.getTitle())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_geo_beeping_petit_vert)));
                    }
                    LatLng toMove = new LatLng(deviceMarkers.get(0).getLatitude(), deviceMarkers.get(0).getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toMove, 14.0f));

                } else {
                    Toast.makeText(getBaseContext(), "No position", Toast.LENGTH_SHORT).show();
                }
                callGetAPI = false;
            }
        }

        public boolean getPositions() {
            URL url;
            String requestURL = serverUrl + "devices/?access_token="
                    + PrefManager.getInstance(MapsActivity.this).getToken() + "&zz=02";
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

                    deviceMarkers = JsonParser.parseMarkers(new JSONObject(response).getJSONArray("data"));

                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }

        public boolean postCircle() throws JSONException {
            /*URL url;
            String requestURL = serverUrl + "devices/" + device.getId();
            Log.e("Add zone to device ", requestURL.toString());

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("access_token", PrefManager.getInstance(MapsActivity.this).getToken());
            JSONObject jsonPoints = new JSONObject();
            jsonPoints.put("circle_center_lat", shapePoints.get(0).latitude);
            jsonPoints.put("circle_center_lng", shapePoints.get(0).longitude);
            jsonPoints.put("circle_radius", circleRadius);
            jsonParams.put("last_zone_area_attributes", jsonPoints);
            Log.i("CIRCLE", jsonParams.toString());*/

            /*JSONObject jsonPoints = new JSONObject();
            jsonPoints.put("circle_center_lat", shapePoints.get(0).latitude);
            jsonPoints.put("circle_center_lng", shapePoints.get(0).longitude);
            jsonPoints.put("circle_radius", circleRadius);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPatch httppost = new HttpPatch(serverUrl + "devices/" + device.getId());

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("access_token", PrefManager.getInstance(MapsActivity.this).getToken()));
                nameValuePairs.add(new BasicNameValuePair("last_zone_area_attributes", jsonPoints.toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                Log.i("CIRCLE CODE", response.getStatusLine().getStatusCode() + "");
                Log.i("CIRCLE PATCH", response.getEntity().toString());
                return true;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            String postUrl = serverUrl + "devices/" + device.getId();
            Gson gson = new Gson();
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPatch request = new HttpPatch(postUrl);
            CircleParams params = new CircleParams(PrefManager.getInstance(MapsActivity.this).getToken(),
                    new Circle(shapePoints.get(0).latitude, shapePoints.get(0).longitude, circleRadius));

            System.out.println(gson.toJson(params));
            StringEntity postingString = null;
            try {
                postingString = new StringEntity(gson.toJson(params));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.setEntity(postingString);
            request.setHeader("Content-type", "application/json");

            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("CIRCLE CODE", response.getStatusLine().getStatusCode() + "");
            Log.i("CIRCLE RESPONSE", response.getEntity().toString());

            return false;

            /*boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json");

                DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                printout.writeBytes(URLEncoder.encode(jsonParams.toString(), "UTF-8"));
                printout.flush();
                printout.close();

                int responseCode = conn.getResponseCode();
                Log.i("RESPONS CODE", responseCode + "");
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            return result;*/
        }

        public boolean postShape() throws JSONException {
            /*URL url;
            String requestURL = serverUrl + "zone_areas";
            Log.e("Add zone to device ", requestURL.toString());

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("access_token", PrefManager.getInstance(MapsActivity.this).getToken());
            jsonParams.put("device_id", device.getId());
            JSONArray jsonPointsArray = new JSONArray();
            for (int i = 0; i < shapePoints.size(); i++) {
                JSONObject jsonPoints = new JSONObject();
                jsonPoints.put("order", i + 1);
                int lastIndex = i == 0 ? 0 : i - 1;
                jsonPoints.put("start_lat", shapePoints.get(lastIndex).latitude);
                jsonPoints.put("start_lng", shapePoints.get(lastIndex).longitude);
                jsonPoints.put("end_lat", shapePoints.get(i).latitude);
                jsonPoints.put("end_lng", shapePoints.get(i).longitude);
                jsonPointsArray.put(jsonPoints);
            }
            jsonParams.put("itinerary_segments_attributes", jsonPointsArray);
            Log.i("SEGMENT", jsonParams.toString());*/

            String postUrl = serverUrl + "zone_areas";
            Gson gson = new Gson();
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(postUrl);
            SegmentParams params = new SegmentParams();
            params.access_token = PrefManager.getInstance(MapsActivity.this).getToken();
            params.device_id = device.getId();
            List<SegmentPosition> positions = new ArrayList<>();
            for (int i = 0; i < shapePoints.size(); i++) {
                int lastIndex = i == 0 ? 0 : i - 1;
                positions.add(new SegmentPosition(i + 1, shapePoints.get(lastIndex).latitude, shapePoints.get(lastIndex).longitude,
                        shapePoints.get(i).latitude, shapePoints.get(i).longitude));
            }
            params.itinerary_segments_attributes = positions;

            System.out.println(gson.toJson(params));
            StringEntity postingString = null;
            try {
                postingString = new StringEntity(gson.toJson(params));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.setEntity(postingString);
            request.setHeader("Content-type", "application/json");

            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("ZONE CODE", response.getStatusLine().getStatusCode() + "");
            Log.i("ZONE RESPONSE", response.getEntity().toString());

            /*boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
                DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
                printout.writeBytes(URLEncoder.encode(jsonParams.toString(), "UTF-8"));
                printout.flush();
                printout.close();

                int responseCode = conn.getResponseCode();
                Log.i("POS CODE", responseCode + "");
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
*/
            return false;
        }
    }

    public class PlaceAdapter extends BaseAdapter {
        private Context mContext;

        public PlaceAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return (searchedPlacesResult == null) ? 0 : searchedPlacesResult.length();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            PlaceItemHolder holder = null;
            if (v == null) {
                v = LayoutInflater.from(MapsActivity.this).inflate(R.layout.place_listview_item, null);
                holder = new PlaceItemHolder();

                v.setTag(holder);
            } else {
                holder = (PlaceItemHolder) v.getTag();
            }

            SafeJSONObject placeData = searchedPlacesResult.getJSONObject(position);
            holder.configure(v, placeData.getString("formatted_address"));

            return v;
        }
    }

    private class PlaceItemHolder {
        TextView textViewPlaceName;

        void configure(View v, String strPlaceName) {
            textViewPlaceName = (TextView) v.findViewById(R.id.tvPlaceName);
            textViewPlaceName.setText(strPlaceName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 25) {
            if (resultCode == RESULT_OK && data != null) {
                device = (Device) data.getSerializableExtra("DEVICE_KEY");
                Log.i("activity zen", "onresult");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public class PositionsTask1 extends AsyncTask<Void, Void, Boolean> {

        String serverUrl = "http://beepingsscb.devprium.com/api/";
        Bitmap bmp;

        @Override
        protected Boolean doInBackground(Void... params) {

            return getPositions1();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

            } else {
                Toast.makeText(getBaseContext(), "No position", Toast.LENGTH_SHORT).show();

            }
        }

        public boolean getPositions1() {
            URL url;
            String requestURL = serverUrl + "devices/" + device.getId() + "?access_token="
                    + PrefManager.getInstance(MapsActivity.this).getToken();
            Log.e("Sign in ", requestURL.toString());
            boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = "";
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i("RESPONSE", response);
                    JSONObject json = new JSONObject(response).getJSONObject("data").getJSONObject("attributes");
                    Log.i("img", device.getPicture() + "");
                    if (device.getPicture() != -1) {
                        Log.i("loading img", device.getPicture() + "");

                    } else {

                    }

                    position = new LatLng(json.getJSONObject("position").getDouble("latitude"), json.getJSONObject("position").getDouble("longitude"));

                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }

    }

    public void onButtonBackClick(View view) {
        finish();
    }
}
