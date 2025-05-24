package com.example.smishingdetectionapp.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public LoginViewModelFactory() {
        // Empty constructor for compatibility
    }

    public LoginViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            if (application != null) {
                return (T) new LoginViewModel(application);
            } else {
                // Fall back to creating without application context for compatibility
                return (T) new LoginViewModel(null);
            }
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}