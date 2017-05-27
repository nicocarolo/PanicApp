package com.example.nico.panicapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.Manifest;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 0;
    private static final int PERMISSION_RECORD_AUDIO = 1;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 3;
    private static final int AUDIO_SECONDS_DURATION = 15;
    private String FIREBASE_URL = "https://panicapp-b4790.firebaseio.com/";
    private String FIREBASE_CHILD = "test";
    private StorageReference mStorageRef;
    FirebaseDatabase database;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationManager mLocationManager;

    private MediaRecorder mRecorder;
    private String mFileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO},
                    PERMISSION_RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    public void createPoliceAlert(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("PoliceAlert");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        Map<String, String> alertData = new HashMap<>();
        if (mLastLocation != null){
            alertData.put("Latitude", String.valueOf(mLastLocation.getLatitude()));
            alertData.put("Longitude", String.valueOf(mLastLocation.getLongitude()));
        } else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        alertData.put("Date", date);

        // Set the child's data to the value passed in from the text box.
        childRef.setValue(alertData);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/Police-" + childRef.getKey() + ".3gp";
        startRecording(mFileName);
        new CountDownTimer(AUDIO_SECONDS_DURATION*1000, AUDIO_SECONDS_DURATION*1000) {//countdown Period =5000
            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                stopRecording();
                uploadFile(mFileName);
                Toast toast = Toast.makeText(getApplicationContext(), "Alerta Policial" + " ("
                        + String.valueOf(mLastLocation.getLatitude())
                        + " ; "
                        + String.valueOf(mLastLocation.getLongitude())
                        + ")", Toast.LENGTH_LONG);
                toast.show();
            }

        }.start();
    }

    public void createMedicAlert(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("MedicAlert");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        Map<String, String> alertData = new HashMap<>();
        if (mLastLocation != null){
            alertData.put("Latitude", String.valueOf(mLastLocation.getLatitude()));
            alertData.put("Longitude", String.valueOf(mLastLocation.getLongitude()));
        } else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        alertData.put("Date", date);

        // Set the child's data to the value passed in from the text box.
        childRef.setValue(alertData);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/Medic-" + childRef.getKey() + ".3gp";
        startRecording(mFileName);
        new CountDownTimer(AUDIO_SECONDS_DURATION*1000, AUDIO_SECONDS_DURATION*1000) {//countdown Period =5000
            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                stopRecording();
                uploadFile(mFileName);
                Toast toast = Toast.makeText(getApplicationContext(), "Alerta Medica" + " ("
                        + String.valueOf(mLastLocation.getLatitude())
                        + " ; "
                        + String.valueOf(mLastLocation.getLongitude())
                        + ")", Toast.LENGTH_LONG);
                toast.show();
            }

        }.start();

    }

    public void createSexAlert(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("SexAlert");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        Map<String, String> alertData = new HashMap<>();
        if (mLastLocation != null){
            alertData.put("Latitude", String.valueOf(mLastLocation.getLatitude()));
            alertData.put("Longitude", String.valueOf(mLastLocation.getLongitude()));
        } else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        alertData.put("Date", date);

        // Set the child's data to the value passed in from the text box.
        childRef.setValue(alertData);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/Sex-" + childRef.getKey() + ".3gp";
        startRecording(mFileName);
        new CountDownTimer(AUDIO_SECONDS_DURATION*1000, AUDIO_SECONDS_DURATION*1000) {//countdown Period =5000
            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                stopRecording();
                uploadFile(mFileName);
                Toast toast = Toast.makeText(getApplicationContext(), "Alerta Genero" + " ("
                        + String.valueOf(mLastLocation.getLatitude())
                        + " ; "
                        + String.valueOf(mLastLocation.getLongitude())
                        + ")", Toast.LENGTH_LONG);
                toast.show();
            }

        }.start();

    }

    public void createBomberAlert(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("BomberAlert");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        Map<String, String> alertData = new HashMap<>();
        if (mLastLocation != null){
            alertData.put("Latitude", String.valueOf(mLastLocation.getLatitude()));
            alertData.put("Longitude", String.valueOf(mLastLocation.getLongitude()));
        } else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        alertData.put("Date", date);

        // Set the child's data to the value passed in from the text box.
        childRef.setValue(alertData);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/Bomber-" + childRef.getKey() + ".3gp";
        startRecording(mFileName);
        new CountDownTimer(AUDIO_SECONDS_DURATION*1000, AUDIO_SECONDS_DURATION*1000) {//countdown Period =5000
            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                stopRecording();
                uploadFile(mFileName);
                Toast toast = Toast.makeText(getApplicationContext(), "Alerta Bomberos" + " ("
                        + String.valueOf(mLastLocation.getLatitude())
                        + " ; "
                        + String.valueOf(mLastLocation.getLongitude())
                        + ")", Toast.LENGTH_LONG);
                toast.show();
            }

        }.start();

    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    mLastLocation = location;
                    Log.d("Getting Location", location.toString());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // getting GPS status
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            // getting network status
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
            } else{
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//
                }
//
            }
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                    mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
            case PERMISSION_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your record!", Toast.LENGTH_SHORT).show();
                }

                break;
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your storage!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void startRecording(String fileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mRecorder.setMaxDuration(60000);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void uploadFile(String fileName){
        String[] filenameSplitted = fileName.split("/");

        StorageReference audiosRef = mStorageRef.child("audio/" + filenameSplitted[filenameSplitted.length -1]);
        Uri file = Uri.fromFile(new File(fileName));
        UploadTask uploadTask = audiosRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

}
