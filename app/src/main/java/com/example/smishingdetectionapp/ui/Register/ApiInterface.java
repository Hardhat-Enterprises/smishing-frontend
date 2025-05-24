package com.example.smishingdetectionapp.ui.Register;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("/api/auth/signup")
    Call<SignupResponse> signupUser(@Body SignupRequest signupRequest);

    @POST("/api/auth/verify-email")
    Call<VerificationResponse> verifyOTP(@Body VerifyRequest verifyRequest);
}
