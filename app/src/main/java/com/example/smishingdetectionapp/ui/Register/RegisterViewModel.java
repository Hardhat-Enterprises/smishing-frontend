package com.example.smishingdetectionapp.ui.Register;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class RegisterViewModel extends AndroidViewModel {

    private RegisterRepository registerRepository;
    private MutableLiveData<SignupResponse> signupResponse;
    private MutableLiveData<String> error;
    private static final String TAG = "RegisterViewModel";

    public RegisterViewModel(Application application) {
        super(application);
        registerRepository = new RegisterRepository();
        signupResponse = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    public LiveData<SignupResponse> getSignupResponse() {
        return signupResponse;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void registerUser(String fullName, String phoneNumber, String email, String password) {
        Log.d(TAG, "Registering user: " + email);
        registerRepository.registerUser(fullName, phoneNumber, email, password, signupResponse, error);
    }
}
