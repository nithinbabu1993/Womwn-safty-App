package com.example.womensaftyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AllPoliceStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_police_stations);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),AdminHome.class));finish();
    }
}