package cf.jerryzrf.solarterms;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONObject;

import java.time.LocalDate;

/**
 * @author JerryZRF
 */
public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        int st = getIntent().getIntExtra("st", -1);
        JSONObject object;
        try {
            object = Json.getObject(LocalDate.now().getYear(), st);
            final String stName = object.getString("name");
            TextView content = findViewById(R.id.content);
            setTitle(stName + "的简介");
            content.setText(object.getString("introduction"));
            ((BottomNavigationView)findViewById(R.id.nav_view)).setOnNavigationItemSelectedListener((item) -> {
                try {
                    switch (item.getItemId()) {
                        case R.id.item_introduction:
                            setTitle(stName + "的简介");
                            content.setText(object.getString("introduction"));
                            break;
                        case R.id.item_xisu:
                            setTitle(stName + "的习俗");
                            content.setText(object.getString("xisu"));
                            break;
                        case R.id.item_yiji:
                            setTitle("当天的宜忌");
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