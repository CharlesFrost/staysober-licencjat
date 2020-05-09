package com.example.stay_sober_android;

import androidx.annotation.NonNull;
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class DatabaseService {
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private User currentUser;
    private static final DatabaseReference MY_DATABASE = FirebaseDatabase.getInstance().getReference("profiles");

    public User getCurrentUser() {
        MY_DATABASE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (userSnapshot.getKey().equals(AUTH.getUid())) {
                        currentUser = user;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return currentUser;
    }
}
