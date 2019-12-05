package com.simple.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.simple.radio.model.PlayStatus;
import com.simple.radio.model.PlayViewModel;
import com.simple.radio.model.RadioStation;
import com.simple.radio.ui.main.SectionsPagerAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PlayViewModel mPlayStateViewModel;
    private ImageButton mBtnPlayStop;
    private ImageView mIvRadioLogo;
    private TextView mTvRadioTitle;
    private TextView mTvRadioFm;
    private ConstraintLayout mPlayBar;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;
    private BroadcastReceiver mPlayStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RadioService.PLAY_STATUS_CHANGE)) {
                PlayStatus ps = (PlayStatus) intent.getSerializableExtra(RadioService.PLAY_STATUS);
                mPlayStateViewModel.setPlayStatus(ps);
            }
        }
    };

    public static String doGET(String urlAddress) {
        try {
            URL url = new URL(urlAddress);
            URLConnection connection = url.openConnection();

            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doPost(String urlAddress, String body) {
        try {
            URL url = new URL(urlAddress);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("encoding", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json;UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Accept", "application/json;*");

            connection.setDoInput(true);
            connection.setDoOutput(true);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                bufferedWriter.write(body);
                bufferedWriter.flush();
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initViewModel();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RadioService.PLAY_STATUS_CHANGE);
        registerReceiver(mPlayStatusReceiver, intentFilter);

        new Thread(() -> {
            doGET("https://github.com");
            doPost("https://github.com", "{}");
        }).start();
    }

    private void initViewModel() {
        mPlayStateViewModel = ViewModelProviders.of(this).get(PlayViewModel.class);
        mPlayStateViewModel.getPlayStatus().observe(this, (playStatus) -> {
            switch (playStatus) {
                case STATUS_PAUSE:
                    mBtnPlayStop.setEnabled(true);
                    mBtnPlayStop.setImageResource(R.drawable.ic_play_color);
                    break;

                case STATUS_NONE:
                    mBtnPlayStop.setEnabled(false);
                    mIvRadioLogo.setImageResource(R.drawable.ic_default_logo);
                    break;

                case STATUS_WATIING:
                    mBtnPlayStop.setEnabled(false);
                    mIvRadioLogo.setImageResource(R.drawable.ic_radio_station);
                    break;

                case STATUS_PLAY:
                    mBtnPlayStop.setEnabled(true);
                    mBtnPlayStop.setImageResource(R.drawable.ic_pause_color);
                    break;

                default:
                    break;
            }
        });

        mPlayStateViewModel.getStation().observe(this, (radioStation) -> {
            Intent intent = new Intent(this, RadioService.class);
            if (radioStation != null) {
                mPlayBar.setVisibility(View.VISIBLE);
                mTvRadioTitle.setText(mPlayStateViewModel.getStation().getValue().mTitle);
                mTvRadioFm.setText(mPlayStateViewModel.getStation().getValue().mSignal);
                intent.setAction(RadioService.ACTION_PLAY);
                Bundle b = new Bundle();
                b.putString(RadioService.DATA_SOURCE, mPlayStateViewModel.getStation().getValue().mStreams.get(0).mUrl);
                intent.putExtras(b);
                startService(intent);
            } else {
                mPlayBar.setVisibility(View.GONE);
                mTvRadioTitle.setText(null);
                mTvRadioFm.setText(null);
                intent.setAction(RadioService.ACTION_CLOSE_RADIO);
                startService(intent);
            }
        });


        mPlayStateViewModel.setStation(null);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.menu_update:
                    mPlayStateViewModel.setStation(null);
                    return true;
                case R.id.menu_about:
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        });

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioStation si = new RadioStation();
                si.mName = "Test";
                si.mSignal = "FM 100.2 Mhz";
                si.mTitle = "Test Radio";
                si.mWebsite = "https://www.abc.net.au/triplej/";
                List<RadioStation.Stream> streams = new ArrayList<>();
                RadioStation.Stream s = new RadioStation.Stream();
                s.mUrl = "https://abcradiolivehls-lh.akamaihd.net/i/triplejnsw_1@327300/master.m3u8";
                s.mMime = "application/x-mpegURL";
                s.mIsContainer = false;
                s.mMediaType = "HLS";
                streams.add(s);
                si.mStreams = streams;
                mPlayStateViewModel.setStation(si);
                mPlayStateViewModel.setPlayStatus(PlayStatus.STATUS_PAUSE);
            }
        });

        mIvRadioLogo = findViewById(R.id.iv_radio_station_logo);
        mTvRadioTitle = findViewById(R.id.tv_radio_station_title);
        mTvRadioFm = findViewById(R.id.tv_radio_station_fm);
        mPlayBar = findViewById(R.id.cl_play_bar);
        mBtnPlayStop = findViewById(R.id.ib_play_stop);
        mBtnPlayStop.setOnClickListener((v) -> {
            Intent intent = new Intent(this, RadioService.class);
            switch (mPlayStateViewModel.getPlayStatus().getValue()) {
                case STATUS_PAUSE:
                    intent.setAction(RadioService.ACTION_PLAY);
                    Bundle b = new Bundle();
                    b.putString(RadioService.DATA_SOURCE, mPlayStateViewModel.getStation().getValue().mStreams.get(0).mUrl);
                    intent.putExtras(b);
                    startService(intent);
                    mPlayStateViewModel.setPlayStatus(PlayStatus.STATUS_WATIING);
                    break;
                case STATUS_NONE:
                case STATUS_WATIING:
                    break;
                case STATUS_PLAY:
                    if (mPlayStateViewModel.getStation().getValue() != null) {
                        intent.setAction(RadioService.ACTION_STOP);
                        startService(intent);
                    } else {
                        intent.setAction(RadioService.ACTION_CLOSE_RADIO);
                        startService(intent);
                    }
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPlayStatusReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
}