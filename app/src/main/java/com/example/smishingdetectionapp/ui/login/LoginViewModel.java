package com.example.smishingdetectionapp.ui.login;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smishingdetectionapp.R;

public class LoginViewModel extends AndroidViewModel {
    private LoginRepository loginRepository;
    private MutableLiveData<LoginResponse> loginResponse;
    private MutableLiveData<String> error;
    private MutableLiveData<LoginFormState> loginFormState;
    private MutableLiveData<LoginResult> loginResult;

    public LoginViewModel(Application application) {
        super(application);
        loginRepository = new LoginRepository();
        loginResponse = new MutableLiveData<>();
        error = new MutableLiveData<>();
        loginFormState = new MutableLiveData<>();
        loginResult = new MutableLiveData<>();
    }

    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponse;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        // First validate inputs
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
            return;
        }
        if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
            return;
        }

        // If inputs are valid, attempt login
        loginRepository.loginUser(email, password, loginResponse, error);
    }

    // A placeholder username validation check
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}