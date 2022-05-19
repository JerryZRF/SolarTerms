package cf.jerryzrf.solarterms;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author JerryZRF
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("关于");
    }
}

