package com.example.womensaftyapp.Police;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.womensaftyapp.Adapter.ComplaintAdapter;
import com.example.womensaftyapp.Dashboard.MyComplaints;
import com.example.womensaftyapp.Dashboard.UserDashboard;
import com.example.womensaftyapp.R;
import com.example.womensaftyapp.databinding.ActivityMyComplaintsBinding;
import com.example.womensaftyapp.databinding.ActivityRegisteredComplaintsBinding;
import com.example.womensaftyapp.settings.ComplaintModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisteredComplaints extends AppCompatActivity {
    ActivityRegisteredComplaintsBinding binding;
    FirebaseFirestore db;
    ComplaintAdapter adapter = new ComplaintAdapter();
    List<ComplaintModel> Hlist = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisteredComplaintsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager ll=new LinearLayoutManager(this);
        binding.rvComplaint.setLayoutManager(ll);
        showData();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), PoliceHome.class));finish();
    }
    private void showData() {
        ProgressDialog progressDoalog = new ProgressDialog(RegisteredComplaints.this);
        progressDoalog.setMessage("getting Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        //Log.d("@", "showData: Called")
        Hlist.clear();
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        db.collection("Complaint").
                whereEqualTo("stationId",sp.getString("uId",""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                Hlist.add(new ComplaintModel(
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uname"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uphone"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("complaint"),
                                        queryDocumentSnapshots.getDocuments().get(i).getId(),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("complaintDate"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("stationName"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("stationPhone"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uid")));
                            }
                            adapter.HospList = Hlist;
                            binding.rvComplaint.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No Complaints Available", Toast.LENGTH_SHORT).show();

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