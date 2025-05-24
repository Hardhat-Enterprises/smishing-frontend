package com.example.smishingdetectionapp.ui.login;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRepository {
    private static final String BASE_URL = "http://34.160.168.26/";


    public void loginUser(String email, String password, MutableLiveData<LoginResponse> loginResponse, MutableLiveData<String> error) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginApiInterface apiInterface = retrofit.create(LoginApiInterface.class);

        // Create the LoginRequest
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Make the POST request
        Call<LoginResponse> call = apiInterface.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResponse.setValue(response.body());
                } else {
                    error.setValue("Login failed. Please check your credentials.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.setValue("Network error. Please try again.");
            }
        });
    }
}