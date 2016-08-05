package com.bp_android.prium.beeping_android.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.MapsActivity;
import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.RouteDirectionActivity;
import com.bp_android.prium.beeping_android.model.Device;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import utils.PrefManager;

public class BeepingZENactivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;

    private ImageView img_back;
    private Device device;
    private TextView text_main, text_map1, text_click;
    private ImageButton info, img_perdu, img_circle, add_photo;
    private String currentAddress;
    private LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeping_zenactivity);

        device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");

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

        text_main = (TextView) findViewById(R.id.name_zen_device);
        text_map1 = (TextView) findViewById(R.id.txt_map1_zen);
        text_main.setText(device.getName());

        text_click = (TextView) findViewById(R.id.txt_click);
        add_photo = (ImageButton) findViewById(R.id.btn_add_photo_air);
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add_photo.setVisibility(View.GONE);
                text_click.setVisibility(View.GONE);
                Intent click = new Intent(BeepingZENactivity.this, AddBeepingZenActivity.class);
                click.putExtra("DEVICE_KEY", device);
                startActivityForResult(click, 23);
            }
        });

        img_back = (ImageView) findViewById(R.id.imageView_back_zen);

        img_perdu = (ImageButton) findViewById(R.id.image_perdu_zen);

        img_perdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_perdu.setBackgroundResource(R.drawable.btn_fiche_zen_parcours_vert);
                Intent map = new Intent(BeepingZENactivity.this, MapsActivity.class);
                //startActivity(map);
                map.putExtra("DEVICE_KEY", device);
                startActivityForResult(map, 25);
            }
        });

        img_circle = (ImageButton) findViewById(R.id.image_route_zen);

        img_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_circle.setBackgroundResource(R.drawable.btn_fiche_zen_perimetre_vert);
                Intent map1 = new Intent(BeepingZENactivity.this, RouteDirectionActivity.class);
                //startActivity(map);
                map1.putExtra("DEVICE_KEY", device);
                startActivityForResult(map1, 24);
            }
        });

        info = (ImageButton) findViewById(R.id.quantityActivityReportDetail_zen);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent map = new Intent(BeepingZENactivity.this, InformationActivity.class);
                startActivity(map);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("activity ZEN", "on resume");
        if (device.getPicture() != -1) {

            add_photo.setVisibility(View.GONE);
            text_click.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage("http://beepingsscb.devprium.com/api/pictures/" + device.getPicture() +
                    "?type=original&access_token=" + PrefManager.getInstance(this).getToken(), img_back);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 23) {
            if (resultCode == RESULT_OK && data != null) {
                device = (Device) data.getSerializableExtra("DEVICE_KEY");
                Log.i("activity zen", "onresult");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_settings);
        MenuItem edit = menu.add("Edit");
        edit.setIcon(R.drawable.nav_modif_vert);
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
                Intent ed_zen = new Intent(this, BeepingZenEditActivity.class);
                ed_zen.putExtra("DEVICE_KEY", device);
                startActivityForResult(ed_zen, 23);
                // startActivity(ed);
            }
        } catch (NullPointerException e) {
        }
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

/*        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(48.857502, 2.2735993);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_geo_beeping_petit_vert)));

        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.857502, 2.2735993), 14.0f));*/

        new PositionsTask().execute();
    }

    private Bitmap getMarkerBitmapFromView(Bitmap bmp) {

        View view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) view.findViewById(R.id.profile_image);
        markerImageView.setImageBitmap(bmp);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;

    }

    public class PositionsTask extends AsyncTask<Void, Void, Boolean> {

        String serverUrl = "http://beepingsscb.devprium.com/api/";
        Bitmap bmp;

        @Override
        protected Boolean doInBackground(Void... params) {

            return getPositions();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                MarkerOptions marker = new MarkerOptions().position(position).title(device.getName());
                if (bmp != null) {
                    //marker.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                    marker.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(bmp)));
                } else {
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_geo_beeping_petit_vert));
                }
                mMap.addMarker(marker);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.0f));
                text_map1.setText(currentAddress);
            } else {
                Toast.makeText(getBaseContext(), "No position", Toast.LENGTH_SHORT).show();

            }
        }

        public boolean getPositions() {
            URL url;
            String requestURL = serverUrl + "devices/" + device.getId() + "?access_token="
                    + PrefManager.getInstance(BeepingZENactivity.this).getToken();
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
                        bmp = ImageLoader.getInstance().loadImageSync("http://beepingsscb.devprium.com/api/pictures/" + device.getPicture() +
                                "?type=thumbnail&access_token=" + PrefManager.getInstance(BeepingZENactivity.this).getToken());
                        Log.i("BMP URL", "http://beepingsscb.devprium.com/api/pictures/" + device.getPicture() +
                                "?type=thumbnail&access_token=" + PrefManager.getInstance(BeepingZENactivity.this).getToken());
                        Log.i("BMP", bmp.getHeight() + "");
                    }
                    else
                    {

                    }
                    currentAddress = json.getString("current_address");
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

}

