package com.example.womensaftyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.womensaftyapp.Dashboard.UserDashboard;

public class SendComplaint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_complaint);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
        finish();
    }
}