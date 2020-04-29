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
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {
    private DatabaseReference myDatabase;
    private ArrayList<User> users;
    private TextView textView;
    private FirebaseAuth mAuth;
    private User currentUser;
    private ListView pplList;
    private ArrayAdapter<User> listAdapter;

    public PeopleFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        users = new ArrayList<>();
        myDatabase = FirebaseDatabase.getInstance().getReference("profiles");
        mAuth = FirebaseAuth.getInstance();
        pplList = getView().findViewById(R.id.pplList);

        listAdapter = new UserAdapter(getActivity(),users);
        pplList.setAdapter(listAdapter);
        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUid().equals(mAuth.getUid())) {
                        currentUser = user;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getChildrenCount());
                listAdapter.clear();
                users.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (!user.getUid().equals(mAuth.getUid()) && (user.isGiver()==currentUser.isReacher() || user.isReacher()==currentUser.isGiver())) {
                        users.add(user);
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
