package co.euphony.sample.fragments.rx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.euphony.sample.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RxMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RxMainFragment extends Fragment {

    public RxMainFragment() {
        // Required empty public constructor
    }

    public static RxMainFragment newInstance() {
        return new RxMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rx_main, container, false);
        return v;
    }
}