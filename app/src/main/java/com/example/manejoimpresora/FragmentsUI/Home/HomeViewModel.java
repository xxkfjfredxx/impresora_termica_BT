package com.example.manejoimpresora.FragmentsUI.Home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public HomeViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
