package com.bp_android.prium.beeping_android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.model.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import utils.AirbrakeNotifier;
import utils.ConnectionDetector;
import utils.JsonParser;
import utils.PrefManager;

public class SplashScreenActivity extends Activity {

    Animation animFadein;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AirbrakeNotifier.register(this, "API_KEY");

        PrefManager manager = PrefManager.getInstance(this);
        if (manager.isLogged()) {
            if (ConnectionDetector.isOnline()) {
                new UserLoginTask(manager.getLogin(), manager.getPassword(), manager.getFirstNameKey(), manager.getLastNameKey()
                        , manager.getBirthdayKey(), manager.getMobileKey()).execute();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else {

            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            };
            timer.start();
        }

        // image = (ImageView) findViewById(R.id.imageView_splash);

        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_img);

        // start the animation
        // image.startAnimation(animFadein);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstName;
        private final String mLastName;
        private final String mBirthDate;
        private final String mMobileNumber;
        String serverUrl = "http://beepingsscb.devprium.com";

        UserLoginTask(String email, String password, String first_name, String last_name, String birth_date, String mobile_number) {
            mEmail = email;
            mPassword = password;
            mFirstName = first_name;
            mLastName = last_name;
            mBirthDate = birth_date;
            mMobileNumber = mobile_number;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performLoginCall(mEmail, mPassword, mFirstName, mLastName, mBirthDate);

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashScreenActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }

        public boolean performLoginCall(String mail, String password, String first_name, String last_name, String birthday) {
            URL url;
            String requestURL = serverUrl + "/oauth/token?username=" + mail + "&password=" + URLEncoder.encode(password) + "&grant_type=password";
            Log.e("Sign in ", requestURL.toString());
            boolean result = false;
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    PrefManager.getInstance(SplashScreenActivity.this).setLogin(mEmail);
                    PrefManager.getInstance(SplashScreenActivity.this).setPassword(mPassword);
                    PrefManager.getInstance(SplashScreenActivity.this).setLogged(true);
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = "";
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i("RESPONSE USER INFO", response);
                    JSONObject tokenJSON = new JSONObject(response);
                    String token = tokenJSON.getString("access_token");
                    PrefManager.getInstance(SplashScreenActivity.this).setToken(token);
                    getUsersInfo(token);
                    getListDevices(token);
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }

        private void getUsersInfo(final String token) throws Exception {

            URL detailedUrl = new URL(serverUrl + "/api/users/?access_token=" + token);
            Log.i("GET USER INFO", detailedUrl.toString());
            HttpURLConnection detailedConn = (HttpURLConnection) detailedUrl.openConnection();
            detailedConn.setReadTimeout(15000);
            detailedConn.setConnectTimeout(15000);
            detailedConn.setRequestMethod("GET");
            int responseCodeDet = detailedConn.getResponseCode();

            if (responseCodeDet == HttpURLConnection.HTTP_OK) {
                String lineDet;
                BufferedReader brDet = new BufferedReader(new InputStreamReader(detailedConn.getInputStream()));
                String responseDet = "";
                while ((lineDet = brDet.readLine()) != null) {
                    responseDet += lineDet;
                }
                Log.i("RESPONSE", responseDet);
                JSONObject json = new JSONObject(responseDet).getJSONObject("data").getJSONObject("attributes");
                Log.i("FIRST NAME", json.getString("first_name"));
                Log.i("LAST NAME", json.getString("last_name"));
                Log.i("BIRTH", json.getString("birthdate"));
                Log.i("MOBILE", json.getString("mobile_phone"));
                PrefManager.getInstance(SplashScreenActivity.this).setFirstNameKey(json.getString("first_name"));
                PrefManager.getInstance(SplashScreenActivity.this).setLastNameKey(json.getString("last_name"));
                PrefManager.getInstance(SplashScreenActivity.this).setBirthdayKey(json.getString("birthdate"));
                PrefManager.getInstance(SplashScreenActivity.this).setMobileKey(json.getString("mobile_phone"));
                int picture = -1;
                try {
                    picture = json.getInt("picture");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PrefManager.getInstance(SplashScreenActivity.this).setPicture_account(picture);
                PrefManager.getInstance(SplashScreenActivity.this).setUserId(new JSONObject(responseDet).getJSONObject("data").getInt("id"));
               /* UserInfo userInfo = new UserInfo(json.getString("mobile_phone"),
                        json.getString("first_name"),
                        json.getString("last_name"), json.getString("email"),
                        json.getString("birthdate"));
                PrefManager.getInstance(SplashScreenActivity.this).setUserInfo(userInfo);*/
            }

        }

        private void getListDevices(final String token) throws Exception {

            URL detailedUrl = new URL(serverUrl + "/api/devices?access_token=" + token);
            Log.i("GET USER INFO", detailedUrl.toString());
            HttpURLConnection detailedConn = (HttpURLConnection) detailedUrl.openConnection();
            detailedConn.setReadTimeout(15000);
            detailedConn.setConnectTimeout(15000);
            detailedConn.setRequestMethod("GET");
            int responseCodeDet = detailedConn.getResponseCode();
            Log.i("devices resp", responseCodeDet + "");

            if (responseCodeDet == HttpURLConnection.HTTP_OK || responseCodeDet == HttpURLConnection.HTTP_CREATED) {
                String lineDet;
                BufferedReader brDet = new BufferedReader(new InputStreamReader(detailedConn.getInputStream()));
                String responseDet = "";
                while ((lineDet = brDet.readLine()) != null) {
                    responseDet += lineDet;
                }
                Log.i("RESPONSE DEVICES", responseDet);

                JSONArray devices = new JSONObject(responseDet).getJSONArray("data");
                JsonParser.parseDevices(devices, SplashScreenActivity.this);

            }

        }
    }
}