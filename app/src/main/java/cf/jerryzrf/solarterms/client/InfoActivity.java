package cf.jerryzrf.solarterms.client;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author JerryZRF
 */
public final class InfoActivity extends AppCompatActivity {

    int nowId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //左右滑动
        final float[] mPosX = {0};
        TextView content = findViewById(R.id.content);
        content.setLongClickable(true);
        content.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPosX[0] = event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getRawX() - mPosX[0] > 50) {
                        if (nowId != 0) {
                            display(nowId - 1);
                        }
                    } else if (mPosX[0] - event.getRawX() > 50) {
                        if (nowId != 3) {
                            display(nowId + 1);
                        }
                    }
                    BottomNavigationView bnv = findViewById(R.id.nav_view);
                    switch (nowId) {
                        case 0:
                            bnv.setSelectedItemId(R.id.item_introduction);
                            break;
                        case 1:
                            bnv.setSelectedItemId(R.id.item_xisu);
                            break;
                        case 2:
                            bnv.setSelectedItemId(R.id.item_yiji);
                            break;
                        case 3:
                            bnv.setSelectedItemId(R.id.item_photos);
                            break;
                        default:
                    }
                    break;
                default:
            }
            return true;
        });

        String st = getIntent().getStringExtra("st");
        JSONObject object;
        try {
            object = Json.getMainData(st);
            nowId = 0;
            setTitle(st + "的简介");
            content.setText(object.getString("introduction"));
            ((BottomNavigationView) findViewById(R.id.nav_view)).setOnItemSelectedListener((item) -> {
                switch (item.getItemId()) {
                    case R.id.item_introduction:
                        display(0);
                        break;
                    case R.id.item_xisu:
                        display(1);
                        break;
                    case R.id.item_yiji:
                        display(2);
                        break;
                    case R.id.item_photos:
                        display(3);
                    default:
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.crashByJson(this);
        }
    }

    private void display(int id) {
        String st = getIntent().getStringExtra("st");
        TextView content = findViewById(R.id.content);
        JSONObject object;
        nowId = id;
        try {
            object = Json.getMainData(st);
            switch (id) {
                case 0:
                    setTitle(st + "的简介");
                    content.setText(object.getString("introduction"));
                    break;
                case 1:
                    setTitle(st + "的习俗");
                    content.setText(object.getString("xisu"));
                    break;
                case 2:
                case R.id.item_yiji:
                    setTitle(st + "的宜忌");
                    content.setText(object.getString("yiji"));
                    break;
                case 3:
                    setTitle(st + "的美图");
                    content.setText(null);
                    break;
                default:
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}