package com.simple.radio;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simple.radio.model.PlayStatus;
import com.simple.radio.model.PlayViewModel;
import com.simple.radio.model.StationInfo;
import com.simple.radio.ui.main.SectionsPagerAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
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
                StationInfo si = new StationInfo();
                si.setName("Test");
                si.setSignal("FM 100.2 Mhz");
                si.setTitle("Test Radio");
                si.setWebsite("https://www.abc.net.au/triplej/");
                List<StationInfo.Stream> streams = new ArrayList<>();
                StationInfo.Stream s = new StationInfo.Stream();
                s.setUrl("https://abcradiolivehls-lh.akamaihd.net/i/triplejnsw_1@327300/master.m3u8");
                s.setMime("application/x-mpegURL");
                s.setIsContainer(false);
                s.setMediaType("HLS");
                streams.add(s);
                si.setStreams(streams);
                mPlayStateViewModel.setStation(si);
                mPlayStateViewModel.setPlayStatus(PlayStatus.STATUS_PAUSE);
            }
        });


        mIvRadioLogo = (ImageView) findViewById(R.id.iv_radio_station_logo);
        mTvRadioTitle = (TextView) findViewById(R.id.tv_radio_station_title);
        mTvRadioFm = (TextView) findViewById(R.id.tv_radio_station_fm);
        mPlayBar = (ConstraintLayout) findViewById(R.id.cl_play_bar);
        mBtnPlayStop = (ImageButton) findViewById(R.id.ib_play_stop);

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

        mPlayStateViewModel.getStation().observe(this, (stationInfo) -> {
            Intent intent = new Intent(this, RadioService.class);
            if (stationInfo != null) {
                mPlayBar.setVisibility(View.VISIBLE);
                intent.setAction(RadioService.ACTION_PLAY);
                Bundle b = new Bundle();
                b.putString(RadioService.DATA_SOURCE, mPlayStateViewModel.getStation().getValue().getStreams().get(0).getUrl());
                intent.putExtras(b);
                startService(intent);
            } else {
                mPlayBar.setVisibility(View.GONE);
                intent.setAction(RadioService.ACTION_CLOSE_RADIO);
                startService(intent);
            }
        });

        mBtnPlayStop.setOnClickListener((v) -> {
            Intent intent = new Intent(this, RadioService.class);
            switch (mPlayStateViewModel.getPlayStatus().getValue()) {
                case STATUS_PAUSE:
                    intent.setAction(RadioService.ACTION_PLAY);
                    Bundle b = new Bundle();
                    b.putString(RadioService.DATA_SOURCE, mPlayStateViewModel.getStation().getValue().getStreams().get(0).getUrl());
                    intent.putExtras(b);
                    startService(intent);
                    mPlayStateViewModel.setPlayStatus(PlayStatus.STATUS_WATIING);
                    break;
                case STATUS_NONE:
                case STATUS_WATIING:
                    break;
                case STATUS_PLAY:
                    if(mPlayStateViewModel.getStation().getValue()!=null) {
                        intent.setAction(RadioService.ACTION_STOP);
                        startService(intent);
                    }else{
                        intent.setAction(RadioService.ACTION_CLOSE_RADIO);
                        startService(intent);
                    }
                    break;
                default:
                    break;
            }
        });

        mPlayStateViewModel.setStation(null);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RadioService.PLAY_STATUS_CHANGE);
        registerReceiver(mPlayStatusReceiver, intentFilter);
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

    private PlayViewModel mPlayStateViewModel;
    private ImageButton mBtnPlayStop;
    private ImageView mIvRadioLogo;
    private TextView mTvRadioTitle;
    private TextView mTvRadioFm;
    private ConstraintLayout mPlayBar;
    private ViewPager mViewPager;
    private  FloatingActionButton mFab;

    private BroadcastReceiver mPlayStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RadioService.PLAY_STATUS_CHANGE)) {
                PlayStatus ps = (PlayStatus) intent.getSerializableExtra(RadioService.PLAY_STATUS);
                mPlayStateViewModel.setPlayStatus(ps);
            }
        }
    };
}