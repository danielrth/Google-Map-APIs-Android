package com.bp_android.prium.beeping_android.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import utils.PrefManager;

/**
 * Created by Vaibhav on 2/23/16.
 */
public class ContactFragment extends android.support.v4.app.Fragment {

    public ContactFragment() {

    }

    private Button send_message;
    private EditText subject_edit, message_edit;
    private ImageView contact_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contact, container, false);


        subject_edit = (EditText) view.findViewById(R.id.edit_text1_contact);
        message_edit = (EditText) view.findViewById(R.id.edit_text2_contact);

        contact_image = (ImageView) view.findViewById(R.id.contact_image_back);

        send_message = (Button) view.findViewById(R.id.edit_btn_contact);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message1 = subject_edit.getText().toString();
                String message2 = message_edit.getText().toString();
                new UserContactTask(message1, message2).execute();
                Intent intent_back = new Intent(getActivity(), MainActivity.class);
                startActivity(intent_back);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("Contact", "on resume");
        if (PrefManager.getInstance(getActivity()).getPicture_account() != -1) {
            ImageLoader.getInstance().displayImage("http://beepingsscb.devprium.com/api/pictures/" + PrefManager.getInstance(getActivity()).getPicture_account() +
                    "?type=medium&access_token=" + PrefManager.getInstance(getActivity()).getToken(), contact_image);
        }

    }

    public class UserContactTask extends AsyncTask<Void, Void, Boolean> {

        private String mSubject;
        private String mContact_message;
        String serverUrl = "http://beepingsscb.devprium.com";
        private ProgressDialog progressDialog;

        UserContactTask(String subject, String contact_message) {

            try {
                mSubject = URLEncoder.encode(subject, "UTF-8");
                mContact_message = URLEncoder.encode(contact_message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                mSubject = "Unsupported encoding";
                mContact_message = "Unsupported encoding";
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return performCodeCall(mSubject, mContact_message);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.i("RESULT", success + "");
            if (success) {
                Toast.makeText(getActivity(), "Contact message send successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean performCodeCall(String subject, String contact_message) {
            URL url;
            String requestURL = serverUrl + "/api/contact_messages?access_token=" + PrefManager.getInstance(getActivity()).getToken() + "&subject=" + subject
                    + "&message=" + contact_message + "&system_info=Android";
            Log.e("Add device", requestURL.toString());
            boolean result = false;
            try {
                Log.i("SERIAL NUMBER", subject);
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
