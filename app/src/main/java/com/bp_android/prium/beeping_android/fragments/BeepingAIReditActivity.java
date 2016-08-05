package com.bp_android.prium.beeping_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.MultipartUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ByteArrayBody;
import com.parse.entity.mime.content.StringBody;
import com.parse.signpost.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import utils.PrefManager;

public class BeepingAIReditActivity extends AppCompatActivity {

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    Toolbar toolbar;
    Bitmap bm;
    private Device device;

    String charset = "UTF-8";
    String requestURL;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beeping_airedit);

        device = (Device) getIntent().getSerializableExtra("DEVICE_KEY");
        requestURL = "http://beepingsscb.devprium.com/api/devices/" + device.getId() + "/pictures?access_token="
                + PrefManager.getInstance(BeepingAIReditActivity.this).getToken();
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

        ImageButton camera = (ImageButton) findViewById(R.id.camera_btn_air);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                Intent intent_camera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent_camera);*/
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.imageView_back_air);
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
                try {
                    onSelectFromGalleryResult(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    private void onSelectFromGalleryResult(Intent data) throws IOException {
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

        new UserPhotoTask().execute();

        return true;

    }

    public class UserPhotoTask extends AsyncTask<Void, Void, Boolean> {

        String serverUrl = "http://beepingsscb.devprium.com";

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performUpdateCall();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Toast.makeText(getBaseContext(), "Refresh selected", Toast.LENGTH_SHORT)
                        .show(); // And when we get to grid activity the image is old there, right? yes.
                PrefManager.getInstance(BeepingAIReditActivity.this).updateAirDevice(device);
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
                // here ?
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
