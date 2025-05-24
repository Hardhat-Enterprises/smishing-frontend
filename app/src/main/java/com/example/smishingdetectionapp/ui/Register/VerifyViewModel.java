package com.example.smishingdetectionapp.ui.Register;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class VerifyViewModel extends AndroidViewModel {

    private VerifyRepository verifyRepository;
    private MutableLiveData<VerificationResponse> verificationResponse;
    private static final String TAG = "VerifyViewModel";

    public VerifyViewModel(Application application) {
        super(application);
        verifyRepository = new VerifyRepository();
        verificationResponse = new MutableLiveData<>();
    }

    public LiveData<VerificationResponse> getVerificationResponse() {
        return verificationResponse;
    }

    public void verifyOTP(String email, String otp) {
        Log.d(TAG, "Verifying OTP for email: " + email);
        verifyRepository.verifyOTP(email, otp, verificationResponse);
    }
}
