package com.example.smishingdetectionapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import java.util.Map;
import retrofit2.http.Header;

import java.util.HashMap;
import java.util.List;


// Handles all the API requests
public interface ApiService {

    // Send FCM token to backend for push notifications
    @POST("api/users/{userId}/update-fcm-token")
    Call<Void> updateFcmToken(@Path("userId") String userId, @Body HashMap<String, String> body);

    // Add guardian email to user's profile
    @POST("api/users/{userId}/add-guardian")
    Call<Void> addGuardian(@Path("userId") String userId, @Body HashMap<String, String> body);

    //notify guardian about a smishing alert
    @POST("users/{userID}/notify-guardian")
    Call<Void> triggerNotification(@Path("userID") String userID);

    @POST("users/{userId}/get-notification-history")
    Call<List<NotificationItem>> getNotificationHistory(@Path("userId") String userId);

    @POST("/user/save-fcm-token")
    Call<Void> saveFcmToken(@Header("Authorization") String token, @Body Map<String, String> body);


}
