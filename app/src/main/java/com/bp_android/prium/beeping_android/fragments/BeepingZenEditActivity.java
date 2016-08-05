package com.bp_android.prium.beeping_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.MultipartUtility;
import com.bp_android.prium.beeping_android.temp.Position;
import com.bp_android.prium.beeping_android.temp.UpdateParams;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import utils.LocationHelper;
import utils.PrefManager;

public class BeepingZenEditActivity extends AppCompatActivity {

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    Toolbar toolbar;
    Bitmap bm;
    AutoCompleteTextView edit_text_zen;

    private Device device;
    String charset = "UTF-8";
    String requestURL;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeping_zen_edit);

        device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");
        requestURL = "http://beepingsscb.devprium.com/api/devices/" + device.getId() + "/pictures?access_token="
                + PrefManager.getInstance(BeepingZenEditActivity.this).getToken();
        Log.i("PICTURE OUTPUT", requestURL); // here

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.nav_drawer_vert));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        toolbar.setBackgroundColor(getResources().getColor(R.color.dark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
        }

        edit_text_zen = (AutoCompleteTextView) findViewById(R.id.edit_text_zen);
        edit_text_zen.setText(device.getName());

        ImageButton camera = (ImageButton) findViewById(R.id.camera_btn_zen);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                Intent intent_camera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent_camera);*/
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.imageView_back_zen);
        if (device.getPicture() != -1) {
            ImageLoader.getInstance().displayImage("http://beepingsscb.devprium.com/api/pictures/" + device.getPicture() +
                    "?type=original&access_token=" + PrefManager.getInstance(this).getToken(), ivImage);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imagePath = destination.getAbsolutePath();
        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        imagePath = selectedImagePath;
        ivImage.setImageBitmap(bm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_settings);
        MenuItem edit = menu.add("Edit");
        edit.setIcon(R.drawable.nav_valid_vert);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MENU", "selected" + item);

        Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                .show();

        new UserPhotoTask().execute();
        return true;

    }

    public boolean performUpdateDeviceCall(String alert_mode, String lost_mode, String name, double latitude,
                                           double longitude) {
        try {
            String postUrl = "http://beepingsscb.devprium.com/api/devices/" + device.getId();
            Gson gson = new Gson();
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPatch request = new HttpPatch(postUrl);
            UpdateParams params = new UpdateParams();
            params.access_token = PrefManager.getInstance(BeepingZenEditActivity.this).getToken();
            params.alert_mode = alert_mode;
            params.lost_mode = lost_mode;
            params.name = name;
            params.bluetooth_uuid = "AAAA";
            params.last_position_attributes = new Position(latitude, longitude);

            System.out.println(gson.toJson(params));
            StringEntity postingString = new StringEntity(gson.toJson(params));
            request.setEntity(postingString);
            request.setHeader("Content-type", "application/json");

            HttpResponse response = httpClient.execute(request);
            Log.i("UPD CODE", response.getStatusLine().getStatusCode() + "");

            Log.i("UPDATE RESPONSE", response.getEntity().toString());
            device.setName(name);
            PrefManager.getInstance(getBaseContext()).updateZenDevice(device); // changed it to zen.
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

        public class UserPhotoTask extends AsyncTask<Void, Void, Boolean> {

        String serverUrl = "http://beepingsscb.devprium.com";

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Location location = LocationHelper.getLastKnownLocation(BeepingZenEditActivity.this);
            String position = null;
            try {
                position = new JSONObject().put("latitude", location.getLatitude()).put("longitude", location.getLongitude()).toString();
                // we have problem here i guess
            } catch (JSONException e) {
                e.printStackTrace();
            }
            performUpdateDeviceCall("false", "false", edit_text_zen.getText().toString(),
                    location.getLatitude(), location.getLongitude());
            return performUpdateCall();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Toast.makeText(getBaseContext(), "Refresh selected", Toast.LENGTH_SHORT)
                        .show();
                PrefManager.getInstance(BeepingZenEditActivity.this).updateZenDevice(device);
                onBackPressed();

            } else {
                Toast.makeText(getBaseContext(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean performUpdateCall() {
            try {
                if (TextUtils.isEmpty(imagePath)) {
                    return true;
                }
                MultipartUtility multipart = null;
                multipart = new MultipartUtility(requestURL, charset);
                multipart.addFilePart("photo", new File(imagePath));
                List<String> response = multipart.finish(); // response from server.
                Log.i("RESPONSE", response.toString());
                device.setPicture(new JSONArray(response.toString()).getJSONObject(0).getJSONObject("data").getInt("id"));
                Intent result = new Intent();
                result.putExtra("DEVICE_KEY", device);
                setResult(RESULT_OK, result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }
}
