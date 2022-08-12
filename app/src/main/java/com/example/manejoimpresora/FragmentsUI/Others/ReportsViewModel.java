package com.example.manejoimpresora.FragmentsUI.Others;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ReportsViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
