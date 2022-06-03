package cf.jerryzrf.solarterms.client;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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