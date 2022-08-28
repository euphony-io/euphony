package co.euphony.sample.fragments.rx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.euphony.sample.R;

public class RxCommunicationFragment extends Fragment {

    public RxCommunicationFragment() {
        // Required empty public constructor
    }

    public static RxCommunicationFragment newInstance(String param1, String param2) {
        RxCommunicationFragment fragment = new RxCommunicationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rx_communication, container, false);
    }
}