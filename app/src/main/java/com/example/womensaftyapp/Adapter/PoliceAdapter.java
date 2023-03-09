package com.example.womensaftyapp.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.womensaftyapp.AllPoliceStations;
import com.example.womensaftyapp.Emergencylist;
import com.example.womensaftyapp.Policemodel;
import com.example.womensaftyapp.SendComplaint;
import com.example.womensaftyapp.databinding.LayoutAllBinding;
import com.example.womensaftyapp.settings.Parentmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PoliceAdapter extends RecyclerView.Adapter<PoliceAdapter.MyviewHolder> {
    public List<Policemodel> HospList;
    LayoutAllBinding binding;

    @NonNull
    @Override
    public PoliceAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = LayoutAllBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PoliceAdapter.MyviewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull PoliceAdapter.MyviewHolder holder, int position) {
        Policemodel dm = HospList.get(position);
        holder.hname.setText(dm.getName());
        holder.haddress.setText(dm.getAddress());
        holder.hphone.setText(dm.getPhone());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = view.getRootView().getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                if (sp.getString("utype", "").equals("User")) {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    alertbox.setMessage("What to do?");
                    alertbox.setTitle("warning!!");
                    alertbox.setNeutralButton("Call police", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + dm.getPhone()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(intent);

                        }
                    });
                    alertbox.setPositiveButton("Locate Station", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (dm.getHlatitude() != "" && dm.getHlongitude() != null) {
                                String issue = "http://maps.google.com/maps?q=loc:" + dm.getHlatitude() + "," + dm.getHlongitude() + " (" + dm.getName() + ")";
                                locateLocation(issue, view);
                            } else {
                                Toast.makeText(view.getRootView().getContext(), "location not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertbox.setNegativeButton("Send Complaint", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sd = view.getContext().getSharedPreferences("police", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = sd.edit();
                            ed.putString("sname", dm.getName());
                            ed.putString("sphone", dm.getPhone());
                            ed.putString("sid", dm.getPin());
                            ed.putString("from", "adapter");
                            ed.commit();
                            Intent i = new Intent(view.getRootView().getContext(), SendComplaint.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);
                            dialog.dismiss();
                        }
                    });

                    alertbox.show();

                } else {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    alertbox.setMessage("Do you really wants to Remove this police station?");
                    alertbox.setTitle("Delete!!");

                    alertbox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteDepartment(dm.getPin(), view, holder.getAdapterPosition());

                        }
                    });
                    alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertbox.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return HospList.size();
    }

    private void locateLocation(String issue, View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(issue));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        view.getRootView().getContext().startActivity(intent);
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView hname, haddress, hphone, labeladdress;
        ConstraintLayout root;
        ImageView ddelete;

        public MyviewHolder(@NonNull LayoutAllBinding binding) {
            super(binding.getRoot());
            hname = binding.tvPatientName;
            root = binding.root;
            haddress = binding.tvPatientAddress;
            hphone = binding.tvPatientPhone;
            labeladdress = binding.labelAddress;
        }
    }

    private void deleteDepartment(String doc_name, View view, int adapterPosition) {
        //Log.d("@", "showData: Called")

        final ProgressDialog progressDoalog = new ProgressDialog(view.getRootView().getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(doc_name).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(adapterPosition);
                        Intent i = new Intent(view.getRootView().getContext(), AllPoliceStations.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        view.getRootView().getContext().startActivity(i);
                        Toast.makeText(view.getRootView().getContext(), " station removed successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getRootView().getContext(), "Technical error occured", Toast.LENGTH_SHORT).show();

                    }
                });

        progressDoalog.dismiss();

    }

}




