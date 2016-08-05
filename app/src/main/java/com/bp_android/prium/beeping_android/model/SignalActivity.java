package com.bp_android.prium.beeping_android.model;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.fragments.BluetoothActivity;
import com.bp_android.prium.beeping_android.fragments.InformationActivity;

import java.util.ArrayList;

public class SignalActivity extends BluetoothActivity {

    boolean isBluetoothDeviceFound = false;

    private ImageView Cross;
    ImageButton AlarmImage;
    TextView NearOrFarTop;
    TextView NearOrFarBottom;

    ImageView BatteryLevelImage;
    TextView BatteryLevel;

    BluetoothDevice mDevice;

    View SircleXL;
    View SircleL;
    View SircleM;

    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        initViews();

        mDevice = getIntent().getParcelableExtra(BluetoothActivity.EXTRA_DEVICE);

        initBluetoothAndRegisterReceiver();
    }

    private void initBluetoothAndRegisterReceiver() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, filter);
            mBtAdapter.enable();
            mBtAdapter.startDiscovery();
        }
    }

    private void releaseBluetoothAndRegisterReceiver() {
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        unregisterReceiver(mReceiver);
    }

    private void initViews() {
        Cross = (ImageView) findViewById(R.id.quit);
        Cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SircleXL = (View) findViewById(R.id.circlexl);
        SircleL = (View) findViewById(R.id.circlel);
        SircleM = (View) findViewById(R.id.circlem);

        AlarmImage = (ImageButton) findViewById(R.id.alarmImage);
        NearOrFarBottom = (TextView) findViewById(R.id.nearOrFarBottom);
        NearOrFarTop = (TextView) findViewById(R.id.nearOrFarTop);

        BatteryLevelImage = (ImageView) findViewById(R.id.batteryImage);
        BatteryLevel = (TextView) findViewById(R.id.batteryState);

        if (getBatteryLevel() < 14) {
            BatteryLevel.setText(R.string.contentSignalBatteryLow);
            BatteryLevelImage.setImageResource(R.drawable.picto_batt_critique);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected boolean isBLEEnabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

/*    public void onFindMeClicked(final View view) {
        if (isBLEEnabled()) {
            if (!isBluetoothDeviceFound) {
                // do nothing
            } else if (getService().toggleImmediateAlert()) {

            } else {

            }
        } else {

        }
    }*/

    private void setLongDistance() {
        SircleXL.setBackgroundResource(R.drawable.circle_white);
        SircleL.setBackgroundResource(R.drawable.circle_white);
        SircleM.setBackgroundResource(R.drawable.circle_white);
        AlarmImage.setImageResource(R.drawable.btn_sonner_air_trait_blanc);
        NearOrFarTop.setText(R.string.contentSignalFar);
        NearOrFarBottom.setText(R.string.contentSignalFarBottom);
    }

    private void setMediumDistance() {
        SircleXL.setBackgroundResource(R.drawable.circle_dark_green);
        SircleL.setBackgroundResource(R.drawable.circle_white);
        SircleM.setBackgroundResource(R.drawable.circle_white);
        AlarmImage.setImageResource(R.drawable.btn_sonner_air_trait_blanc);
        NearOrFarTop.setText(R.string.contentSignalFar);
        NearOrFarBottom.setText(R.string.contentSignalFarBottom);
    }

    private void setSmallDistance() {
        SircleXL.setBackgroundResource(R.drawable.circle_dark_green);
        SircleL.setBackgroundResource(R.drawable.circle_dark_green);
        SircleM.setBackgroundResource(R.drawable.circle_white);
        AlarmImage.setImageResource(R.drawable.btn_sonner_air_trait_blanc);
        NearOrFarTop.setText(R.string.contentSignalFar);
        NearOrFarBottom.setText(R.string.contentSignalFarBottom);
    }

    private void setCloseDistance() {
        SircleXL.setBackgroundResource(R.drawable.circle_dark_green);
        SircleL.setBackgroundResource(R.drawable.circle_dark_green);
        SircleM.setBackgroundResource(R.drawable.circle_dark_green);
        AlarmImage.setImageResource(R.drawable.btn_sonner_air_fond_blanc);
        NearOrFarTop.setText(R.string.contentSignalNear);
        NearOrFarBottom.setText(R.string.contentSignalNearBottom);
    }

    private void goToSearchScreen() {
        Intent intent = new Intent(SignalActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }

    private float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return ((float) level / (float) scale) * 100.0f;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().equals(mDevice.getAddress())) {
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    if (rssi < -85) {
                        setLongDistance();
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (rssi < -70)
                        setMediumDistance();
                    else if (rssi < -55)
                        setSmallDistance();
                    else
                        setCloseDistance();
                    isBluetoothDeviceFound = true;
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (!isBluetoothDeviceFound) {
                    mBtAdapter.cancelDiscovery();
                    //showExitAlert();
                    Intent resultIntent = new Intent(SignalActivity.this, BluetoothActivity.class);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(SignalActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SignalActivity.this);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setContentTitle(getResources().getString(R.string.contentSignalExitDialog1));
                    mBuilder.setContentText(getResources().getString(R.string.contentSignalExitDialog2));
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setAutoCancel(true);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());
                    //goToSearchScreen();
                } else {
                    isBluetoothDeviceFound = false;
                    mBtAdapter.startDiscovery();
                }
            }
        }
    };

    private void showExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignalActivity.this);
        builder.setCustomTitle(getLayoutInflater().inflate(R.layout.custom_signal_dialog_title, null));
        builder.setMessage(R.string.contentSignalExitDialog2);
        builder.setPositiveButton(R.string.contentSignalExitDialogYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                goToSearchScreen();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    /*@Override
    protected void onDestroy() {
        releaseBluetoothAndRegisterReceiver();
        super.onDestroy();
    }*/

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
}
