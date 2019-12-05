package com.simple.radio.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import static com.simple.radio.Constants.TAG;

public class RadioStation {

    @SerializedName("id")
    public int mId;
    @SerializedName("title")
    public String mTitle;
    @SerializedName("log_url")
    public String mLogoUrl;
    @SerializedName("streams")
    public List<Stream> mStreams;
    @SerializedName("tags")
    public String mTags;
    @SerializedName("name")
    public String mName;
    @SerializedName("signal")
    public String mSignal;
    @SerializedName("website")
    public String mWebsite;
    @SerializedName("individual")
    public boolean mIndividual;
    @SerializedName("genre")
    public String mGenre;

    public static List<RadioStation> fromJson(String json) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<RadioStation> stations = null;
        try {
            stations = gson.fromJson(json, new TypeToken<List<RadioStation>>() {
            }.getType());
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
        return stations;
    }

    public static String toJson(RadioStation rs) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(rs);
    }

    public static class Stream {
        @SerializedName("is_container")
        public boolean mIsContainer;
        @SerializedName("media_type")
        public String mMediaType;
        @SerializedName("mime")
        public String mMime;
        @SerializedName("url")
        public String mUrl;
    }
}
