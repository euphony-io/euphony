package co.euphony.sample.fragments.tx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import co.euphony.sample.R;
import co.euphony.tx.EuTxManager;

public class TxCommunicationFragment extends Fragment {

    Spinner mCountSpinner;
    Spinner mEngineSpinner;
    EditText mSpeakText;
    Button mSpeakBtn;
    EuTxManager.PlayerEngine mSelectedEngineId = EuTxManager.PlayerEngine.ANDROID_DEFAULT_ENGINE;

    EuTxManager mTxManager = null;
    int count = 1;
    boolean speakOn = false;

    public TxCommunicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mTxManager != null) {
            mTxManager.stop();
        }
    }

    public static TxCommunicationFragment newInstance(String param1, String param2) {
        TxCommunicationFragment fragment = new TxCommunicationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTxManager = EuTxManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tx_communication, container, false);

        mCountSpinner = v.findViewById(R.id.count_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.count_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountSpinner.setAdapter(adapter);
        mCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                count = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                count = -1;
            }
        });

        mEngineSpinner = v.findViewById(R.id.engine_spinner);
        ArrayAdapter<CharSequence> engineAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.player_engine_array, android.R.layout.simple_spinner_dropdown_item);
        mEngineSpinner.setAdapter(engineAdapter);
        mEngineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1)
                    mSelectedEngineId = EuTxManager.PlayerEngine.EUPHONY_NATIVE_ENGINE;
                else
                    mSelectedEngineId = EuTxManager.PlayerEngine.ANDROID_DEFAULT_ENGINE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSelectedEngineId = EuTxManager.PlayerEngine.ANDROID_DEFAULT_ENGINE;
            }
        });


        mSpeakText = v.findViewById(R.id.speak_text);
        mSpeakBtn = v.findViewById(R.id.speak_btn);

        mSpeakBtn.setOnClickListener(view -> {
            if (speakOn) {
                mTxManager.stop();
                mSpeakBtn.setText(R.string.tx_comm_speak_btn_title);
                speakOn = false;
            } else {
                mTxManager.setCode(mSpeakText.getText().toString()); // To generate acoustic data "Hello, Euphony" for 5 times.
                mTxManager.play(count, mSelectedEngineId);
                mSpeakBtn.setText(R.string.tx_comm_stop_btn_title);
                speakOn = true;
            }
        });

        return v;
    }
}