package co.euphony.sample.fragments.tx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.euphony.sample.R;

public class TxCommunicationFragment extends Fragment {

    public TxCommunicationFragment() {
        // Required empty public constructor
    }

    public static TxCommunicationFragment newInstance(String param1, String param2) {
        TxCommunicationFragment fragment = new TxCommunicationFragment();
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
        return inflater.inflate(R.layout.fragment_tx_communication, container, false);
    }
}