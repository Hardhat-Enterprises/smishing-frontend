package com.example.smishingdetectionapp.ui.Register;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerifyRepository {

    private static final String BASE_URL = "http://34.160.168.26/";
    private static final String TAG = "VerifyRepository"; // Log Tag

    public void verifyOTP(String email, String otp, MutableLiveData<VerificationResponse> verificationResponse) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        VerifyRequest verifyRequest = new VerifyRequest(email, otp);

        // Log the request
        Log.d(TAG, "Verify OTP Request: " + verifyRequest.toString());

        Call<VerificationResponse> call = apiInterface.verifyOTP(verifyRequest);
        call.enqueue(new Callback<VerificationResponse>() {
            @Override
            public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Verify OTP Success: " + response.body().getMessage());
                    verificationResponse.setValue(response.body());
                } else {
                    Log.e(TAG, "Verify OTP Error: " + (response.body() != null ? response.body().getMessage() : "No message"));
                    verificationResponse.setValue(new VerificationResponse(false, "Verification failed"));
                }
            }

            @Override
            public void onFailure(Call<VerificationResponse> call, Throwable t) {
                Log.e(TAG, "Verify OTP Failure: " + t.getMessage(), t);
                verificationResponse.setValue(new VerificationResponse(false, "Network error. Try again."));
            }
        });
    }
}
