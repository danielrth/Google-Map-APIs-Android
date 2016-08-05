package com.bp_android.prium.beeping_android.fragments;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bp_android.prium.beeping_android.R;

public class QRCodeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.nav_drawer_vert));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        toolbar.setBackgroundColor(getResources().getColor(R.color.black_transparent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black_transparent));
        }

        HandleClick hc = new HandleClick();
        findViewById(R.id.butQR).setOnClickListener(hc);
        findViewById(R.id.butProd).setOnClickListener(hc);
        findViewById(R.id.butOther).setOnClickListener(hc);
    }

    private class HandleClick implements View.OnClickListener {
        public void onClick(View arg0) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            switch (arg0.getId()) {
                case R.id.butQR:
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    break;
                case R.id.butProd:
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    break;
                case R.id.butOther:
                    intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF");
                    break;
            }
            startActivityForResult(intent, 0);    //Barcode Scanner to scan for us
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
            TextView tvResult = (TextView) findViewById(R.id.tvResult); // here
            if (resultCode == RESULT_OK) {
                tvStatus.setText(intent.getStringExtra("SCAN_RESULT_FORMAT"));
                tvResult.setText(intent.getStringExtra("SCAN_RESULT"));
                Intent resultData = new Intent();
                resultData.putExtra("SERIAL_NUMBER", intent.getStringExtra("SCAN_RESULT"));
                setResult(RESULT_OK, resultData);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                tvStatus.setText("Press a button to start a scan.");
                tvResult.setText("Scan cancelled.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menu.removeItem(R.id.action_settings);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MENU", "selected" + item);

        onBackPressed();
        return true;
    }

}
