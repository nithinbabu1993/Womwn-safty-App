package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.Dashboard.MyComplaints;
import com.example.womensaftyapp.Dashboard.UserDashboard;
import com.example.womensaftyapp.databinding.ActivitySendComplaintBinding;
import com.example.womensaftyapp.settings.ComplaintModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SendComplaint extends AppCompatActivity {
    ActivitySendComplaintBinding binding;
    FirebaseFirestore db;
    String police[] = new String[100];
    String stationId,stationName,stationPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        SharedPreferences sd = getSharedPreferences("police", Context.MODE_PRIVATE);
       if(sd.getString("from","").equals("adapter")){
           stationId = sd.getString("sid","");
           stationName = sd.getString("sname","");
           stationPhone = sd.getString("sphone","");
       }else {
           police = sd.getString("policestation", "").split(":");
           stationId = police[10];
           stationName = police[1];
           stationPhone = police[8];
       }
        binding.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etcomplaint.getText().toString().isEmpty()){
                    binding.etcomplaint.setError("Enter error");
                }else{
                    sendComplaint();
                }
            }
        });
    }

    private void sendComplaint() {
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        ComplaintModel obj = new ComplaintModel(sp.getString("name",""),
                sp.getString("mobile",""),
                binding.etcomplaint.getText().toString(),
                stationId,formattedDate,stationName,stationPhone,sp.getString("uId","") );
        db = FirebaseFirestore.getInstance();
        db.collection("Complaint").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SendComplaint.this, "Complaint Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MyComplaints.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendComplaint.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
        finish();
    }
}