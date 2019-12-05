package com.simple.radio;

import com.simple.radio.model.RadioStation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void RadioStation_fromJson() {
        List<RadioStation> lrs = RadioStation.fromJson("[{\"id\":24457,\"name\":\"triple-j\",\"signal\":\"105.7 FM\",\"title\":\"Triple J\",\"website\":\"https://www.abc.net.au/triplej/\",\"logo_url\":\"https://www.abc.net.au/triplej/\",\"tags\":\"news,life\",\"individual\":false,\"streams\":[{\"is_container\":true,\"media_type\":\"HTML\",\"mime\":\"text/html\",\"url\":\"https://www.abc.net.au/triplej/listen-live/player/\"},{\"is_container\":false,\"media_type\":\"MP3\",\"mime\":\"audio/mpeg\",\"url\":\"http://live-radio01.mediahubaustralia.com/2TJW/mp3/\"},{\"is_container\":true,\"media_type\":\"MP3\",\"mime\":\"audio/x-mpegurl\",\"url\":\"https://www.abc.net.au/res/streaming/audio/mp3/triplej.pls\"},{\"is_container\":true,\"media_type\":\"HLS\",\"mime\":\"application/x-mpegURL\",\"url\":\"https://abcradiolivehls-lh.akamaihd.net/i/triplejnsw_1@327300/master.m3u8\"}]}]");
        assertEquals(1, lrs.size());
    }

    @Test
    public void RadioStation_toJson() {
        RadioStation rs = new RadioStation();
        rs.mId = 24457;
        rs.mName = "triple-j";
        rs.mSignal = "105.7 FM";
        rs.mTitle = "Triple J";
        rs.mWebsite = "https://www.abc.net.au/triplej/";
        rs.mLogoUrl = "https://www.abc.net.au/triplej/";
        rs.mTags = "news,life";
        rs.mIndividual = false;
        rs.mStreams = new ArrayList<>();
        RadioStation.Stream stream = new RadioStation.Stream();
        stream.mIsContainer = true;
        stream.mMediaType = "HTML";
        stream.mMime = "text/html";
        stream.mUrl = "https://www.abc.net.au/triplej/listen-live/player/";
        rs.mStreams.add(stream);
        stream = new RadioStation.Stream();
        stream.mIsContainer = false;
        stream.mMediaType = "MP3";
        stream.mMime = "audio/mpeg";
        stream.mUrl = "http://live-radio01.mediahubaustralia.com/2TJW/mp3/";
        rs.mStreams.add(stream);
        stream = new RadioStation.Stream();
        stream.mIsContainer = true;
        stream.mMediaType = "MP3";
        stream.mMime = "audio/x-mpegmUrl";
        stream.mUrl = "https://www.abc.net.au/res/streaming/audio/mp3/triplej.pls";
        rs.mStreams.add(stream);
        stream = new RadioStation.Stream();
        stream.mIsContainer = true;
        stream.mMediaType = "HLS";
        stream.mMime = "application/x-mpegmUrl";
        stream.mUrl = "https://abcradiolivehls-lh.akamaihd.net/i/triplejnsw_1@327300/master.m3u8";
        rs.mStreams.add(stream);
        stream = new RadioStation.Stream();
        String json = RadioStation.toJson(rs);
        assertNotNull(json);
    }
}