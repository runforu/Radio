package com.simple.radio.model;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
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