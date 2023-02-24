package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.Dashboard.UserDashboard;
import com.example.womensaftyapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginFun();
            }
        });
        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        if (sp.getString("utype", "").equals("User")) {
            startActivity(new Intent(SignInActivity.this, UserDashboard.class));
            finish();
        }
    }

    private void callLoginFun() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").whereEqualTo("pin", binding.loginpin.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                progressDoalog.dismiss();
                                Toast.makeText(SignInActivity.this, "Login pin not Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("utype", queryDocumentSnapshots.getDocuments().get(0).getString("utype"));
                                editor.putString("name", queryDocumentSnapshots.getDocuments().get(0).getString("name"));
                                editor.putString("mobile", queryDocumentSnapshots.getDocuments().get(0).getString("phone"));
                                editor.putString("uId", queryDocumentSnapshots.getDocuments().get(0).getId());
                                editor.commit();
                                progressDoalog.dismiss();
                                if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("User")) {
                                    startActivity(new Intent(SignInActivity.this, UserDashboard.class));
                                    finish();
                                }
                                else if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("Admin")) {
                                    startActivity(new Intent(SignInActivity.this, AdminHome.class));
                                    finish();
                                }
                                else if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("Police")) {
                                    startActivity(new Intent(SignInActivity.this, AdminHome.class));
                                    finish();
                                }
                            }

                        } catch (Exception e) {
                            //progressDoalog.dismiss();
                            Log.d("exception: ", e.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(SignInActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {

        finish();
        moveTaskToBack(true);
    }
}