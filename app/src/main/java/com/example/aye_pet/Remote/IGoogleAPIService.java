package com.example.aye_pet.Remote;
import com.example.aye_pet.Model.MyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces (@Url String url);

}
