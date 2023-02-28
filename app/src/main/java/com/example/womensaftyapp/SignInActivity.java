package com.example.womensaftyapp;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission_group.CAMERA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.Dashboard.UserDashboard;
import com.example.womensaftyapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;
    FirebaseFirestore db;

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED&& result2== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CALL_PHONE,SEND_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        Toast.makeText(this, "Permission Granted, Now you can access location data and sms,call phone.", Toast.LENGTH_SHORT).show();

                    else {
                        Toast.makeText(this, "Permission Denied, You cannot access location data and sms,call phone.", Toast.LENGTH_SHORT).show();


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to All the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CALL_PHONE,SEND_SMS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SignInActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
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
        if (!checkPermission()) {
            requestPermission();
        } else {
           // Toast.makeText(this, "Permission already granted.", Toast.LENGTH_SHORT).show();
        }
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