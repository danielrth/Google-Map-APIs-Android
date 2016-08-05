package com.bp_android.prium.beeping_android;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bp_android.prium.beeping_android.fragments.BoutiqueFragment;
import com.bp_android.prium.beeping_android.fragments.ContactFragment;
import com.bp_android.prium.beeping_android.fragments.MyAccountFragment;
import com.bp_android.prium.beeping_android.fragments.NotificationFragment;
import com.bp_android.prium.beeping_android.fragments.SelectBeepingsFragment;
import com.parse.ParseUser;

import utils.ConnectionDetector;
import utils.PrefManager;

public class MainActivity extends AppCompatActivity {

    // Connection detector class
    ConnectionDetector cd;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    SelectBeepingsFragment scanFragment;

    NavigationView navigationView;
    DrawerLayout drawer;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.nav_drawer_vert));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        // toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent1));

        cd = new ConnectionDetector(getApplicationContext());

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent1));
        }


/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        String str_user1 = PrefManager.getInstance(this).getLogin();
        String str_user2 = PrefManager.getInstance(this).getFirstNameKey();
        String str_user3 = PrefManager.getInstance(this).getLastNameKey();
        View headerLayout = navigationView.getHeaderView(0);
        //View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView panel = (TextView) headerLayout.findViewById(R.id.textView_email);
        TextView panel1 = (TextView) headerLayout.findViewById(R.id.textView_name);
        panel.setText(str_user1);
        panel1.setText(str_user2 + " " + str_user3);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.

                // item.setChecked(false);
                int id = item.getItemId();
                String title = getString(R.string.app_name);
                if (id == R.id.nav_camera) {
                    // Handle the camera action

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.replace(R.id.fragmentLayout, new MyAccountFragment()).commit();
                    title = "Mon compte";

                } else if (id == R.id.nav_gallery) {

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    mFragmentTransaction.addToBackStack(null);
                    scanFragment = new SelectBeepingsFragment();
                    mFragmentTransaction.replace(R.id.fragmentLayout, scanFragment).commit();
                    title = "Mes Beepings";
                    setTitleColor(R.color.color_menu);

                } else if (id == R.id.nav_slideshow) {

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.replace(R.id.fragmentLayout, new NotificationFragment()).commit();
                    title = "Les notifications";
                    setTitleColor(R.color.light);

                } else if (id == R.id.nav_manage) {

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.replace(R.id.fragmentLayout, new BoutiqueFragment()).commit();
                    title = "La boutiques";
                    setTitleColor(R.color.light);

                } else if (id == R.id.nav_share) {

                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.replace(R.id.fragmentLayout, new ContactFragment()).commit();
                    title = "Contactez-nous";
                    setTitleColor(R.color.light);

                } else if (id == R.id.nav_send) {

                    ParseUser.logOut();
                    finish();
                    Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(logout);

                }

                getSupportActionBar().setTitle(title);
                toolbar.invalidate();

                drawer.closeDrawer(GravityCompat.START);
                toolbar.invalidate();

                return true;
            }
        });

        toolbar.invalidate();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                // getFragmentManager().popBackStack();
                findViewById(R.id.welcomeText).setVisibility(View.VISIBLE);
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    //  getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MainActivityRes", requestCode + "");
        if (scanFragment != null) {
            scanFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setBarTitle(final String title) {
        getSupportActionBar().setTitle(title);
        // toolbar.invalidate();
    }

}
