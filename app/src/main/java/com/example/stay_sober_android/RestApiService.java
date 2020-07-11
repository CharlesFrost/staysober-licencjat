package com.example.stay_sober_android;

import com.example.Place;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
public interface RestApiService {
        @POST("place/nearbysearch/json?location={lat},{long}&type=doctor&keyword=uzależnienia&key=AIzaSyC-rxywikKFlONC7EXfGAIWONDI1uBHyN4&radius=200000")
        Call<List<Place>> listPlaces(@Path("lat") double lat, @Path("long") double lng);
}
