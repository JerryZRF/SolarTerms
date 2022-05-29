package cf.jerryzrf.solarterms;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONObject;

/**
 * @author JerryZRF
 */
public final class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        String st = getIntent().getStringExtra("st");
        JSONObject object;
        try {
            object = Json.getMainConfig(st);
            TextView content = findViewById(R.id.content);
            setTitle(st + "的简介");
            content.setText(object.getString("introduction"));
            ((BottomNavigationView) findViewById(R.id.nav_view)).setOnItemSelectedListener((item) -> {
                try {
                    switch (item.getItemId()) {
                        case R.id.item_introduction:
                            setTitle(st + "的简介");
                            content.setText(object.getString("introduction"));
                            break;
                        case R.id.item_xisu:
                            setTitle(st + "的习俗");
                            content.setText(object.getString("xisu"));
                            break;
                        case R.id.item_yiji:
                            setTitle(st + "的宜忌");
                            content.setText(object.getString("yiji"));
                            break;
                        default:
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.crashByJson(this);
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.crashByJson(this);
        }
    }
}