package com.example.stay_sober_android;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.stay_sober_android.models.ChatMessage;
import com.example.stay_sober_android.models.ChatModel;
import com.example.stay_sober_android.models.Request;
import com.example.stay_sober_android.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference profilesDatabase;
    private DatabaseReference requestsDatabase;
    private DatabaseReference chats;
    private DatabaseReference privateChats;

    private FirebaseAuth mAuth;
    private Button acceptRequestBtn;
    private Button deleteRequestBtn;
    private ImageView imageView;
    private TextView aboutTextView;
    private TextView usernameTextView;
    private User requestSender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String senderId = getIntent().getStringExtra("senderId");
        profilesDatabase = FirebaseDatabase.getInstance().getReference("profiles");
        chats = FirebaseDatabase.getInstance().getReference("chats");

        requestsDatabase = FirebaseDatabase.getInstance().getReference("requests");

        mAuth = FirebaseAuth.getInstance();
        acceptRequestBtn = findViewById(R.id.acceptRequestBtn);
        deleteRequestBtn = findViewById(R.id.deleteRequestBtn);
        aboutTextView = findViewById(R.id.aboutTextView);
        usernameTextView = findViewById(R.id.usernameTextView);

        profilesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserId().equals(senderId)) {
                        aboutTextView.setText(user.getDescription());
                        usernameTextView.setText(user.getName());
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatId = chats.push().getKey();
                chats.push().setValue(new ChatModel(senderId,mAuth.getUid(),chatId));
                requestsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Request request = userSnapshot.getValue(Request.class);
                            if (request.getSender().equals(senderId) && request.getReceiver().equals(mAuth.getUid())) {
                                userSnapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        deleteRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Request request = userSnapshot.getValue(Request.class);
                            if (request.getSender().equals(senderId) && request.getReceiver().equals(mAuth.getUid())) {
                                userSnapshot.getRef().removeValue();
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
