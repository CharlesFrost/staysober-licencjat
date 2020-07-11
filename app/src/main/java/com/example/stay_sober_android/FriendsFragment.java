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
import com.example.stay_sober_android.models.ChatModel;
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private DatabaseReference myDatabase;
    private DatabaseReference chatsDatabase;
    private Set<String> ids;
    private ArrayList<User> users;
    private TextView textView;
    private FirebaseAuth mAuth;
    private User currentUser;
    private ListView pplList;
    private ArrayAdapter<User> listAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        users = new ArrayList<>();
        ids = new HashSet<>();
        myDatabase = FirebaseDatabase.getInstance().getReference("profiles");
        chatsDatabase = FirebaseDatabase.getInstance().getReference("chats");

        mAuth = FirebaseAuth.getInstance();
        pplList = getView().findViewById(R.id.friendsListView);

        listAdapter = new UserAdapter(getActivity(),users,false);
        pplList.setAdapter(listAdapter);

        chatsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = chatSnapshot.getValue(ChatModel.class);
                    if (chatModel.getSecondPerson() != null) {


                    if (chatModel.getSecondPerson().equals(mAuth.getUid().toString()) || chatModel.getFirstPerson().equals(mAuth.getUid().toString())) {
                        ids.add(chatModel.getFirstPerson());
                        ids.add(chatModel.getSecondPerson());
                    }
                    }
                }
                myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (ids.contains(user.getUserId()) && !user.getUserId().equals(mAuth.getUid())) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

}
