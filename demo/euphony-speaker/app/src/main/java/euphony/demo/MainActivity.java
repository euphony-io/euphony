package euphony.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import euphony.lib.transmitter.EuTxManager;

public class MainActivity extends AppCompatActivity {

    Spinner mCountSpinner;
    Switch mLiveSwt;
    EditText mSpeakText;
    Button mSpeakBtn;

    EuTxManager mTxManager = new EuTxManager();

    int count = 1;
    boolean speakOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCountSpinner = findViewById(R.id.count_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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

        mLiveSwt = findViewById(R.id.live_swt);
        mSpeakText = findViewById(R.id.speakText);
        mSpeakBtn = findViewById(R.id.speakBtn);

        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speakOn) {
                    mTxManager.stop();
                    mSpeakBtn.setText("Speak :)");
                    speakOn = false;
                } else {
                    mTxManager.euInitTransmit(mSpeakText.getText().toString()); // To generate acoustic data "Hello, Euphony" for 5 times.
                    mTxManager.process(count);
                    mSpeakBtn.setText("Stop :(");
                    speakOn = true;
                }
            }
        });
    }
}
