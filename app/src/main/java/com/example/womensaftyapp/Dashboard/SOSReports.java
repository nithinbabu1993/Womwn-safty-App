package com.example.womensaftyapp.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.womensaftyapp.Adapter.IssueAdapter;
import com.example.womensaftyapp.Adapter.PoliceAdapter;
import com.example.womensaftyapp.AllPoliceStations;
import com.example.womensaftyapp.Policemodel;
import com.example.womensaftyapp.R;
import com.example.womensaftyapp.databinding.ActivitySosreportsBinding;
import com.example.womensaftyapp.settings.RepoprtModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SOSReports extends AppCompatActivity {
ActivitySosreportsBinding binding;
    FirebaseFirestore db;
    IssueAdapter adapter = new IssueAdapter();
    List<RepoprtModel> Hlist = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySosreportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager ll=new LinearLayoutManager(this);
        binding.rvIssue.setLayoutManager(ll);
        showData();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),UserDashboard.class));finish();
    }
    private void showData() {
        ProgressDialog progressDoalog = new ProgressDialog(SOSReports.this);
        progressDoalog.setMessage("getting Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        //Log.d("@", "showData: Called")
        Hlist.clear();
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        db.collection("Reportedissues").whereEqualTo("uid",sp.getString("uId",""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
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
                            binding.rvIssue.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else{
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
}