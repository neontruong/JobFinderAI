package com.example.jobfinder01.ui.jobapplications;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JobApplicationViewModel extends ViewModel {

    private final MutableLiveData<List<JobApplication>> applications;
    private static final String PREFS_NAME = "JobApplicationsPrefs";
    private static final String KEY_APPLICATIONS = "job_applications";

    public JobApplicationViewModel() {
        applications = new MutableLiveData<>();
        applications.setValue(new ArrayList<>());
    }

    public LiveData<List<JobApplication>> getApplications() {
        return applications;
    }

    public void loadApplications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String appsJson = prefs.getString(KEY_APPLICATIONS, null);
        if (appsJson != null) {
            Type type = new TypeToken<List<JobApplication>>() {}.getType();
            List<JobApplication> apps = new Gson().fromJson(appsJson, type);
            applications.setValue(apps != null ? apps : new ArrayList<>());
        } else {
            applications.setValue(new ArrayList<>());
        }
    }

    public void addApplication(Context context, JobApplication application) {
        List<JobApplication> currentApps = applications.getValue() != null ? new ArrayList<>(applications.getValue()) : new ArrayList<>();
        if (!currentApps.contains(application)) {
            currentApps.add(application);
            applications.setValue(currentApps);
            saveToPrefs(context, currentApps);
        }
    }

    private void saveToPrefs(Context context, List<JobApplication> apps) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String appsJson = new Gson().toJson(apps);
        editor.putString(KEY_APPLICATIONS, appsJson);
        editor.apply();
    }
}