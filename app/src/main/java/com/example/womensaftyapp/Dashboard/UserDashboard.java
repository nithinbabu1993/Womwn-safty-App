package com.example.womensaftyapp.Dashboard;

import static java.lang.Double.parseDouble;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.womensaftyapp.AllPoliceStations;
import com.example.womensaftyapp.Emergencylist;
import com.example.womensaftyapp.Policemodel;
import com.example.womensaftyapp.R;
import com.example.womensaftyapp.SendComplaint;
import com.example.womensaftyapp.SignInActivity;
import com.example.womensaftyapp.databinding.ActivityUserDashboardBinding;
import com.example.womensaftyapp.settings.LocationMonitoringService;
import com.example.womensaftyapp.settings.RepoprtModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserDashboard extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    String latitude, longitude;
    Boolean b = false;
    FirebaseFirestore db;
    ProgressDialog progressDoalog;
    List<Policemodel> police = new ArrayList();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUserDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarUserDashboard.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.name);
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        nav_user.setText(sp.getString("name", ""));
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = FirebaseFirestore.getInstance();
        progressDoalog = new ProgressDialog(UserDashboard.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                        //Toast.makeText(ChooseActivity.this, latitude + longitude + "", Toast.LENGTH_SHORT).show();
                        if (latitude != null && longitude != null) {
//                            LatLng latLng = new LatLng(parseDouble(latitude), parseDouble(longitude));
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
//                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(latLng)
//                                    .title("you are Here")
//                                    .icon(BitmapDescriptorFactory
//                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();
                            if (b == false) {
                                mMap.clear();
                                showData();
                                b = true;
                            }

                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));


        ImageView sos = findViewById(R.id.sos);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParents();
                Toast.makeText(UserDashboard.this, "Sending Sms to all police station and your parents list", Toast.LENGTH_SHORT).show();
            }
        });
        Button emergency = findViewById(R.id.emergency);
        emergency.setVisibility(View.GONE);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboard.this, Emergencylist.class));
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//
        if (id == R.id.nav_emergency) {
            startActivity(new Intent(UserDashboard.this, Emergencylist.class));
            finish();
        }
        if (id == R.id.nav_police) {
            startActivity(new Intent(UserDashboard.this, AllPoliceStations.class));
            finish();
        }
        if (id == R.id.nav_sos) {
            startActivity(new Intent(UserDashboard.this, SOSReports.class));
            finish();
        }
        if (id == R.id.nav_complaints) {
            startActivity(new Intent(UserDashboard.this, MyComplaints.class));
            finish();
        }

        if (id == R.id.nav_logout) {
            SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("utype", "");
            editor.commit();

            startActivity(new Intent(UserDashboard.this, SignInActivity.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }

    private void showData() {
        //Log.d("@", "showData: Called")

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").whereEqualTo("utype", "Police")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int i;
                        for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {

                            try {
                                if (latitude != null && longitude != null) {
                                    float[] results = new float[1];
                                    Location.distanceBetween(parseDouble(latitude), parseDouble(longitude),
                                            parseDouble(queryDocumentSnapshots.getDocuments().get(i).getString("hlatitude")), parseDouble(queryDocumentSnapshots.getDocuments().get(i).getString("hlongitude")),
                                            results);
                                    float km = results[0] / 1000;
                                    police.clear();
                                    police.add(new Policemodel(queryDocumentSnapshots.getDocuments().get(i).getId(), "", queryDocumentSnapshots.getDocuments().get(i).getString("phone"), "", "", "", ""));
                                    LatLng latLng = new LatLng(Double.parseDouble(queryDocumentSnapshots.getDocuments().get(i).getString("hlatitude")), Double.parseDouble(queryDocumentSnapshots.getDocuments().get(i).getString("hlongitude")));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    //  mMap.clear();

                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title("Station Name:\t" + queryDocumentSnapshots.getDocuments().get(i).getString("name")
                                                    + ":" + queryDocumentSnapshots.getDocuments().get(i).getString("address")
                                                    + "\t:\tDistance from you:" + km + "\t:\t\tStation Phone:"
                                                    + queryDocumentSnapshots.getDocuments().get(i).getString("phone") +
                                                    ":Station ID:" + queryDocumentSnapshots.getDocuments().get(i).getId())
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();

                                }
                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    public void onInfoWindowClick(Marker marker) {
                                        SharedPreferences sd = getSharedPreferences("police", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor ed = sd.edit();
                                        ed.putString("policestation", marker.getTitle());
                                        ed.putString("from", "dash");
                                        ed.commit();
                                        startActivity(new Intent(getApplicationContext(), SendComplaint.class));
                                        finish();
                                    }
                                });

                            } catch (Exception e) {
                            }

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

    private void getParents() {
        //Log.d("@", "showData: Called")

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        db.collection("Emergency").whereEqualTo("uid", sp.getString("uId", ""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                police.add(new Policemodel("", "", queryDocumentSnapshots.getDocuments().get(i).getString("phone"), "", "", "", ""));
                            }
                            String issue = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Please Help me. I am in trouble.Track me in google map with this link" + ")";
//
                            if (police.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "No parents/police available Available", Toast.LENGTH_SHORT).show();

                            } else {
                                for (i = 0; i < police.size(); i++) {
                                    if(police.get(i).getPin()!=""){
                                        reportIssue(issue,police.get(i).getPin());
                                    }
                                    sendSMS(police.get(i).getPhone(), issue);
                                    Log.d("@@", "Sending sms to : "+police.get(i).getPhone());
                                }

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "No parents Available", Toast.LENGTH_SHORT).show();

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

    private void reportIssue(String issue, String pin) {
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        RepoprtModel obj = new RepoprtModel(pin, sp.getString("uId", ""), issue, formattedDate, sp.getString("name", ""), sp.getString("mobile", ""));
        db = FirebaseFirestore.getInstance();
        db.collection("Reportedissues").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(UserDashboard.this, "Issue Registered successfully", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(),AddPolice.class));finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserDashboard.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}