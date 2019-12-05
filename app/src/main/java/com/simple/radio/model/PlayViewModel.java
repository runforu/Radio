package com.simple.radio.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlayViewModel extends ViewModel {

    private MutableLiveData<PlayStatus> mPlayStatus = new MutableLiveData<>();
    private MutableLiveData<RadioStation> mCurrentRadioStation = new MutableLiveData<>();

    public MutableLiveData<PlayStatus> getPlayStatus() {
        return mPlayStatus;
    }

    public void setPlayStatus(PlayStatus status) {
        mPlayStatus.setValue(status);
    }

    public void setStation(RadioStation station, PlayStatus status) {
        mCurrentRadioStation.setValue(station);
        mPlayStatus.setValue(status);
    }

    public MutableLiveData<RadioStation> getStation() {
        return mCurrentRadioStation;
    }

    public void setStation(RadioStation station) {
        mCurrentRadioStation.setValue(station);
    }

}