package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.Adapter.ParentAdapter;
import com.example.womensaftyapp.databinding.ActivityEmergencylistBinding;
import com.example.womensaftyapp.settings.Parentmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Emergencylist extends AppCompatActivity {
    ActivityEmergencylistBinding binding;
    FirebaseFirestore db;
    ParentAdapter adapter = new ParentAdapter();
    List<Parentmodel> Hlist = new ArrayList();
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        progressDoalog = new ProgressDialog(Emergencylist.this);
        progressDoalog.setMessage("Adding Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        binding.rvEmergency.setLayoutManager(new LinearLayoutManager(this));

        showData();
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
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        Parentmodel obj = new Parentmodel(sp.getString("uId",""),binding.tvname.getText().toString(), binding.tvphone.getText().toString());
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
    private void showData() {

        //Log.d("@", "showData: Called")
        Hlist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        db.collection("Emergency").whereEqualTo("uid", sp.getString("uId",""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {

                                Hlist.add(new Parentmodel(
                                        queryDocumentSnapshots.getDocuments().get(i).getId(),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("name"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("phone")));
                            }
                            adapter.HospList = Hlist;
                            binding.rvEmergency.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No parents Available", Toast.LENGTH_SHORT).show();

                        }
                        progressDoalog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),UserHome.class));finish();
    }
}