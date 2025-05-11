package com.example.jobfinder01.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> username = new MutableLiveData<>("Nguyễn Văn A");
    private final MutableLiveData<String> email = new MutableLiveData<>("email@example.com");
    private final MutableLiveData<String> phone = new MutableLiveData<>("0123-456-789");
    private final MutableLiveData<String> jobTitle = new MutableLiveData<>("Software Engineer");

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public LiveData<String> getJobTitle() {
        return jobTitle;
    }
}
