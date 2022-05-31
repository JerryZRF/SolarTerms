package cf.jerryzrf.solarterms.client;

import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("设置");
        ((Switch) findViewById(R.id.dateFormat)).setOnCheckedChangeListener((view, isChecked) -> {

        });
    }
}