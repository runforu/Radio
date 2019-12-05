package com.simple.radio.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RadioStationViewModel extends ViewModel {

    public MutableLiveData<String> getKeyWord() {
        return mKeyWord;
    }

    public void setKeyWord(String mKeyWord) {
        this.mKeyWord.setValue(mKeyWord);
    }

    private MutableLiveData<String> mKeyWord = new MutableLiveData<>();

}