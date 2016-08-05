package com.bp_android.prium.beeping_android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.MainActivity;
import com.bp_android.prium.beeping_android.MapsActivity;
import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.UserInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import utils.PrefManager;

/**
 * Created by Vaibhav on 2/11/16.
 */
//This is the class you are trying to open as activity, but it's a fragment

public class MyAccountFragment extends Fragment {

    private UserInfo userInfo;

    public MyAccountFragment() {

    }

    private TextView firstName, lastName, birthDate, mobile, email, text_click;
    private String first_name, last_name, birth_date, mobile_no, email_login;
    private ImageView img_back;
    private ImageButton add_photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        // userInfo = (UserInfo) getIntent().getSerializableExtra("DEVICE_KEY");
        ((MainActivity) getActivity()).setToolbarColor(Color.BLACK);
        setHasOptionsMenu(true);
        Log.i("ACC fra", "onCreateView");

        text_click = (TextView) view.findViewById(R.id.txt_click_account);

        add_photo = (ImageButton) view.findViewById(R.id.btn_add_photo_account);
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add_photo.setVisibility(View.GONE);
                text_click.setVisibility(View.GONE);
                Intent ed = new Intent(getActivity(), MyAccountEditFragment.class);
                startActivityForResult(ed, 2);

            }
        });

        img_back = (ImageView) view.findViewById(R.id.img_add_account);

        // img_back = (ImageView) view.findViewById(R.id.image_account_back);

        first_name = PrefManager.getInstance(getActivity()).getFirstNameKey();

        firstName = (TextView) view.findViewById(R.id.first_name);
        firstName.setText(first_name);

        last_name = PrefManager.getInstance(getActivity()).getLastNameKey();
        lastName = (TextView) view.findViewById(R.id.last_name);
        lastName.setText(last_name);

        birth_date = PrefManager.getInstance(getActivity()).getBirthdayKey();
        birthDate = (TextView) view.findViewById(R.id.birthday);
        birthDate.setText(birth_date);

        email_login = PrefManager.getInstance(getActivity()).getLogin();
        Log.i("FRAGMENT EMAIL", email_login);
        email = (TextView) view.findViewById(R.id.email_login_name);
        email.setText(email_login);

        mobile_no = PrefManager.getInstance(getActivity()).getMobileKey();
        mobile = (TextView) view.findViewById(R.id.mobile);
        mobile.setText(mobile_no);
        return view;

    }

    // Call Back method  to get the Message form other Activity
  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            String message=data.getStringExtra("MESSAGE");
            String message1=data.getStringExtra("MESSAGE1");
            String message2=data.getStringExtra("MESSAGE2");
            String message3=data.getStringExtra("MESSAGE3");
            String message4=data.getStringExtra("MESSAGE4");

            firstName.setText(message);
            lastName.setText(message1);
            birthDate.setText(message2);
            mobile.setText(message3);
            email.setText(message4);

        }
    }
*/

    @Override
    public void onResume() {
        super.onResume();
        first_name = PrefManager.getInstance(getActivity()).getFirstNameKey();
        firstName.setText(first_name);

        last_name = PrefManager.getInstance(getActivity()).getLastNameKey();
        Log.i("RESUME last name", last_name);
        lastName.setText(last_name);

        birth_date = PrefManager.getInstance(getActivity()).getBirthdayKey();
        birthDate.setText(birth_date);

        email_login = PrefManager.getInstance(getActivity()).getLogin();
        Log.i("RESUME EMAIL", email_login);
        email.setText(email_login);

        mobile_no = PrefManager.getInstance(getActivity()).getMobileKey();
        mobile.setText(mobile_no);

        add_photo.setVisibility(View.GONE);
        text_click.setVisibility(View.GONE);

        Log.i("My account", "on resume");
        if (PrefManager.getInstance(getActivity()).getPicture_account() != -1) {
            ImageLoader.getInstance().displayImage("http://beepingsscb.devprium.com/api/pictures/" + PrefManager.getInstance(getActivity()).getPicture_account() +
                    "?type=medium&access_token=" + PrefManager.getInstance(getActivity()).getToken(), img_back);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeItem(R.id.action_settings);
        MenuItem edit = menu.add("Edit");
        edit.setIcon(R.drawable.nav_modif_vert);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("acc fr", "item");
        if (item.getTitle().equals("Edit")) {
            Log.i("MENU", "Edit");
            Toast.makeText(getActivity(), "Refresh selected", Toast.LENGTH_SHORT)
                    .show();
            Intent ed = new Intent(getActivity(), MyAccountEditFragment.class);
            startActivityForResult(ed, 2);

        }
        return true;

    }
}
