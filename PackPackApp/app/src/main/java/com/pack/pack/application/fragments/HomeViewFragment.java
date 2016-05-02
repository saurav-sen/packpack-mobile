package com.pack.pack.application.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.HomeActivityAdapter;
import com.pack.pack.application.topic.activity.model.TopicEvent;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeViewFragment extends Fragment {

    private TabType tabType;

    private ProgressDialog progressDialog;

   public void setTabType(TabType tabType) {
       this.tabType = tabType;
   }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // progressDialog = new ProgressDialog(getActivity());
       // progressDialog.setMessage("Loading...");
        //progressDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = (View) inflater.inflate(R.layout.home_topic_view, container, false);
        ListView listView = (ListView) view.findViewById(R.id.home_events);
        listView.setAdapter(new HomeActivityAdapter(getActivity(), Collections.<TopicEvent>emptyList()));
        //hideProgressDialog();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
