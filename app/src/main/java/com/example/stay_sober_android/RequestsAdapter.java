package com.example.stay_sober_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.stay_sober_android.models.Request;
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestsAdapter extends ArrayAdapter<Request> {
    private DatabaseReference requestsDatabase;
    private FirebaseAuth mAuth;
    public RequestsAdapter(@NonNull Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
        requestsDatabase = FirebaseDatabase.getInstance().getReference("requests");
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Request request = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.displayNameTextView);
        textView.setText(request.getSenderName());
        TextView aboutMe =convertView.findViewById(R.id.aboutMePpl);
        TextView addPerson = convertView.findViewById(R.id.sendRequestToPerson);
        aboutMe.setVisibility(View.INVISIBLE);
        addPerson.setText("OPEN");
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(),ProfileActivity.class).putExtra("senderId",request.getSender()));
            }
        });
        return convertView;
    }
}
