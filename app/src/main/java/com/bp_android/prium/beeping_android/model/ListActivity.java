package com.bp_android.prium.beeping_android.model;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.adapters.BluetoothDeviceAdapter;
import com.bp_android.prium.beeping_android.fragments.BluetoothActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ImageView Cross;
    private ListView BluetoothDevicesListView;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Cross = (ImageView) findViewById(R.id.quit);
        Cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BluetoothDevicesListView = (ListView) findViewById(R.id.bluetoothDevices);
        BluetoothDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                BluetoothDevice device = (BluetoothDevice) adapterView.getItemAtPosition(i);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Intent intent = new Intent(ListActivity.this, ContinueActivity.class);
                    intent.putExtra(BluetoothActivity.EXTRA_DEVICE, device);
                    startActivity(intent);
                } else
                    device.createBond();

                /*boolean isBonded = true;
                BluetoothDevice device = (BluetoothDevice) adapterView.getItemAtPosition(i);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED)
                    isBonded = device.createBond();
                if(isBonded)
                {
                    Snackbar.make(findViewById(android.R.id.content), R.string.contentSnackbar1, Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListActivity.this, ContinueActivity.class);
                    intent.putExtra(MainActivity.EXTRA_DEVICE, device);
                    startActivity(intent);
                }
                else
                    Snackbar.make(findViewById(android.R.id.content), R.string.contentSnackbar2, Snackbar.LENGTH_SHORT).show();*/
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(BluetoothActivity.EXTRA_DEVICES_LIST)) {
            mDeviceList = intent.getParcelableArrayListExtra(BluetoothActivity.EXTRA_DEVICES_LIST);
            bluetoothDeviceAdapter = new BluetoothDeviceAdapter(ListActivity.this, mDeviceList);
            BluetoothDevicesListView.setAdapter(bluetoothDeviceAdapter);
        }

        RegisterReceiver();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void RegisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Intent intentContinue = new Intent(ListActivity.this, ContinueActivity.class);
                    intentContinue.putExtra(BluetoothActivity.EXTRA_DEVICE, device);
                    startActivity(intentContinue);
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "List Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bp_android.prium.beeping_android.model/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "List Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bp_android.prium.beeping_android.model/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
