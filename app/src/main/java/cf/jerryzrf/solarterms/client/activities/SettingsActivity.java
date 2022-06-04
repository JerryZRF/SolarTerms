package cf.jerryzrf.solarterms.client.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import cf.jerryzrf.solarterms.client.R;
import cf.jerryzrf.solarterms.client.SettingFragment;

/**
 * @author JerryZRF
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setTitle("设置");
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingFragment())
                    .commit();
        }
    }
}