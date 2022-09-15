package co.euphony.sample.fragments.rx;

import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;

import co.euphony.rx.EuPICallDetector;
import co.euphony.rx.EuRxManager;
import co.euphony.sample.R;
import co.euphony.util.EuOption;

public class RxEuPIFragment extends Fragment {

    EuRxManager mRxManager = EuRxManager.getInstance();
    EuOption mOption = EuOption.builder()
            .modeWith(EuOption.ModeType.EUPI)
            .build();
    Integer[] mFrequencies = new Integer[] {18500, 19000, 19500, 20000, 20500};
    HashMap<Integer, EuPIRxViewMaker> mEuPICounters = new HashMap<>();

    boolean isListening = false;
    Button mListenBtn = null;

    public RxEuPIFragment() {
        // Required empty public constructor
    }

    public static RxEuPIFragment newInstance(String param1, String param2) {
        return new RxEuPIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mRxManager != null)
            mRxManager.finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRxManager.setOption(mOption);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rx_eupi, container, false);
        LinearLayoutCompat linearLayout = v.findViewById(R.id.frequency_view_list);

        for (Integer frequency : mFrequencies) {
            mEuPICounters.put(frequency, new EuPIRxViewMaker(frequency, this.getActivity()));
            EuPIRxViewMaker eupiCounter = mEuPICounters.get(frequency);

            if(eupiCounter != null) {
                linearLayout.addView(eupiCounter.getLabelView());
                linearLayout.addView(eupiCounter.getCountView());
                /* EuPI's "WaveKeyPressed" will be detected  */
                mRxManager.setOnWaveKeyPressed(frequency, new EuPICallDetector() {
                    @Override
                    public void call() {
                        eupiCounter.incrementKeyPressedCount();
                    }
                });

                /* EuPI's "WaveKeyDown" will be detected  */
                mRxManager.setOnWaveKeyDown(frequency, new EuPICallDetector() {
                    @Override
                    public void call() {
                        eupiCounter.incrementKeyDownCount();
                    }
                });

                /* EuPI's "WaveKeyDown" will be detected  */
                mRxManager.setOnWaveKeyUp(frequency, new EuPICallDetector() {
                    @Override
                    public void call() {
                        eupiCounter.incrementKeyUpCount();
                    }
                });
            }
        }

        mListenBtn = v.findViewById(R.id.listenBtn);
        mListenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isListening) {
                    mRxManager.finish();
                    mListenBtn.setText(R.string.listen_btn_title);
                } else {
                    mRxManager.listen();
                    mListenBtn.setText(R.string.stop_btn_title);
                }

                isListening ^= true;
            }
        });

        return v;
    }
}