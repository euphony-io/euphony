package co.euphony.sample;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    String[] mPermissions = {
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
    }

    private void requestPermission() {
        /* Request permission for audio */
        ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new
                ActivityResultContracts.RequestMultiplePermissions(), grantResults -> {
            for(Boolean result : grantResults.values()) {
                if (!result)
                    this.finish();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            multiplePermissionLauncher.launch(mPermissions);
        }
    }
}