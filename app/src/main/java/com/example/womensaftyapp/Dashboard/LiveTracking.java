package com.example.womensaftyapp.Dashboard;

import static java.lang.Double.parseDouble;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.womensaftyapp.Parent.ParentHome;
import com.example.womensaftyapp.Police.PoliceHome;
import com.example.womensaftyapp.R;
import com.example.womensaftyapp.databinding.ActivityLiveTrackingBinding;
import com.example.womensaftyapp.settings.ComplaintModel;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LiveTracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityLiveTrackingBinding binding;
    Bundle bid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLiveTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bid = getIntent().getExtras();
        binding.floatingMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(parseDouble(ComplaintModel.latitude),
                        parseDouble(ComplaintModel.longitude));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                showData();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showData();
    }

    private void showData() {

        //Log.d("@", "showData: Called")

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Reportedissues").document(bid.getString("bid")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    if (documentSnapshot.getString("liveLatitude") != null && documentSnapshot.getString("liveLongitude") != null) {

                        LatLng latLng = new LatLng(parseDouble(documentSnapshot.getString("liveLatitude")),
                                parseDouble(documentSnapshot.getString("liveLongitude")));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                          mMap.clear();
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Your Child is here")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            public void onInfoWindowClick(Marker marker) {
                                String issue = "http://maps.google.com/maps?q=loc:" + documentSnapshot.getString("liveLatitude") + "," + documentSnapshot.getString("liveLongitude") + " (Victim Located)";

                                locateLocation(issue);
                            }
                        });
                    } else {
                        Toast.makeText(LiveTracking.this, "Child location not found", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    //
    private void locateLocation(String issue) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(issue));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        if (sp.getString("utype", "").equals("User")) {
            startActivity(new Intent(getApplicationContext(), UserDashboard.class));
            finish();
        } else if (sp.getString("utype", "").equals("Police")) {
            startActivity(new Intent(getApplicationContext(), PoliceHome.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), ParentHome.class));
            finish();
        }

    }
}