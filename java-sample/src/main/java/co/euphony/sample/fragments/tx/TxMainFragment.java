package co.euphony.sample.fragments.tx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.euphony.sample.R;

public class TxMainFragment extends Fragment {

    public TxMainFragment() {
        // Required empty public constructor
    }

    public static TxMainFragment newInstance() {
        return new TxMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_tx_main, container, false);
        return v;
    }
}