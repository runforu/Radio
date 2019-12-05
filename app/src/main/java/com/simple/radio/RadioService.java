package com.simple.radio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.simple.radio.model.PlayStatus;

import java.io.IOException;

public class RadioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    public static final String ACTION_PLAY = "com.simple.radio.action.PLAY";
    public static final String ACTION_STOP = "com.simple.radio.action.STOP";
    public static final String ACTION_CLOSE_RADIO = "com.simple.radio.action.CLOSE_RADIO";
    public static final String PLAY_STATUS_CHANGE = "com.simple.radio.PLAY_STATUS_CHANGE";
    public static final String DATA_SOURCE = "com.simple.radio.DATA_SOURCE";
    public static final String PLAY_STATUS = "com.simple.radio.PLAY_STATUS";
    MediaPlayer mMediaPlayer;
    WifiManager.WifiLock mWifiLock;
    private Intent mIntent = new Intent("com.simple.radio.PLAY_STATUS_CHANGE");

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(this);
        mWifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "radio_lock");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mWifiLock.acquire();

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }

            Bundle b = intent.getExtras();
            try {
                mMediaPlayer.setDataSource(b.getString(DATA_SOURCE));
            } catch (IOException e) {
            }
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();

            mIntent.removeExtra(PLAY_STATUS);
            mIntent.putExtra(PLAY_STATUS, PlayStatus.STATUS_WATIING);
            sendBroadcast(mIntent);
        } else if (intent.getAction().equals(ACTION_STOP)) {
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }

            mIntent.removeExtra(PLAY_STATUS);
            mIntent.putExtra(PLAY_STATUS, PlayStatus.STATUS_PAUSE);
            sendBroadcast(mIntent);
        } else if (intent.getAction().equals(ACTION_CLOSE_RADIO)) {
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }

            mIntent.removeExtra(PLAY_STATUS);
            mIntent.putExtra(PLAY_STATUS, PlayStatus.STATUS_NONE);
            sendBroadcast(mIntent);
        } else {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        mIntent.removeExtra(PLAY_STATUS);
        mIntent.putExtra(PLAY_STATUS, PlayStatus.STATUS_PLAY);
        sendBroadcast(mIntent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mIntent.removeExtra(PLAY_STATUS);
        mIntent.putExtra(PLAY_STATUS, PlayStatus.STATUS_PAUSE);
        sendBroadcast(mIntent);
        return false;
    }
}