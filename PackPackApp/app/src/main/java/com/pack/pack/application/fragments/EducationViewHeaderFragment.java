package com.pack.pack.application.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pack.pack.application.R;

/**
 * Created by Saurav on 08-07-2017.
 */
public class EducationViewHeaderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.education_event_item_header, container, false);
        return view;
    }
}
