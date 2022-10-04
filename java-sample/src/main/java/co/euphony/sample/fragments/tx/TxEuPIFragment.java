package co.euphony.sample.fragments.tx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.euphony.sample.R;
import co.euphony.tx.EuTxManager;

public class TxEuPIFragment extends Fragment {

    EuTxManager txManager = null;

    public TxEuPIFragment() {
        // Required empty public constructor
    }

    public static TxEuPIFragment newInstance(String param1, String param2) {
        return new TxEuPIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txManager = EuTxManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tx_eupi, container, false);

        Button eupi18500Btn = v.findViewById(R.id.eupi18500);
        Button eupi19000Btn = v.findViewById(R.id.eupi19000);
        Button eupi19500Btn = v.findViewById(R.id.eupi19500);
        Button eupi20000Btn = v.findViewById(R.id.eupi20000);
        Button eupi20500Btn = v.findViewById(R.id.eupi20500);

        eupi18500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txManager.callEuPI(18500, EuTxManager.EuPIDuration.LENGTH_SHORT);
            }
        });

        eupi19000Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txManager.callEuPI(19000, EuTxManager.EuPIDuration.LENGTH_SHORT);
            }
        });

        eupi19500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txManager.callEuPI(19500, EuTxManager.EuPIDuration.LENGTH_SHORT);
            }
        });

        eupi20000Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txManager.callEuPI(20000, EuTxManager.EuPIDuration.LENGTH_LONG);
            }
        });

        eupi20500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txManager.callEuPI(20500, EuTxManager.EuPIDuration.LENGTH_LONG);
            }
        });
        return v;
    }
}