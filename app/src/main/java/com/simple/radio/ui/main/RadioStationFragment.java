package com.simple.radio.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.simple.radio.R;
import com.simple.radio.model.RadioStationViewModel;

public class RadioStationFragment extends Fragment {

    private RadioStationViewModel mViewModel;
    private RecyclerView mRecyclerView;

    public static RadioStationFragment newInstance() {
        RadioStationFragment fragment = new RadioStationFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RadioStationViewModel.class);
        mViewModel.setKeyWord("");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_radio_station, container, false);
        mRecyclerView = root.findViewById(R.id.rv_radio_list);
        mViewModel.getKeyWord().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}