package com.example.womensaftyapp.Adapter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.womensaftyapp.AppUsers;
import com.example.womensaftyapp.UserModel;
import com.example.womensaftyapp.databinding.LayoutAllBinding;
import com.example.womensaftyapp.databinding.LayoutIssueBinding;
import com.example.womensaftyapp.settings.RepoprtModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.MyviewHolder> {
    public List<RepoprtModel> HospList;
    LayoutIssueBinding binding;

    @NonNull
    @Override
    public IssueAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = LayoutIssueBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new IssueAdapter.MyviewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull IssueAdapter.MyviewHolder holder, int position) {
        RepoprtModel dm = HospList.get(position);
        holder.hname.setText(dm.getUname());
        holder.haddress.setText(dm.getIssue());
        holder.hphone.setText(dm.getRdate());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Locate this issue?");
                alertbox.setTitle("locate!!");

                alertbox.setPositiveButton("Locate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        locateLocation(dm.getIssue(), view, holder.getAdapterPosition());

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
        TextView hname, haddress, hphone,labeladdress;
        ConstraintLayout root;
        ImageView ddelete;

        public MyviewHolder(@NonNull LayoutIssueBinding binding) {
            super(binding.getRoot());
            hname = binding.tvPatientName;
            root = binding.root;
            haddress = binding.tvPatientAddress;
            hphone = binding.tvPatientPhone;
            labeladdress = binding.labelAddress;
        }
    }

    private void locateLocation(String issue, View view, int adapterPosition) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(issue));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        view.getRootView().getContext().startActivity(intent);
    }
}





