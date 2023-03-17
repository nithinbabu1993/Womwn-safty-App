package com.example.womensaftyapp.settings;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.womensaftyapp.Dashboard.SOSReports;
import com.example.womensaftyapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;



public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    static GoogleApiClient mLocationClient = null;
    LocationRequest mLocationRequest = new LocationRequest();
    String lat1, lon1, type;

    long oldTime = 0;
    private Timer apiTimer, timer1;
    Boolean b=false;
    FirebaseFirestore db;
    private TimerTask apiTask, timerTask1;
    String driver_Id, oldlat = null, oldlon = null;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        db = FirebaseFirestore.getInstance();
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient.connect();

        startTimer1();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            Notification notification = new NotificationCompat.Builder(LocationMonitoringService.this, CHANNEL_ID)
                    .setContentTitle("MyRide App is tracking you")
                    .setContentText("keep signed in")
                    .setSmallIcon(R.drawable.splash)
                    .setSound(null)
                    .build();
            startForeground(1, notification);
        }
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null) {
            Log.d(TAG, "== location != null");
            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

        }

    }

    private void sendMessageToUI(String lat, String lng) {
        lat1 = lat;
        lon1 = lng;
        ComplaintModel.latitude=lat;
        ComplaintModel.longitude=lng;
        Log.d(TAG, "Sending info...");
      //  Toast.makeText(this, lat + lng + "", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }


    public void startTimer1() {
        //set a new Timer
        timer1 = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask1();
        //Date currentTime = Calendar.getInstance().getTime();
        //schedule the timer, to wake up every 5 minute

        timer1.schedule(timerTask1, 10000, 10000); //
    }

    public void initializeTimerTask1() {
        timerTask1 = new TimerTask() {
            public void run() {

                Log.i("bgloc", "in timer ++++  " + lat1 + lon1);
                SharedPreferences jorney=getSharedPreferences("help", Context.MODE_PRIVATE);
                if (jorney.getString("status", "").equals("1")) {
                    if(b==false) {
                        startApiTimer();
                        b=true;
                    }
                } else {
                    Log.d("bgloc", "Location sharing stopped");

                }

            }
        };
    }
    public void startApiTimer() {
        //set a new Timer
        apiTimer = new Timer();
        //initialize the TimerTask's job
        showDialogue();
        //Date currentTime = Calendar.getInstance().getTime();
        //schedule the timer, to wake up every 60 seconds
        apiTimer.schedule(apiTask, 60000, 60000); //
    }
    private void showDialogue() {
        apiTask = new TimerTask() {
            public void run() {
                b=false;
                Log.d("bgloc", "Api called ");
                // Toast.makeText(LocationMonitoringService.this, "counter called", Toast.LENGTH_SHORT).show();
                driverUpdation();
//
            }
        };

//
    }
    private void driverUpdation() {
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
                                db.collection("Reportedissues").document( queryDocumentSnapshots.getDocuments().get(i).getId()).
                                        update("liveLatitude",lat1,"liveLongitude",lon1)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(LocationMonitoringService.this, "victim location updated", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("bgloc", "onFailure: ");
                                                Toast.makeText(LocationMonitoringService.this, e+"updation failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }else{
                            //Toast.makeText(getApplicationContext(), "No issues Available", Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        //calling broadcastreceiver to restart service

        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

        //stoptimertask();
    }

//    private void driverUpdation() {
//        SharedPreferences sharedPreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
//        Apiinterface apiinterface = Apiclient.getClient().create(Apiinterface.class);
//        Call<login_model> call = apiinterface.locupdate("LocUpdateCommon", sharedPreferences.getString("uid",""),sharedPreferences.getString("utype",""),lat1,lon1);
//        call.enqueue(new Callback<login_model>() {
//            @Override
//            public void onResponse(Call<login_model> call, Response<login_model> response) {
//               // Toast.makeText(LocationMonitoringService.this, "Loc Updated", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<login_model> call, Throwable t) {
//                Toast.makeText(LocationMonitoringService.this, t+"", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}