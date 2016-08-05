package com.bp_android.prium.beeping_android.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
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
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.DeviceMarker;
import com.bp_android.prium.beeping_android.model.SignalActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.DialogInterface;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.JsonParser;
import utils.PrefManager;

public class BeepingAIRactivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;
    private ImageView img_back;
    TextView txt_map;

    private ImageButton img_bluetooth, img_map, info, image_rep1, img_sound;
    private Device device;
    BluetoothDevice mDevice;
    private String currentAddress;
    private LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeping_airactivity);

        //((MainActivity) getActivity()).setToolbarColor(Color.BLACK);
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

        txt_map = (TextView) findViewById(R.id.txt_map1);

        img_back = (ImageView) findViewById(R.id.imageView_back_air);

        img_sound = (ImageButton) findViewById(R.id.image_sound);

        img_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_sound.setBackgroundResource(R.drawable.btn_fiche_air_sonner_vert);
                Intent blue = new Intent(BeepingAIRactivity.this, SignalActivity.class);
                startActivity(blue);
            }
        });

        img_bluetooth = (ImageButton) findViewById(R.id.image_rep);


        img_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_bluetooth.setBackgroundResource(R.drawable.btn_fiche_air_recuperer_vert);
                Intent blue = new Intent(BeepingAIRactivity.this, SignalActivity.class);
                blue.putExtra(BluetoothActivity.EXTRA_DEVICE, mDevice);
                startActivity(blue);
            }
        });

        img_map = (ImageButton) findViewById(R.id.image_perdu);

        img_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_map.setBackgroundResource(R.drawable.btn_fiche_air_perdu_vert);
                Intent map = new Intent(BeepingAIRactivity.this, MapsActivity.class);
                startActivity(map);
            }
        });

        info = (ImageButton) findViewById(R.id.quantityActivityReportDetail);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent map = new Intent(BeepingAIRactivity.this, InformationActivity.class);
                startActivity(map);
            }
        });

        image_rep1 = (ImageButton) findViewById(R.id.image_rep1);

        image_rep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image_rep1.setBackgroundResource(R.drawable.btn_fiche_air_recuperer_vert);
                Intent map = new Intent(BeepingAIRactivity.this, InformationActivity.class);
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
        Log.i("activity AIR", "on resume");
        if (device.getPicture() != -1) {
            ImageLoader.getInstance().displayImage("http://beepingsscb.devprium.com/api/pictures/" + device.getPicture() +
                    "?type=original&access_token=" + PrefManager.getInstance(this).getToken(), img_back);
        }
    }

    public void open(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Beepings retrouvé ?");
        alertDialogBuilder.setMessage("Ce Beeping AIR a été déclaré perdu, l’avez vous retrouvé ?");

        alertDialogBuilder.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(BeepingAIRactivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 22) {
            if (resultCode == RESULT_OK && data != null) {
                device = (Device) data.getSerializableExtra("DEVICE_KEY");
                Log.i("activity air", "onresult");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                Intent ed = new Intent(this, BeepingAIReditActivity.class);
                ed.putExtra("DEVICE_KEY", device);
                startActivityForResult(ed, 22);
            }
        } catch (NullPointerException e) {
        }

        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
                txt_map.setText(currentAddress);

            } else {
                Toast.makeText(getBaseContext(), "No position", Toast.LENGTH_SHORT).show();

            }
        }

        public boolean getPositions() {
            URL url;
            String requestURL = serverUrl + "devices/" + device.getId() + "?access_token="
                    + PrefManager.getInstance(BeepingAIRactivity.this).getToken();
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
                                "?type=thumbnail&access_token=" + PrefManager.getInstance(BeepingAIRactivity.this).getToken());
                        Log.i("BMP URL", "http://beepingsscb.devprium.com/api/pictures/" + device.getPicture() +
                                "?type=thumbnail&access_token=" + PrefManager.getInstance(BeepingAIRactivity.this).getToken());
                        Log.i("BMP", bmp.getHeight() + "");
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

