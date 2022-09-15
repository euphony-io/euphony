package co.euphony.sample.fragments.rx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.euphony.rx.AcousticSensor;
import co.euphony.rx.EuRxManager;
import co.euphony.sample.R;

public class RxCommunicationFragment extends Fragment {

    EuRxManager mRxManager = EuRxManager.getInstance();
    private TextView listenView = null;
    private Button listenBtn = null;

    private boolean isListening = false;

    public RxCommunicationFragment() {
        // Required empty public constructor
    }

    public static RxCommunicationFragment newInstance(String param1, String param2) {
        return new RxCommunicationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rx_communication, container, false);

        listenView = v.findViewById(R.id.listenView);
        listenBtn = v.findViewById(R.id.button);

        mRxManager.setAcousticSensor(new AcousticSensor() {
            @Override
            public void notify(String letters) {
                listenView.setText(letters);
                listenBtn.setText(getString(R.string.listen_btn_title));
                isListening = false;
            }
        });

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isListening) {
                    mRxManager.finish();
                    listenBtn.setText(getString(R.string.listen_btn_title));
                } else {
                    mRxManager.listen();  //Listening Start
                    listenBtn.setText(getString(R.string.stop_btn_title));
                }

                isListening ^= true;
            }
        });

        return v;
    }
}