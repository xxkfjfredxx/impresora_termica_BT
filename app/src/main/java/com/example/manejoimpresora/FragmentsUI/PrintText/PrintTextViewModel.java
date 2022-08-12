package com.example.manejoimpresora.FragmentsUI.PrintText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrintTextViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public PrintTextViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
