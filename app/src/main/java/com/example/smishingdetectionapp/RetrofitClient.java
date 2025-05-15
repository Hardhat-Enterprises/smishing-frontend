package com.example.smishingdetectionapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:3000/api/";
    private static Retrofit retrofit = null;

    // No-arg method to use default BASE_URL
    public static Retrofit getClient() {
        return getClient(BASE_URL);
    }

    // Optional: Keep this if you ever want custom base URLs
    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
