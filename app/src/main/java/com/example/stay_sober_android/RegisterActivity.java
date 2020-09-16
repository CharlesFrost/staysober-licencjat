package com.example.stay_sober_android;

import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.stay_sober_android.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.view.View.INVISIBLE;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText repeatPasswordInput;
    private Switch showPasswordSwitch;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private RadioButton needHelpButton;
    private RadioButton offerHelpRadio;
    private CheckBox bothCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        repeatPasswordInput = findViewById(R.id.repeatPasswordInput);
        showPasswordSwitch = findViewById(R.id.showPasswordSwitch);
        registerBtn = findViewById(R.id.registerBtn);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        bothCheckbox = findViewById(R.id.bothCheckbox);
        offerHelpRadio = findViewById(R.id.offerHelpRadio);
        needHelpButton = findViewById(R.id.needHelpButton);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(emailInput.getText().toString().trim(), passwordInput.getText().toString().trim(), repeatPasswordInput.getText().toString().trim());
            }
        });

        showPasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    repeatPasswordInput.setTransformationMethod(new HideReturnsTransformationMethod());
                    passwordInput.setTransformationMethod(new HideReturnsTransformationMethod());
                } else {
                    repeatPasswordInput.setTransformationMethod(new PasswordTransformationMethod());
                    passwordInput.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        bothCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    offerHelpRadio.setClickable(false);
                    needHelpButton.setClickable(false);
                    needHelpButton.setChecked(false);
                    offerHelpRadio.setChecked(false);
                } else {
                    offerHelpRadio.setClickable(true);
                    needHelpButton.setClickable(true);
                }
            }
        });
    }

    private void registerUser(String email, String password, String repeatPassword) {

        if (!bothCheckbox.isChecked() && !offerHelpRadio.isChecked() && !needHelpButton.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Choose your destiny!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(repeatPassword) || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Passwords are different!", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Please pass the correct informations", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(INVISIBLE);
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameInput.getText().toString().trim()).build();
                authResult.getUser().updateProfile(profileUpdates);
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
                FirebaseDatabase.getInstance().getReference("profiles").child(mAuth.getUid())
                        .setValue(new User(nameInput.getText().toString().trim(),
                                0,0,1,"Welcome!",null,reacher,giver,mAuth.getUid()));
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                progressBar.setVisibility(INVISIBLE);
                startActivity(intent);
                finish();
            }
        });
    }
}
