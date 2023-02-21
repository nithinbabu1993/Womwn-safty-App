package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.databinding.ActivityEmergencylistBinding;
import com.example.womensaftyapp.settings.Parentmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Emergencylist extends AppCompatActivity {
    ActivityEmergencylistBinding binding;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        binding.btnAddparents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvname.getText().toString().isEmpty()) {
                    binding.tvname.setError("Enter  Name");
                } else if (binding.tvphone.getText().toString().isEmpty()) {
                    binding.tvphone.setError("Enter  phone number");
                } else {
                    checkphoneExist();
                }
            }
        });
    }
    private void checkphoneExist() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("Emergency").whereEqualTo("phone", binding.tvphone.getText().toString()).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            RegisterParent();
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(Emergencylist.this, "This phone number Already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(Emergencylist.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void RegisterParent() {
        Parentmodel obj = new Parentmodel(binding.tvname.getText().toString(), binding.tvphone.getText().toString());
        db = FirebaseFirestore.getInstance();
        db.collection("Emergency").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Emergencylist.this, "Parent Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Emergencylist.class));finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Emergencylist.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),UserHome.class));finish();
    }
}