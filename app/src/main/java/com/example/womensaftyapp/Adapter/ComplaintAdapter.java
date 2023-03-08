package com.example.womensaftyapp.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.womensaftyapp.Dashboard.MyComplaints;
import com.example.womensaftyapp.Police.RegisteredComplaints;
import com.example.womensaftyapp.Policemodel;
import com.example.womensaftyapp.SendComplaint;
import com.example.womensaftyapp.databinding.LayoutAllBinding;
import com.example.womensaftyapp.databinding.LayoutComplaintBinding;
import com.example.womensaftyapp.settings.ComplaintModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.MyviewHolder> {
    public List<ComplaintModel> HospList;
    LayoutComplaintBinding binding;

    @NonNull
    @Override
    public ComplaintAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = LayoutComplaintBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ComplaintAdapter.MyviewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintAdapter.MyviewHolder holder, int position) {
        ComplaintModel dm = HospList.get(position);
        holder.sname.setText(dm.getStationName());
        holder.sphone.setText(dm.getStationPhone());
        holder.complaint.setText(dm.getComplaint());
        holder.uname.setText(dm.getUname());
        holder.uphone.setText(dm.getUphone());
        holder.cdate.setText(dm.getComplaintDate());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Do you really wants to Remove this complaint from this police station?");
                alertbox.setTitle("Delete!!");

                alertbox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDepartment(dm.getStationId(), view, holder.getAdapterPosition());

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

        });
    }

    @Override
    public int getItemCount() {
        return HospList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView sname, sphone, complaint, uname, uphone,cdate;
        ConstraintLayout root;
        ImageView ddelete;

        public MyviewHolder(@NonNull LayoutComplaintBinding binding) {
            super(binding.getRoot());
            sname = binding.sname;
            root = binding.root;
            sphone = binding.sphone;
            complaint = binding.complaint;
            uname = binding.uname;
            uphone = binding.uphone;
            cdate = binding.cdate;
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
        db.collection("Complaint").document(doc_name).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SharedPreferences sp = view.getRootView().getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                        if (sp.getString("utype", "").equals("User")) {
                            notifyItemChanged(adapterPosition);
                            Intent i = new Intent(view.getRootView().getContext(), MyComplaints.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);
                            Toast.makeText(view.getRootView().getContext(), " Complaint removed successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            notifyItemChanged(adapterPosition);
                            Intent i = new Intent(view.getRootView().getContext(), RegisteredComplaints.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);
                            Toast.makeText(view.getRootView().getContext(), " Complaint removed successfully", Toast.LENGTH_SHORT).show();

                        }
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
