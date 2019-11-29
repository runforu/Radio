package com.simple.radio.model;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simple.radio.RadioService;

import static com.simple.radio.RadioService.ACTION_PLAY;
import static com.simple.radio.RadioService.ACTION_STOP;
import static com.simple.radio.RadioService.DATA_SOURCE;

public class PlayViewModel extends ViewModel {


    public MutableLiveData<PlayStatus> getPlayStatus() {
        return mPlayStatus;
    }

    public void setPlayStatus(PlayStatus status) {
        mPlayStatus.setValue(status);
    }

    public void setStation(StationInfo station) {
        mCurrentRadio.setValue(station);
    }

    public void setStation(StationInfo station, PlayStatus status) {
        mCurrentRadio.setValue(station);
        mPlayStatus.setValue(status);
    }

    public MutableLiveData<StationInfo> getStation() {
        return mCurrentRadio;
    }

    private MutableLiveData<PlayStatus> mPlayStatus = new MutableLiveData<>();

    private MutableLiveData<StationInfo> mCurrentRadio = new MutableLiveData<>();

}