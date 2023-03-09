package com.example.womensaftyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.womensaftyapp.databinding.ActivityForgotPinBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class ForgotPinActivity extends AppCompatActivity {
    ActivityForgotPinBinding binding;
    FirebaseFirestore db;
    String Otp;
    SharedPreferences sp;
    String type="",name,mob,address,utype,plat,plon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
       binding.rgb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch(checkedId){
                   case R.id.rbpolice: type=binding.rbpolice.getText().toString();break;
                   case R.id.rbuser:type=binding.rbuser.getText().toString();break;
                   case R.id.rbparent:type=binding.rbparent.getText().toString();break;
                   default:
               }
           }
       });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("")){
                    Toast.makeText(ForgotPinActivity.this, "please choose User Type", Toast.LENGTH_SHORT).show();

                }
               else if (binding.tvphone.getText().toString().isEmpty()) {
                    binding.tvphone.setError("Enter phone Number");
                } else {
                    PerformPhoneNumberValidity(binding.tvphone.getText().toString());
                }
            }
        });
        binding.btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvotp.getText().toString().isEmpty()) {
                    binding.tvphone.setError("Enter Otp");
                } else {
                    if (Otp.equals(binding.tvotp.getText().toString())) {
                        binding.tvotp.setVisibility(View.GONE);
                        binding.btnverify.setVisibility(View.GONE);
                        binding.tvnewpin.setVisibility(View.VISIBLE);
                        binding.btnupdatePin.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ForgotPinActivity.this, "Otp does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.btnupdatePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvnewpin.getText().toString().isEmpty()) {
                    binding.tvnewpin.setError("Enter New login Pin");
                } else {
                    updateloginPin();
                }
            }
        });
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void PerformPhoneNumberValidity(String phone) {
        Log.d("@@", type);
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").whereEqualTo("phone", phone)
                .whereEqualTo("utype",type)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDoalog.dismiss();
                        if(queryDocumentSnapshots.size()>0) {
                            type=queryDocumentSnapshots.getDocuments().get(0).getId();
                            name=queryDocumentSnapshots.getDocuments().get(0).getString("name");
                            mob=queryDocumentSnapshots.getDocuments().get(0).getString("phone");
                            address=queryDocumentSnapshots.getDocuments().get(0).getString("address");
                            utype=queryDocumentSnapshots.getDocuments().get(0).getString("utype");
                            plat=queryDocumentSnapshots.getDocuments().get(0).getString("hlatitude");
                            plon=queryDocumentSnapshots.getDocuments().get(0).getString("hlongitude");
                            try {
                                Random rand = new Random();
                                Otp = String.format("%04d", rand.nextInt(100000));
                                sendSMS(phone, "Your login pin Reset Otp is:" + Otp);
                                binding.tvphone.setVisibility(View.GONE);
                                binding.button.setVisibility(View.GONE);
                                binding.tvotp.setVisibility(View.VISIBLE);
                                binding.btnverify.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                //progressDoalog.dismiss();
                                Log.d("exception: ", e.toString());
                            }
                        }
                        else{
                            Toast.makeText(ForgotPinActivity.this, "Mobile number not registered for this user", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(ForgotPinActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateloginPin() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        Policemodel obj = new Policemodel(binding.tvnewpin.getText().toString(), name, mob, address,utype,plat,plon);  db.collection("User").document(type).set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDoalog.dismiss();
                Toast.makeText(ForgotPinActivity.this, "Pin updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ForgotPinActivity.this, SignInActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDoalog.dismiss();
                Toast.makeText(ForgotPinActivity.this, "updation failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPinActivity.this, SignInActivity.class));
        finish();
    }
}