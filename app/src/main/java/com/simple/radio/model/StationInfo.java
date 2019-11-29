package com.simple.radio.model;

import java.util.ArrayList;
import java.util.List;

public class StationInfo {
    private String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSignal() {
        return mSignal;
    }

    public void setSignal(String mSignal) {
        this.mSignal = mSignal;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public List<Stream> getStreams() {
        return mStreams;
    }

    public void setStreams(List<Stream> mStreams) {
        this.mStreams = mStreams;
    }


    public String getLogoUrl() {
        return mLogoUrl;
    }

    public void setLogoUrl(String mLogoUrl) {
        this.mLogoUrl = mLogoUrl;
    }

    private String mLogoUrl;
    private List<Stream> mStreams;
    private String mName;
    private String mSignal;
    private String mWebsite;

    public static class Stream {
        public boolean ismIsContainer() {
            return mIsContainer;
        }

        public void setIsContainer(boolean mIsContainer) {
            this.mIsContainer = mIsContainer;
        }

        public String getMediaType() {
            return mMediaType;
        }

        public void setMediaType(String mMediaType) {
            this.mMediaType = mMediaType;
        }

        public String getMime() {
            return mMime;
        }

        public void setMime(String mMime) {
            this.mMime = mMime;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        private boolean mIsContainer;
        private String mMediaType;
        private String mMime;
        private String mUrl;

    }
}
