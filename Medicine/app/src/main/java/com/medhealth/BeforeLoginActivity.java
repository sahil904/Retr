package com.medhealth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.medi.R;

@SuppressWarnings("ALL")
public class BeforeLoginActivity extends AppCompatActivity {

    RelativeLayout loginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_login);
        loginLayout = findViewById(R.id.button_login_layout);
        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeforeLoginActivity.this,NewLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
