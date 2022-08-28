package co.euphony.sample.fragments.tx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.euphony.sample.R;

public class TxEuPIFragment extends Fragment {

    public TxEuPIFragment() {
        // Required empty public constructor
    }

    public static TxEuPIFragment newInstance(String param1, String param2) {
        TxEuPIFragment fragment = new TxEuPIFragment();
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
        View v = inflater.inflate(R.layout.fragment_tx_eupi, container, false);

        return v;
    }
}