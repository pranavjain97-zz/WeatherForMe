package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private TextView detailData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailData = (TextView) findViewById(R.id.tv_weather_data);

        Intent detailIntent = getIntent();
        if (detailIntent.hasExtra(Intent.EXTRA_TEXT)) {

            String textPassed = detailIntent.getStringExtra(Intent.EXTRA_TEXT);
            detailData.setText(textPassed);


        }
    }
}
