package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRegisterFun();
            }
        });
    }
    private void callRegisterFun() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").
                whereEqualTo("pin", binding.lpin.getText().toString()).
                whereEqualTo("phone", binding.uphone.getText().toString()).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            userRegistration(binding.lpin.getText().toString());
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(SignUpActivity.this, "This pin/phone number Already Taken", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(SignUpActivity.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void userRegistration(String number) {
        UserModel obj = new UserModel(number, binding.uname.getText().toString(), binding.uphone.getText().toString(),"User","");
        db = FirebaseFirestore.getInstance();
        db.collection("User").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SignUpActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Creation failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
        finish();
    }
}