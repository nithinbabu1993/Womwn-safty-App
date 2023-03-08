package com.example.womensaftyapp;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.womensaftyapp.Adapter.IssueAdapter;
import com.example.womensaftyapp.Dashboard.SOSReports;
import com.example.womensaftyapp.databinding.ActivityAdminHomeBinding;
import com.example.womensaftyapp.settings.RepoprtModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    ActivityAdminHomeBinding binding;
    FirebaseFirestore db;
    IssueAdapter adapter = new IssueAdapter();
    List<RepoprtModel> Hlist = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager ll=new LinearLayoutManager(this);
        binding.rvIssues.setLayoutManager(ll);
        showData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.tv_addpolice:
                startActivity(new Intent(AdminHome.this, AddPolice.class));
                finish();
                return true;
            case R.id.tv_viewpolice:
                startActivity(new Intent(AdminHome.this, AllPoliceStations.class));
                finish();
                return true;

            case R.id.tv_appusers:
                startActivity(new Intent(AdminHome.this, AppUsers.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertbox=new AlertDialog.Builder(AdminHome.this);
        alertbox.setMessage("Do you really wants to logout from this app?");
        alertbox.setTitle("Logout!!");

        alertbox.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("utype", "");
                editor.commit();
                startActivity(new Intent(AdminHome.this, SignInActivity.class));
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
    private void showData() {
        ProgressDialog progressDoalog = new ProgressDialog(AdminHome.this);
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
                            binding.rvIssues.setAdapter(adapter);
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