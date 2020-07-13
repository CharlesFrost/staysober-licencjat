package com.example.stay_sober_android;

import com.example.stay_sober_android.models.gmaps.*;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
public interface RestApiService {
        @POST("place/nearbysearch/json")
        Call<Place> listPlaces(@Query("location") String coords, @Query("type") String type,
                                     @Query("keyword") String keyword,@Query("key") String key,
                                     @Query("radius") String radius, @Query("fields") String fields);

        @POST("place/details/json")
        Call<com.example.stay_sober_android.models.gmaps.one_place.Place> getPlace(@Query("place_id") String place_id, @Query("fields") String fields,@Query("key") String key);
}
