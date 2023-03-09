package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.womensaftyapp.Adapter.ParentAdapter;
import com.example.womensaftyapp.Adapter.UserAdapter;
import com.example.womensaftyapp.databinding.ActivityAppUsersBinding;
import com.example.womensaftyapp.databinding.ActivityEmergencylistBinding;
import com.example.womensaftyapp.settings.Parentmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppUsers extends AppCompatActivity {
    ActivityAppUsersBinding binding;
    FirebaseFirestore db;
    UserAdapter adapter = new UserAdapter();
    List<UserModel> Hlist = new ArrayList();
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAppUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDoalog = new ProgressDialog(AppUsers.this);
        progressDoalog.setMessage("getting Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));

        showData();
    }
    private void showData() {

        //Log.d("@", "showData: Called")
        Hlist.clear();
         db = FirebaseFirestore.getInstance();
        db.collection("User")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                Hlist.add(new UserModel(
                                        queryDocumentSnapshots.getDocuments().get(i).getId(),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("name"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("phone"),"",""));
                            }
                            adapter.HospList = Hlist;
                            binding.rvUsers.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No users Available", Toast.LENGTH_SHORT).show();

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
        startActivity(new Intent(getApplicationContext(),AdminHome.class));finish();
    }
}