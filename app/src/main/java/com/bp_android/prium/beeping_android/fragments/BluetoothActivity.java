package com.bp_android.prium.beeping_android.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.ContinueActivity;
import com.bp_android.prium.beeping_android.model.ListActivity;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    //  public class BluetoothActivity<E extends BleProfileService.LocalBinder> extends AppCompatActivity {

    Button b1, b2, b3, b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    private View circles;
    private View circlem;
    private View circlel;

    Toolbar toolbar;
    //private E mService;

    public static final String EXTRA_DEVICES_LIST = "BEEPINGS_AIR_EXTRA_DEVICES_LIST";
    public static final String EXTRA_DEVICE = "BEEPINGS_AIR_EXTRA_DEVICE";

    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bluetooth_air);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.nav_drawer_vert));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        toolbar.setBackgroundColor(getResources().getColor(R.color.color_menu));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.color_menu));
        }

        initCircles();
        initBluetoothAndRegisterReceiver();
    }

/*    protected E getService() {
        return mService;
    }*/

    // showAlertDialog(context, "Bluetooth is synchronised");

/*      b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView) findViewById(R.id.listView);*/

    private void initBluetoothAndRegisterReceiver() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
            mBtAdapter.enable();
            mBtAdapter.startDiscovery();
        }
    }

    private void releaseBluetoothAndRegisterReceiver() {
        if (mBtAdapter != null)
            mBtAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }

    private void initCircles() {
        circles = (View) findViewById(R.id.circles);
        circlem = (View) findViewById(R.id.circlem);
        circlel = (View) findViewById(R.id.circlel);

        /*circles.setAlpha(0);
        circlem.setAlpha(0);
        circlel.setAlpha(0);*/

        final AlphaAnimation fadeInS = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation fadeOutS = new AlphaAnimation(1.0f, 0.0f);
        fadeInS.setDuration(1000);
        fadeOutS.setDuration(1000);
        fadeInS.setFillBefore(true);
        fadeInS.setFillEnabled(true);
        fadeInS.setFillAfter(true);
        fadeOutS.setFillBefore(true);
        fadeOutS.setFillEnabled(true);
        fadeOutS.setFillAfter(true);

        final AlphaAnimation fadeInM = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation fadeOutM = new AlphaAnimation(1.0f, 0.0f);
        fadeInM.setDuration(1000);
        fadeOutM.setDuration(1000);
        fadeInM.setFillEnabled(true);
        fadeInM.setFillAfter(true);
        fadeInM.setFillBefore(true);
        fadeOutM.setFillBefore(true);
        fadeOutM.setFillEnabled(true);
        fadeOutM.setFillAfter(true);

        final AlphaAnimation fadeInL = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation fadeOutL = new AlphaAnimation(1.0f, 0.0f);
        fadeInL.setDuration(1000);
        fadeOutL.setDuration(1000);
        fadeInL.setFillEnabled(true);
        fadeInL.setFillAfter(true);
        fadeInL.setFillBefore(true);
        fadeOutL.setFillBefore(true);
        fadeOutL.setFillEnabled(true);
        fadeOutL.setFillAfter(true);

        fadeInS.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circlem.startAnimation(fadeInM);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeInM.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circlel.startAnimation(fadeInL);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeInL.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circles.startAnimation(fadeOutS);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeOutS.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circlem.startAnimation(fadeOutM);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeOutM.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circlel.startAnimation(fadeOutL);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeOutL.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                circles.startAnimation(fadeInS);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        circles.startAnimation(fadeInS);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName().contains("AIR")) {
                    boolean isInList = false;
                    for (int i = 0; i < mDeviceList.size(); i++) {
                        if (device.getAddress().equals(mDeviceList.get(i).getAddress())) {
                            isInList = true;
                            break;
                        }
                    }
                    if (!isInList)
                        mDeviceList.add(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mDeviceList.size() == 0)
                    showRepeatAlert();
                else if (mDeviceList.size() == 1)
                    startPairingAndLastActivity(mDeviceList.get(0));
                else
                    startListActivity();
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Intent intentContinue = new Intent(BluetoothActivity.this, ContinueActivity.class);
                    intentContinue.putExtra(EXTRA_DEVICE, device);
                    mDeviceList.clear();
                    startActivity(intentContinue);
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void startPairingAndLastActivity(BluetoothDevice device) {
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            Intent intent = new Intent(BluetoothActivity.this, ContinueActivity.class);
            intent.putExtra(EXTRA_DEVICE, device);
            mDeviceList.clear();
            startActivity(intent);
        } else
            device.createBond();
    }

    private void startListActivity() {
        ArrayList<BluetoothDevice> tmpList = new ArrayList<BluetoothDevice>(mDeviceList);
        mDeviceList.clear();
        Intent intent = new Intent(BluetoothActivity.this, ListActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_DEVICES_LIST, tmpList);
        startActivity(intent);
    }

    private void showRepeatAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        builder.setCustomTitle(getLayoutInflater().inflate(R.layout.custom_main_dialog_title, null));
        builder.setMessage(R.string.contentMainDialog2);
        builder.setPositiveButton(R.string.contentMainDialogYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mBtAdapter.startDiscovery();
            }
        });
        builder.setNegativeButton(R.string.contentMainDialogNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                BluetoothActivity.this.finish();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    @Override
    protected void onStop() {
        releaseBluetoothAndRegisterReceiver();
        super.onStop();
    }

    @Override
    protected void onResume() {
        initBluetoothAndRegisterReceiver();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


 /*   public void on(View v) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v) {
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
    }

    public void visible(View v) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v) {
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
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
    }*/

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle("Synchronisation");

        // Setting Dialog Message
        alertDialog.setMessage("La synchronisation Bluetooth a échouée, voulez-vous réessayer ?");

        // Setting alert dialog icon
        // alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
