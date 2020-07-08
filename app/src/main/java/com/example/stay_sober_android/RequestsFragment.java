package com.example.stay_sober_android;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stay_sober_android.models.Request;
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private DatabaseReference myDatabase;
    private ArrayList<Request> requests;
    private TextView textView;
    private FirebaseAuth mAuth;
    private User currentUser;
    private ListView requestsList;
    private ArrayAdapter<Request> listAdapter;

    public RequestsFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requests = new ArrayList<>();
        myDatabase = FirebaseDatabase.getInstance().getReference("requests");
        mAuth = FirebaseAuth.getInstance();
        requestsList = getView().findViewById(R.id.pplList);
        listAdapter = new RequestsAdapter(getActivity(),requests);
        requestsList.setAdapter(listAdapter);

        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Request request = userSnapshot.getValue(Request.class);
                    if (!request.getSender().equals(mAuth.getUid())) {
                        requests.add(request);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_people, container, false);
    }

}
