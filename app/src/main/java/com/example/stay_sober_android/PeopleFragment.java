package com.example.stay_sober_android;

import android.app.ProgressDialog;
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
import com.example.Place;
import com.example.stay_sober_android.models.ChatModel;
import com.example.stay_sober_android.models.Request;
import com.example.stay_sober_android.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {
    private DatabaseReference myDatabase;
    private DatabaseReference chatsDatabase;
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
        chatsDatabase = FirebaseDatabase.getInstance().getReference("chats");

        mAuth = FirebaseAuth.getInstance();
        pplList = getView().findViewById(R.id.pplList);

        listAdapter = new UserAdapter(getActivity(), users);
        pplList.setAdapter(listAdapter);
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


        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAdapter.clear();
                users.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    double distance = distance(user.getLatitude(), user.getLongitude(), currentUser.getLatitude(), currentUser.getLongitude(), "K");
                    if (distance < currentUser.getRange()) {
                        if (!userSnapshot.getKey().equals(mAuth.getUid()) && (user.isGiver() == currentUser.isReacher() || user.isReacher() == currentUser.isGiver())) {
                            users.add(user);
                        }
                    }
                }
                listAdapter.notifyDataSetChanged();

                updateList();
                deleteFriends();
//                getSpecialist(currentUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    public void updateList() {
        FirebaseDatabase.getInstance().getReference("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot requestSnapshop : dataSnapshot.getChildren()) {
                    Request request = requestSnapshop.getValue(Request.class);
                    if (request.getReceiver().equals(mAuth.getUid()) || request.getSender().equals(mAuth.getUid())) {
                        for (User user : users) {
                            if (user.getUserId().equals(request.getSender()) || user.getUserId().equals(request.getReceiver())) {
                                users.remove(user);
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteFriends() {
        chatsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatsSnapshop : dataSnapshot.getChildren()) {
                    ChatModel chatModel = chatsSnapshop.getValue(ChatModel.class);
                    if (chatModel.getSecondPerson() != null) {


                        if (chatModel.getFirstPerson().equals(mAuth.getUid()) || chatModel.getSecondPerson().equals(mAuth.getUid())) {
                            List<User> usersToDelete = new ArrayList<>();
                            for (User user : users) {
                                if (user.getUserId().equals(chatModel.getFirstPerson()) || user.getUserId().equals(chatModel.getSecondPerson())) {
                                    usersToDelete.add(user);
                                }
                            }
                            users.removeAll(usersToDelete);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Object getSpecialist(User user) {
        String link = "https://maps.googleapis.com/maps/api/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(link)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApiService service = retrofit.create(RestApiService.class);
        Call<List<Place>> call = service.listPlaces(user.getLatitude(), user.getLongitude());

        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                for (Place place : response.body()) {
                    System.out.println(place.getStatus());
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
        return null;
    }
}
