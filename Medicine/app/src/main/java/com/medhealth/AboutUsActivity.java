package com.medhealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.medi.R;

@SuppressWarnings("ALL")
public class AboutUsActivity extends AppCompatActivity {
    LinearLayout linear_terms,linear_about,linear_termCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        linear_terms = findViewById(R.id.linear_terms);
        linear_about = findViewById(R.id.linear_about);
        linear_termCondition = findViewById(R.id.linear_termCondition);

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        int page = getIntent().getIntExtra("page",0);
        if (page==1){
            linear_about.setVisibility(View.VISIBLE);

        }else if (page == 2){
            linear_termCondition.setVisibility(View.VISIBLE);
        }else if (page==3){
            linear_terms.setVisibility(View.VISIBLE);
        }

//        Log.d("description",getIntent().getStringExtra("description"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Log.d("description", String.valueOf(getIntent().getStringArrayExtra("description")).toString());
       // TextView textView = findViewById(R.id.about_tv);
       // textView.setText(getIntent().getStringExtra("description"));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
