package com.example.jobfinder01.Models;

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

public class SavedJobsViewModel extends ViewModel {

    private final MutableLiveData<List<Job>> savedJobs;
    private static final String PREFS_NAME = "SavedJobsPrefs";
    private static final String KEY_SAVED_JOBS = "saved_jobs";

    public SavedJobsViewModel() {
        savedJobs = new MutableLiveData<>();
        savedJobs.setValue(new ArrayList<>());
    }

    public LiveData<List<Job>> getSavedJobs() {
        return savedJobs;
    }

    public void loadSavedJobs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jobsJson = prefs.getString(KEY_SAVED_JOBS, null);
        if (jobsJson != null) {
            Type type = new TypeToken<List<Job>>() {}.getType();
            List<Job> jobs = new Gson().fromJson(jobsJson, type);
            savedJobs.setValue(jobs != null ? jobs : new ArrayList<>());
        } else {
            savedJobs.setValue(new ArrayList<>());
        }
    }

    public void addSavedJob(Context context, Job job) {
        List<Job> currentJobs = savedJobs.getValue() != null ? new ArrayList<>(savedJobs.getValue()) : new ArrayList<>();
        if (!currentJobs.contains(job)) {
            currentJobs.add(job);
            savedJobs.setValue(currentJobs);

            // Lưu lại danh sách đã cập nhật
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String jobsJson = new Gson().toJson(currentJobs);
            editor.putString(KEY_SAVED_JOBS, jobsJson);
            editor.apply();
        }
    }

    public void removeSavedJob(Context context, Job job) {
        List<Job> currentJobs = savedJobs.getValue() != null ? new ArrayList<>(savedJobs.getValue()) : new ArrayList<>();
        currentJobs.remove(job);
        savedJobs.setValue(currentJobs);

        // Lưu lại danh sách đã cập nhật
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String jobsJson = new Gson().toJson(currentJobs);
        editor.putString(KEY_SAVED_JOBS, jobsJson);
        editor.apply();
    }
}