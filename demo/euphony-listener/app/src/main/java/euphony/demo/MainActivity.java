package euphony.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import euphony.lib.receiver.AcousticSensor;
import euphony.lib.receiver.EuRxManager;

public class MainActivity extends AppCompatActivity {

    boolean mode = false;
    TextView listenView;
    Button listenBtn;

    EuRxManager mRxManager = new EuRxManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenView = findViewById(R.id.listenView);
        listenBtn = findViewById(R.id.button);

        mRxManager.setAcousticSensor(new AcousticSensor() {
            @Override
            public void notify(String letters) {
                listenView.setText(letters);
            }
        });

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode) {
                    mRxManager.finish();
                    listenBtn.setText("Listen");
                    mode = false;
                } else {
                    mRxManager.listen();  //Listening Start
                    listenBtn.setText("Stop");
                    mode = true;
                }
            }
        });
    }
}
