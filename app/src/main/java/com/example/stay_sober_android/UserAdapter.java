package com.example.stay_sober_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.stay_sober_android.models.Doctor;
import com.example.stay_sober_android.models.Request;
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    private DatabaseReference myDatabase;
    private FirebaseAuth mAuth;
    private boolean withAddButon;
    public UserAdapter(Context context, ArrayList<User> users, boolean withAddButon) {
        super(context, 0, users);
        myDatabase = FirebaseDatabase.getInstance().getReference("requests");
        mAuth = FirebaseAuth.getInstance();
        this.withAddButon = withAddButon;
    }
    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        myDatabase = FirebaseDatabase.getInstance().getReference("requests");
        mAuth = FirebaseAuth.getInstance();
        this.withAddButon = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        TextView displayName =convertView.findViewById(R.id.displayNameTextView);
        TextView aboutMe =convertView.findViewById(R.id.aboutMePpl);
        TextView addPerson = convertView.findViewById(R.id.sendRequestToPerson);

        if (!withAddButon) {
            addPerson.setVisibility(View.INVISIBLE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(),ChatActivity.class).putExtra("interlocutor",user.getUserId()));
                }
            });
        } else {
            //send request to person database
            addPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDatabase.push().setValue(new Request(mAuth.getUid(),user.getUserId(),mAuth.getCurrentUser().getDisplayName()));
                }
            });
        }



        displayName.setText(user.getName());
        if (user.getDescription()!=null) {
            aboutMe.setText(user.getDescription());
        } else {
            aboutMe.setText("Text me! :)");
        }

        if (user instanceof Doctor) {
            aboutMe.setVisibility(View.INVISIBLE);
            addPerson.setOnClickListener(v -> {
                int permissionCheck = ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity)v.getContext(), new String[]{Manifest.permission.CALL_PHONE},123);
                } else {
                    v.getContext().startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+((Doctor) user).getPhoneNumber())));
                }
            });
            addPerson.setText("CALL");

        }
        return convertView;
    }

    public void onCall() {

    }


}
