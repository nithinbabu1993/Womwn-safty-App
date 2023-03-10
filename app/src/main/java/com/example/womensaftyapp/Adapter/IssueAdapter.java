package com.example.womensaftyapp.Adapter;

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
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.womensaftyapp.AdminHome;
import com.example.womensaftyapp.AppUsers;
import com.example.womensaftyapp.Dashboard.SOSReports;
import com.example.womensaftyapp.Emergencylist;
import com.example.womensaftyapp.Parent.ParentHome;
import com.example.womensaftyapp.Police.PoliceHome;
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
        SharedPreferences sp = holder.itemView.getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        if (sp.getString("utype", "").equals("User")) {
          holder.to.setVisibility(View.VISIBLE);
          holder.lbl.setVisibility(View.VISIBLE);
            holder.to.setText(dm.getUtype());
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Locate this issue?");
                alertbox.setTitle("locate!!");

                alertbox.setPositiveButton("Locate Spot", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        locateLocation(dm.getIssue(), view, holder.getAdapterPosition());

                    }
                });
                alertbox.setNegativeButton("Live Tracking", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                alertbox.setNeutralButton("Delete Issue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDepartment(dm.getRid(),view, holder.getAdapterPosition());
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
        TextView hname, haddress, hphone,labeladdress,to,lbl;
        ConstraintLayout root;
        ImageView ddelete;

        public MyviewHolder(@NonNull LayoutIssueBinding binding) {
            super(binding.getRoot());
            hname = binding.tvPatientName;
            lbl = binding.labelTo;
            to = binding.tvutype;
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
    private void deleteDepartment(String doc_name, View view, int adapterPosition) {
        //Log.d("@", "showData: Called")

        final ProgressDialog progressDoalog = new ProgressDialog(view.getRootView().getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Reportedissues").document(doc_name).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(adapterPosition);
                        SharedPreferences sp = view.getRootView().getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                        Toast.makeText(view.getRootView().getContext(), " issue removed successfully", Toast.LENGTH_SHORT).show();

                        if (sp.getString("utype", "").equals("User")) {
                            Intent i = new Intent(view.getRootView().getContext(), SOSReports.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);

                        }else if(sp.getString("utype", "").equals("Police")) {
                            Intent i = new Intent(view.getRootView().getContext(), PoliceHome.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);
                        }
                        else if(sp.getString("utype", "").equals("Admin")) {
                            Intent i = new Intent(view.getRootView().getContext(), AdminHome.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);
                        }
                        else if(sp.getString("utype", "").equals("Parent")) {
                            Intent i = new Intent(view.getRootView().getContext(), ParentHome.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(i);
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





