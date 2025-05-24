package com.example.smishingdetectionapp.ui.Register;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterRepository {

    private static final String BASE_URL = "http://34.160.168.26/";

    private static final String TAG = "RegisterRepository"; // Log Tag

    public void registerUser(String fullName, String phoneNumber, String email, String password, MutableLiveData<SignupResponse> signupResponse, MutableLiveData<String> error) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        // Create the SignupRequest
        SignupRequest signupRequest = new SignupRequest(fullName, phoneNumber, email, password);
        // Log the request
        Log.d(TAG, "Register User Request: " + signupRequest.toString());

        // Make the POST request
        Call<SignupResponse> call = apiInterface.signupUser(signupRequest);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    // Log successful response
                    Log.d(TAG, "Register User Success: " + response.body().getMessage());
                    signupResponse.setValue(response.body());
                } else {
                    // Log the status code and error body for better error tracking
                    Log.e(TAG, "Register User Error: " + response.code() + " - " + response.message());
                    error.setValue("Signup failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                // Log error message and stack trace for debugging
                Log.e(TAG, "Register User Failure: " + t.getMessage(), t);
                error.setValue("Network error. Please try again.");
            }

        });
    }
}
