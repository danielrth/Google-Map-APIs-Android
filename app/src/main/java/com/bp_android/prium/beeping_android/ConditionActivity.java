package com.bp_android.prium.beeping_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConditionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

        Button cond_click = (Button) findViewById(R.id.back);
        cond_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ConditionActivity.this, SignUpActivity.class);
                startActivity(back);
                //sdlfkjslf
            }
        });
    }
}
