package com.example.womensaftyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AppUsers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_users);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),AdminHome.class));finish();
    }
}