package com.bp_android.prium.beeping_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.MultipartUtility;
import com.bp_android.prium.beeping_android.model.UserInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import utils.ConnectionDetector;
import utils.PrefManager;


/**
 * Created by Vaibhav on 2/12/16.
 */
//this is your activity, it extends AppCompatActivity
public class MyAccountEditFragment extends AppCompatActivity implements View.OnClickListener {

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    Toolbar toolbar;
    AutoCompleteTextView editText1_firstName, editText2_lastName, editText3_birthDate, editText4_mobile,
            editText5_email, editText6_old_password, editText7_new_password;
    String first_name, last_name, birth_date, mobile_no, email_login;

    private ImageButton ib1;
    private Calendar cal;
    private int day;
    private int month;
    private int year;

    private UserInfo userInfo;
    String charset = "UTF-8";
    String requestURL;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //here's your layout for whole activity
        setContentView(R.layout.fragment_account_edit);
        Log.i("ACC ED ACT", "onCreate");

        // userInfo = (UserInfo) getIntent().getSerializableExtra("USER_KEY");

        requestURL = "http://beepingsscb.devprium.com/api/users/" + PrefManager.getInstance(this).getUserId() + "/pictures?access_token="
                + PrefManager.getInstance(MyAccountEditFragment.this).getToken();
        Log.i("PICTURE OUTPUT", requestURL);

        ib1 = (ImageButton) findViewById(R.id.imageButton1_edit);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        ib1.setOnClickListener(this);

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

        ImageButton camera = (ImageButton) findViewById(R.id.camera_btn);

        editText1_firstName = (AutoCompleteTextView) findViewById(R.id.edit_text1_edit);
        editText2_lastName = (AutoCompleteTextView) findViewById(R.id.edit_text2_edit);
        editText3_birthDate = (AutoCompleteTextView) findViewById(R.id.edit_text3_edit);
        editText4_mobile = (AutoCompleteTextView) findViewById(R.id.edit_text4_edit);
        editText5_email = (AutoCompleteTextView) findViewById(R.id.edit_text5_edit);

        editText6_old_password = (AutoCompleteTextView) findViewById(R.id.edit_text7_edit);
        editText6_old_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertPassword();
            }
        });

        editText7_new_password = (AutoCompleteTextView) findViewById(R.id.edit_text8_edit);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                Intent intent_camera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent_camera);*/
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.imageView_account_back);

        first_name = PrefManager.getInstance(this).getFirstNameKey();
        editText1_firstName.setText(first_name);

        last_name = PrefManager.getInstance(this).getLastNameKey();
        editText2_lastName.setText(last_name);

        birth_date = PrefManager.getInstance(this).getBirthdayKey();
        editText3_birthDate.setText(birth_date);

        mobile_no = PrefManager.getInstance(this).getMobileKey();
        editText4_mobile.setText(mobile_no);

        email_login = PrefManager.getInstance(this).getLogin();
        Log.i("FRAGMENT EMAIL", email_login);
        editText5_email.setText(email_login);

        if (PrefManager.getInstance(this).getPicture_account() != -1) {
            ImageLoader.getInstance().displayImage("http://beepingsscb.devprium.com/api/pictures/" + PrefManager.getInstance(this).getPicture_account() +
                    "?type=medium&access_token=" + PrefManager.getInstance(this).getToken(), ivImage);
        }

    }

    @Override
    public void onClick(View v) {
        showDialog(0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            editText3_birthDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear);
        }
    };

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
        String[] projection = {MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
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

        String message = editText1_firstName.getText().toString();
        String message1 = editText2_lastName.getText().toString();
        String message2 = editText3_birthDate.getText().toString();
        String message3 = editText4_mobile.getText().toString();
        String message4 = editText5_email.getText().toString();
        String message5 = editText6_old_password.getText().toString();
        String message6 = editText7_new_password.getText().toString();

        new UserLoginTask(message, message1, message2, message3).execute();
        //Call webservice here with the data from messages Strings
        new UserPasswordTask(message5, message6).execute();

        new UserPhotoTask().execute();

        return true;
    }

    private void showAlertPassword() {
        final AlertDialog builder = new AlertDialog.Builder(this, R.style.InvitationDialog)
                .setPositiveButton(R.string.password_yes, null)
                .setNegativeButton(R.string.password_no, null)
                .create();

        final EditText etNickName = new EditText(this);
        builder.setView(etNickName);
        builder.setTitle("Changer du mot de passe");
        builder.setMessage("please enter old password");

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button btnAccept = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etNickName.getText().toString().isEmpty()) {
                            Toast.makeText(MyAccountEditFragment.this, "Enter a password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Log.d(TAG, "You have entered: " + etNickName.getText().toString());
                            editText6_old_password.setText(etNickName.getText().toString());
                            builder.dismiss();
                        }
                    }
                });

                final Button btnDecline = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Log.d(TAG, "Invitation declined");
                        builder.dismiss();
                    }
                });
            }
        });

        /* Show the dialog */
        builder.show();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFirstName;
        private final String mLastName;
        private final String mBirthDate;
        private final String mMobileNumber;
        String serverUrl = "http://beepingsscb.devprium.com";

        UserLoginTask(String first_name, String last_name, String birth_date, String mobile_number) {

            mFirstName = first_name;
            mLastName = last_name;
            mBirthDate = birth_date;
            mMobileNumber = mobile_number;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performSignUpCall(mMobileNumber, mFirstName, mLastName, mBirthDate);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                PrefManager manager = PrefManager.getInstance(MyAccountEditFragment.this);
                manager.setFirstNameKey(mFirstName);
                manager.setLastNameKey(mLastName);
                manager.setBirthdayKey(mBirthDate);
                manager.setMobileKey(mMobileNumber);

            } else {
                Toast.makeText(MyAccountEditFragment.this, "Error occurred", Toast.LENGTH_SHORT).show();

            }
        }

        public boolean performSignUpCall(String mobilePhone, String first_name, String last_name, String birth_date) {
            URL url;
            String requestURL = serverUrl + "/api/users/" + PrefManager.getInstance(MyAccountEditFragment.this).getUserId() +
                    "?access_token=" + PrefManager.getInstance(MyAccountEditFragment.this).getToken() +
                    "&first_name=" + first_name + "&last_name=" + last_name + "&birthdate=" + birth_date + "&mobile_phone=" + mobilePhone;
            Log.e("UPDATE ", requestURL.toString());
            boolean result = false;
            try {
          /*  try {
                url = new URL(requestURL);
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPatch postMethod = new HttpPatch();
                postMethod.setURI(URI.create(requestURL));
                    *//*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                            2);
                    nameValuePairs.add(new BasicNameValuePair("user_name",
                            params[0]));
                    nameValuePairs.add(new BasicNameValuePair("user_pass",
                            params[1]));
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                            "UTF-8"));*//*
                String response = hc.execute(postMethod,
                        new BasicResponseHandler());
                Log.i("PATCH RESP", response);
                result = true;*/

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
                        .show();
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
                PrefManager.getInstance(MyAccountEditFragment.this).setPicture_account(new JSONArray(response.toString()).getJSONObject(0).getJSONObject("data").getInt("id"));

                Intent result = new Intent();
                result.putExtra("USER_KEY", userInfo);
                setResult(RESULT_OK, result);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    public class UserPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mOldPassword;
        private final String mNewPassword;
        String serverUrl = "http://beepingsscb.devprium.com";

        UserPasswordTask(String old_password, String new_password) {

            mOldPassword = old_password;
            mNewPassword = new_password;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performPasswordCall(mOldPassword, mNewPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

            } else {
                Toast.makeText(MyAccountEditFragment.this, "Error occurred", Toast.LENGTH_SHORT).show();

            }
        }

        public boolean performPasswordCall(String old_password, String new_password) {
            /*URL url;
            String requestURL = serverUrl + "/api/passwords/" +
                    "?access_token=" + PrefManager.getInstance(MyAccountEditFragment.this).getToken() +
                    "&old_password=" + old_password + "&new_password=" + new_password;
            Log.e("UPDATE ", requestURL.toString());*/
            if (TextUtils.isEmpty(old_password) || TextUtils.isEmpty(new_password)) {
                return true;
            }
            boolean result = false;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPatch http_pass = new HttpPatch(serverUrl + "/api/passwords");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("access_token", PrefManager.getInstance(MyAccountEditFragment.this).getToken()));
                nameValuePairs.add(new BasicNameValuePair("old_password", old_password));
                nameValuePairs.add(new BasicNameValuePair("new_password", new_password));
                http_pass.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(http_pass);
                Log.i("PASS CHANGE CODE", response.getStatusLine().getStatusCode() + "");
                Log.i("PASS CHANGE", response.getEntity().toString());
                result = true;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

    }
}