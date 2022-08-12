package com.example.manejoimpresora.FragmentsUI.PrintBill;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrintBillViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public PrintBillViewModel() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        this.mText = mutableLiveData;
        mutableLiveData.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
