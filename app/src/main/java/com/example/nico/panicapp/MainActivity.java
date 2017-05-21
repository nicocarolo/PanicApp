package com.example.nico.panicapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 0;
    private static final int PERMISSION_RECORD_AUDIO = 1;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private String FIREBASE_URL = "https://panicapp-b4790.firebaseio.com/";
    private String FIREBASE_CHILD = "test";
    FirebaseDatabase database;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private MediaRecorder mRecorder;
    private String mFileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Firebase.setAndroidContext(this);
//        firebase = new Firebase(FIREBASE_URL).child(FIREBASE_CHILD);

        // Connect to the Firebase database
        database = FirebaseDatabase.getInstance();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
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

    public void createPoliceAlarm(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("PoliceAlarm");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        Map<String, String> alarmData = new HashMap<>();
        alarmData.put("Latitude", String.valueOf(mLastLocation.getLatitude()));
        alarmData.put("Longitude", String.valueOf(mLastLocation.getLongitude()));

        // Set the child's data to the value passed in from the text box.
        childRef.setValue(alarmData);
        Context context = getApplicationContext();
        CharSequence text = "Alarma Policial";
        int duration = Toast.LENGTH_SHORT;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/Police-" + childRef.getKey() + ".3gp";
        startRecording(mFileName);
        new CountDownTimer(60000, 60000) {//countdown Period =5000
            @Override
            public void onTick(long millisUntilFinished) {
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(getApplicationContext(), "seconds remaining: " + millisUntilFinished / 6000, duration);
//                toast.show();
            }

            public void onFinish() {
                stopRecording();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), "Alarma Policial" + " ("
                        + String.valueOf(mLastLocation.getLatitude())
                        + " ; "
                        + String.valueOf(mLastLocation.getLongitude())
                        + ")", duration);
                toast.show();
            }

        }.start();
    }

    public void createMedicAlarm(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("MedicAlarm");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        // Set the child's data to the value passed in from the text box.
        childRef.setValue("MedicAlarm");
        Context context = getApplicationContext();
        CharSequence text = "Alarma Medica!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    public void createSexAlarm(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("SexAlarm");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        // Set the child's data to the value passed in from the text box.
        childRef.setValue("SexAlarm");
        Context context = getApplicationContext();
        CharSequence text = "Alarma Genero!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    public void createBomberAlarm(View view) {
        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReference("BomberAlarm");
        // Create a new child with a auto-generated ID.
        DatabaseReference childRef = myRef.push();

        // Set the child's data to the value passed in from the text box.
        childRef.setValue("BomberAlarm");
        Context context = getApplicationContext();
        CharSequence text = "Alarma Bomberos!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

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

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
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
}
