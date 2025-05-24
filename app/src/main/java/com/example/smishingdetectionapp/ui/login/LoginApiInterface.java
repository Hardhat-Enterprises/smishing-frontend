package com.example.smishingdetectionapp.ui.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiInterface {
    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
