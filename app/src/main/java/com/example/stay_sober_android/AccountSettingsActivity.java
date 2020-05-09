package com.example.stay_sober_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.example.stay_sober_android.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Locale;

public class AccountSettingsActivity extends AppCompatActivity {
    private TextView rangeTextView;
    private SeekBar rangeBar;
    private Button saveBtn;
    private FusedLocationProviderClient fusedLocationProvider;
    private FirebaseAuth mAuth;
    private DatabaseReference myDatabase;
    private CheckBox bothCheckbox;
    private RadioButton needHelpButton;
    private RadioButton offerHelpRadio;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mAuth = FirebaseAuth.getInstance();
        saveBtn = findViewById(R.id.saveAccounDatatBtn);
        myDatabase = FirebaseDatabase.getInstance().getReference("profiles");
        bothCheckbox = findViewById(R.id.bothCheckboxSettings);
        needHelpButton = findViewById(R.id.needHelpButtonSettings);
        offerHelpRadio = findViewById(R.id.offerHelpRadioSettings);

        initRangeBar();
        refreshUser();


        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountData(rangeBar);
            }
        });
    }

    private void refreshUser() {
        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (userSnapshot.getKey().equals(mAuth.getUid())) {
                        currentUser = user;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveAccountData(SeekBar seekBar) {
        if (ActivityCompat.checkSelfPermission(AccountSettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            saveInDb();
        } else {
            ActivityCompat.requestPermissions(AccountSettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void saveInDb() {
        fusedLocationProvider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                final Location location = task.getResult();
                if (location != null) {
                    myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                if (userSnapshot.getKey().equals(mAuth.getUid())) {
                                    System.out.println(userSnapshot.getKey());
                                    User user = userSnapshot.getValue(User.class);
                                    user.setLatitude(location.getLatitude());
                                    user.setLongitude(location.getLongitude());
                                    user.setRange(rangeBar.getProgress());
                                    boolean giver = false;
                                    boolean reacher = false;
                                    if (bothCheckbox.isChecked()) {
                                        giver = true;
                                        reacher = true;
                                    } else if (offerHelpRadio.isChecked()) {
                                        giver = true;
                                    } else if (needHelpButton.isChecked()) {
                                        reacher = true;
                                    }
                                    user.setReacher(reacher);
                                    user.setGiver(giver);
                                    FirebaseDatabase.getInstance().getReference("profiles").child(mAuth.getUid()).setValue(user);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }


    private void initRangeBar() {
        rangeBar = findViewById(R.id.rangeBar);
        rangeTextView = findViewById(R.id.rangeTextView);
        rangeBar.setProgress(currentUser.getRange());
        rangeTextView.setText(String.valueOf(currentUser.getRange()));
        rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rangeTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (currentUser.isGiver() && currentUser.isReacher()) {
            this.bothCheckbox.setChecked(true);
        } else if (currentUser.isReacher()) {
            this.needHelpButton.setChecked(true);
        } else if (currentUser.isGiver()) {
            this.offerHelpRadio.setChecked(true);
        }
        refreshUser();
    }
}
