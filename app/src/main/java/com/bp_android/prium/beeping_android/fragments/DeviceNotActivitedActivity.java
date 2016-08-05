package com.bp_android.prium.beeping_android.fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;

public class DeviceNotActivitedActivity extends AppCompatActivity {

    private Button try_again;
    Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_not_activited);

        try_again = (Button) findViewById(R.id.try_again);

        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_zen = new Intent(DeviceNotActivitedActivity.this, AddBeepingZenActivity.class);
                add_zen.putExtra("DEVICE_KEY", device);
                startActivityForResult(add_zen, 22);
                // startActivity(add_zen);
            }
        });
    }
}
