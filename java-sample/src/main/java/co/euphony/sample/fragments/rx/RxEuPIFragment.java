package co.euphony.sample.fragments.rx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.euphony.sample.R;

public class RxEuPIFragment extends Fragment {

    public RxEuPIFragment() {
        // Required empty public constructor
    }

    public static RxEuPIFragment newInstance(String param1, String param2) {
        RxEuPIFragment fragment = new RxEuPIFragment();
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
        return inflater.inflate(R.layout.fragment_rx_eupi, container, false);
    }
}