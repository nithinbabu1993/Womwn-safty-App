package com.example.womensaftyapp.Police;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.Adapter.IssueAdapter;
import com.example.womensaftyapp.AdminHome;
import com.example.womensaftyapp.R;
import com.example.womensaftyapp.SignInActivity;
import com.example.womensaftyapp.databinding.ActivityPoliceHomeBinding;
import com.example.womensaftyapp.settings.RepoprtModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PoliceHome extends AppCompatActivity {
    ActivityPoliceHomeBinding binding;
    FirebaseFirestore db;
    IssueAdapter adapter = new IssueAdapter();
    List<RepoprtModel> Hlist = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPoliceHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager ll = new LinearLayoutManager(this);
        binding.rvRissues.setLayoutManager(ll);
        showData();
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisteredComplaints.class));
                finish();
            }
        });
    }

    private void showData() {
        ProgressDialog progressDoalog = new ProgressDialog(PoliceHome.this);
        progressDoalog.setMessage("getting Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        //Log.d("@", "showData: Called")
        Hlist.clear();
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        db.collection("Reportedissues")
                .whereEqualTo("rid",sp.getString("uId",""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                Hlist.add(new RepoprtModel(
                                        queryDocumentSnapshots.getDocuments().get(i).getId(),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uid"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("issue"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("rdate"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uname"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uphone")));
                            }
                            adapter.HospList = Hlist;
                            binding.rvRissues.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "No issues Available", Toast.LENGTH_SHORT).show();

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

    public void onBackPressed() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(PoliceHome.this);
        alertbox.setMessage("Do you really wants to logout from this app?");
        alertbox.setTitle("Logout!!");

        alertbox.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("utype", "");
                editor.commit();
                startActivity(new Intent(PoliceHome.this, SignInActivity.class));
                finish();

            }
        });
        alertbox.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertbox.show();

    }
}